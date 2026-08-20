package bryanthedragon.morph.client.gui.editor;

import bryanthedragon.morph.client.gui.framework.GuiBase;
import bryanthedragon.morph.client.gui.framework.elements.GuiElement;
import bryanthedragon.morph.client.gui.framework.elements.GuiModelRenderer;
import bryanthedragon.morph.client.gui.framework.elements.GuiPanelBase;
import bryanthedragon.morph.client.gui.framework.elements.buttons.GuiIconElement;
import bryanthedragon.morph.client.gui.framework.elements.input.GuiTextElement;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import bryanthedragon.morph.client.gui.utils.Elements;
import bryanthedragon.morph.client.gui.utils.Icon;
import bryanthedragon.morph.client.gui.utils.Icons;
import bryanthedragon.morph.client.gui.utils.Label;
import bryanthedragon.morph.client.gui.utils.keys.IKey;
import bryanthedragon.morph.utils.math.MathUtils;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.api.morphs.utils.Animation;
import bryanthedragon.morph.api.morphs.utils.IAnimationProvider;
import bryanthedragon.morph.bodypart.GuiBodyPartEditor;
import bryanthedragon.morph.client.gui.creative.GuiCreativeMorphsList;
import bryanthedragon.morph.client.gui.creative.GuiMorphRenderer;
import bryanthedragon.morph.utils.icons.IconUtils.MMIcons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings({"rawtypes", "unchecked"})
public class GuiAbstractMorph<T extends AbstractMorph> extends GuiPanelBase<GuiMorphPanel>
{
    public static final IKey KEY_CATEGORY = IKey.lang("morphcore.gui.editor.keys.category");

    public GuiCreativeMorphsList morphs;

    public GuiIconElement finish;
    public GuiModelRenderer renderer;
    public GuiSettingsPanel settings;

    protected GuiMorphPanel defaultPanel;

    public T morph;

    public GuiAbstractMorph(Minecraft mc)
    {
        super(mc);

        this.finish = new GuiIconElement(mc, Icons.CLOSE, (b) -> this.morphs.exit());
        this.finish.flex().relative(this).set(0, 0, 20, 20).y(1F, -20);
        this.renderer = this.createMorphRenderer(mc);
        this.renderer.flex().relative(this).wh(1F, 1F);
        this.defaultPanel = this.settings = new GuiSettingsPanel(mc, this);

        this.registerPanel(this.settings, IKey.lang("morphcore.gui.editor.settings"), MMIcons.PROPERTIES);
        this.prepend(this.renderer);

        this.add(this.finish);

        this.keys().register(IKey.lang("morphcore.gui.editor.keys.cycle"), GLFW.GLFW_KEY_TAB, this::cycle).category(KEY_CATEGORY);
    }

    protected void cycle()
    {
        int index = -1;

        for (int i = 0; i < this.panels.size(); i ++)
        {
            if (this.view.delegate == this.panels.get(i))
            {
                index = i;

                break;
            }
        }

        index += Screen.hasShiftDown() ? 1 : -1;
        index = MathUtils.cycler(index, 0, this.panels.size() - 1);

        this.buttons.elements.get(index).clickItself(GuiBase.getCurrent());
    }

    protected GuiModelRenderer createMorphRenderer(Minecraft mc)
    {
        return new GuiMorphRenderer(mc);
    }

    public void setMorphs(GuiCreativeMorphsList morphs)
    {
        this.morphs = morphs;
    }

    /**
     * Switch current morph panel to given one
     */
    @Override
    public void setPanel(GuiMorphPanel panel)
    {
        if (this.view.delegate != null)
        {
            this.view.delegate.finishEditing();
        }

        super.setPanel(panel);
        panel.startEditing();
    }

    public boolean canEdit(AbstractMorph morph)
    {
        return morph != null;
    }

    public void startEdit(T morph)
    {
        this.morph = morph;
        this.setupRenderer(morph);

        for (GuiMorphPanel panel : this.panels)
        {
            panel.fillData(morph);
        }

        this.setPanel(this.defaultPanel);
    }

