package bryanthedragon.morph.client.gui.creative;

import bryanthedragon.morph.client.gui.framework.elements.context.GuiContextMenu;
import bryanthedragon.morph.client.gui.framework.elements.context.GuiSimpleContextMenu;
import bryanthedragon.morph.client.gui.framework.elements.modals.GuiConfirmModal;
import bryanthedragon.morph.client.gui.framework.elements.modals.GuiModal;
import bryanthedragon.morph.client.gui.framework.elements.modals.GuiPromptModal;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import bryanthedragon.morph.client.gui.utils.Icons;
import bryanthedragon.morph.client.gui.utils.keys.IKey;
import bryanthedragon.morph.api.MorphManager;
import bryanthedragon.morph.api.creative.categories.AcquiredCategory;
import bryanthedragon.morph.api.creative.categories.MorphCategory;
import bryanthedragon.morph.api.creative.categories.RecentCategory;
import bryanthedragon.morph.api.creative.categories.UserCategory;
import bryanthedragon.morph.api.creative.sections.MorphSection;
import bryanthedragon.morph.api.creative.sections.UserSection;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Screen;
import net.minecraft.nbt.JsonToNBT;

import java.util.function.Consumer;

public class GuiUserSection extends GuiMorphSection
{
    public GuiUserSection(Minecraft mc, GuiCreativeMorphsList parent, MorphSection section, Consumer<GuiMorphSection> callback)
    {
        super(mc, parent, section, callback);
    }

    @Override
    public GuiContextMenu createContextMenu(GuiContext context)
    {
        GuiSimpleContextMenu contextMenu = (GuiSimpleContextMenu) super.createContextMenu(context);

        if (this.parent == null)
        {
            return contextMenu;
        }

        MorphCategory category = this.hoverCategory;
        AbstractMorph morph = this.hoverMorph;

        boolean user = category instanceof UserCategory;
        boolean acquired = category instanceof AcquiredCategory;
        boolean recent = category instanceof RecentCategory;

        if (category != null)
        {
            contextMenu.action(Icons.PASTE, IKey.lang("morphcore.gui.creative.context.paste"), () -> this.pasteMorph(category));
        }

        if (morph != null && (user || acquired))
        {
            contextMenu.action(Icons.REFRESH, IKey.lang("morphcore.gui.creative.context.to_recent"), () -> this.copyToRecent(morph));
        }

        contextMenu.action(Icons.ADD, IKey.lang("morphcore.gui.creative.context.add_category"), () -> section.add(new UserCategory(this.section, "User category")));

        if (user)
        {
            contextMenu.action(Icons.EDIT, IKey.lang("morphcore.gui.creative.context.rename_category"), () -> this.renameCategory(category));
            contextMenu.action(Icons.CLOSE, IKey.lang("morphcore.gui.creative.context.remove_category"), () -> this.removeCategory(category));
        }

        if (recent || acquired)
        {
            contextMenu.action(Icons.CLOSE, IKey.lang("morphcore.gui.creative.context.clear_category"), () -> this.clearCategory(category));
        }

        if (morph != null && category != null)
        {
            contextMenu.action(Icons.CLOSE, IKey.lang("morphcore.gui.creative.context.remove_morph"), () -> this.removeMorph(category, morph));
        }

        return contextMenu;
    }

    private void pasteMorph(MorphCategory category)
    {
        String clipboard = Screen.getClipboardString();

        if (!Screen.hasControlDown())
        {
            try
            {
                category.add(MorphManager.INSTANCE.morphFromNBT(JsonToNBT.getTagFromJson(clipboard)));

                return;
            }
            catch (Exception e)
            {}
        }

        GuiModal.addFullModal(this.parent, () ->
        {
            GuiPromptModal modal = new GuiPromptModal(this.mc, IKey.lang("morphcore.gui.creative.context.paste_modal"), (string) ->
            {
                try
                {
                    category.add(MorphManager.INSTANCE.morphFromNBT(JsonToNBT.getTagFromJson(string)));
                }
                catch (Exception e)
                {}
            });

            modal.text.field.setMaxStringLength(100000);
            modal.setValue(clipboard);

            return modal;
        });
    }

    private void renameCategory(MorphCategory category)
    {
        GuiModal.addModal(this.parent, () ->
        {
            GuiPromptModal modal = new GuiPromptModal(this.mc, IKey.lang("morphcore.gui.creative.context.rename_category_modal"), (string) ->
            {
                category.title = string;
                ((UserSection) this.section).save();
            });

            modal.setValue(category.getTitle());
            modal.flex().relative(this.parent).xy(0.5F, 0.5F).wh(160, 180).anchor(0.5F, 0.5F);

            return modal;
        });
    }

    private void removeCategory(MorphCategory category)
    {
        GuiModal.addModal(this.parent, () ->
        {
            return GuiConfirmModal.createTemplate(this.mc, this.parent, IKey.lang("morphcore.gui.creative.context.remove_category_modal"), (value) ->
            {
                if (value)
                {
                    this.section.remove(category);
                }
            });
        });
    }

    private void removeMorph(MorphCategory category, AbstractMorph morph)
    {
        GuiModal.addModal(this.parent, () ->
        {
            return GuiConfirmModal.createTemplate(this.mc, this.parent, IKey.lang("morphcore.gui.creative.context.remove_morph_modal"), (value) ->
            {
                if (value)
                {
                    category.remove(morph);
                }
            });
        });
    }

    private void clearCategory(MorphCategory category)
    {
        GuiModal.addModal(this.parent, () ->
        {
            GuiConfirmModal modal = new GuiConfirmModal(this.mc, IKey.lang("morphcore.gui.creative.context.clear_category_modal"), (value) ->
            {
                if (value)
                {
                    category.clear();
                }
            });

            modal.flex().relative(this.parent).xy(0.5F, 0.5F).wh(160, 180).anchor(0.5F, 0.5F);

            return modal;
        });
    }

    private void copyToRecent(AbstractMorph morph)
    {
        ((UserSection) this.section).recent.add(morph.copy());
    }
}