    protected void setupRenderer(T morph)
    {
        this.renderer.reset();

        if (this.renderer instanceof GuiMorphRenderer)
        {
            ((GuiMorphRenderer) this.renderer).morph = morph;
        }
    }

    public void finishEdit()
    {
        if (this.view.delegate != null)
        {
            this.view.delegate.finishEditing();
        }
    }

    /**
     * Get presets
     */
    public List<Label<CompoundTag>> getPresets(T morph)
    {
        return Collections.emptyList();
    }

    protected void addPreset(AbstractMorph morph, List<Label<CompoundTag>> list, String label, String json)
    {
        try
        {
            this.addPreset(morph, list, label, TagParser.parseTag(json));
        }
        catch (CommandSyntaxException e)
        {
            throw new IllegalArgumentException("Invalid NBT in morph preset: " + label, e);
        }
    }

    protected void addPreset(AbstractMorph morph, List<Label<CompoundTag>> list, String label, CompoundTag tag)
    {
        CompoundTag morphTag = morph.toNBT();

        morphTag.merge(tag);
        list.add(new Label<CompoundTag>(IKey.str(label), morphTag));
    }

    /**
     * Get quick access editing fields
     */
    public List<GuiElement> getFields(Minecraft mc, GuiCreativeMorphsList morphs, T morph)
    {
        List<GuiElement> elements = new ArrayList<GuiElement>();
        GuiTextElement displayName = new GuiTextElement(mc, (name) ->
        {
            morphs.getSelected().displayName = name;
            morphs.markDirty();
        });

        displayName.setText(morph.displayName);
        elements.add(Elements.label(IKey.lang("morphcore.gui.editor.display_name")));
        elements.add(displayName);

        return elements;
    }

    /**
     * Get current tick for preview
     */
    public int getCurrentTick()
    {
        int tick = 0;

        if (this.morph instanceof IAnimationProvider)
        {
            Animation animation = ((IAnimationProvider) this.morph).getAnimation();

            if (animation.animates)
            {
                tick = animation.duration;
            }
        }

        return tick;
    }

    @Override
    public GuiIconElement registerPanel(GuiMorphPanel panel, IKey tooltip, Icon icon)
    {
        GuiIconElement button = super.registerPanel(panel, tooltip, icon);

        if (panel instanceof GuiBodyPartEditor)
        {
            this.registerKeybind(button, IKey.lang("morphcore.gui.body_parts.open"), GLFW.GLFW_KEY_B).category(KEY_CATEGORY);
        }

        return button;
    }

    @Override
    protected void drawBackground(GuiContext context, int x, int y, int w, int h)
    {
        context.graphics.fill(x, y, x + w, y + h, 0xee000000);
    }

    /* Saving and restoring the store */

    public void fromNBT(CompoundTag tag)
    {
        /* Restore model renderer's position */
        this.renderer.setPosition(tag.getFloat("MX"), tag.getFloat("MY"), tag.getFloat("MZ"));
        this.renderer.setScale(tag.getFloat("MS"));
        this.renderer.setRotation(tag.getFloat("MRY"), tag.getFloat("MRX"));

        /* Restore panel */
        int hash = tag.getInt("PanelHash");

        for (GuiMorphPanel panel : this.panels)
        {
            if (panel.hashCode() == hash)
            {
                this.setPanel(panel);
                panel.fromNBT(tag.getCompound("Panel"));

                return;
            }
        }
    }

    public CompoundTag toNBT()
    {
        CompoundTag tag = new CompoundTag();

        /* Save model renderer's position */
        tag.putFloat("MX", this.renderer.pos.x);
        tag.putFloat("MY", this.renderer.pos.y);
        tag.putFloat("MZ", this.renderer.pos.z);
        tag.putFloat("MS", this.renderer.scale);
        tag.putFloat("MRX", this.renderer.pitch);
        tag.putFloat("MRY", this.renderer.yaw);

        /* Save panel */
        tag.put("Panel", this.view.delegate.toNBT());
        tag.putInt("PanelHash", this.view.delegate.hashCode());

        return tag;
    }
}