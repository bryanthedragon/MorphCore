package org.bryanthedragon.morph.client.gui.creative;

import org.bryanthedragon.morph.client.gui.framework.elements.GuiElement;
import org.bryanthedragon.morph.client.gui.framework.elements.buttons.GuiButtonElement;
import org.bryanthedragon.morph.client.gui.framework.elements.buttons.GuiToggleElement;
import org.bryanthedragon.morph.client.gui.framework.elements.context.GuiSimpleContextMenu;
import org.bryanthedragon.morph.client.gui.framework.elements.input.GuiTextElement;
import org.bryanthedragon.morph.client.gui.framework.elements.list.GuiListElement;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiLabel;
import org.bryanthedragon.morph.client.gui.utils.Elements;
import org.bryanthedragon.morph.client.gui.utils.Icons;
import org.bryanthedragon.morph.client.gui.utils.keys.IKey;
import org.bryanthedragon.morph.utils.time.Timer;
import org.bryanthedragon.morph.MorphCoreProxy.ClientCoreProxy;
import org.bryanthedragon.morph.api.morphs.main.AbstractMorph;
import org.bryanthedragon.morph.capabilities.render.EntitySelector;
import org.bryanthedragon.morph.capabilities.render.ModelRenderer;
import org.bryanthedragon.morph.client.EntityModelHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.TagParser;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.List;
import java.util.function.Consumer;

public class GuiSelectorEditor extends GuiElement
{
    public GuiListElement<EntitySelector> selectors;

    public GuiElement form;
    public GuiTextElement name;
    public GuiTextElement type;
    public GuiTextElement match;
    public GuiToggleElement active;
    public GuiButtonElement pick;

    private EntitySelector selector;
    private Timer timer = new Timer(200);
    private boolean selecting;
    private boolean menu;

    public GuiSelectorEditor(Minecraft mc)
    {
        this(mc, false);
    }

    public GuiSelectorEditor(Minecraft mc, boolean menu)
    {
        super(mc);

        this.menu = menu;

        this.selectors = new GuiSelectorListElement(mc, this::fillData);
        this.selectors.sorting().background().setList(EntityModelHandler.selectors);
        this.selectors.context(() ->
        {
            GuiSimpleContextMenu contextMenu = new GuiSimpleContextMenu(mc).action(Icons.ADD, IKey.lang("morphcore.gui.selectors.add"), this::addSelector);

            if (!this.selectors.getCurrent().isEmpty())
            {
                contextMenu.action(Icons.REMOVE, IKey.lang("morphcore.gui.selectors.remove"), this::removeSelector);
            }

            return contextMenu;
        });

        this.form = new GuiElement(mc);
        this.name = new GuiTextElement(mc, 1000, (name) ->
        {
            this.selector.name = name;
            this.updateTime();
            this.timer.mark();
        });
        this.type = new GuiTextElement(mc, 1000, (name) ->
        {
            this.selector.type = name;
            this.updateTime();
            this.timer.mark();
        });
        this.match = new GuiTextElement(mc, 10000, (value) ->
        {
            try
            {
                this.selector.match = TagParser.parseTag(value);
                this.updateTime();
                this.timer.mark();
            }
            catch (CommandSyntaxException e)
            {
                this.selector.match = null;
                this.updateTime();
                this.timer.mark();
            }
        });
        this.match.tooltip(IKey.lang("morphcore.gui.selectors.match_tooltip"));
        this.active = new GuiToggleElement(mc, IKey.lang("morphcore.gui.selectors.enabled"), (toggle) ->
        {
            this.selector.enabled = toggle.isToggled();
            this.updateTime();
            this.timer.mark();
        });
        this.pick = new GuiButtonElement(mc, IKey.lang("morphcore.gui.body_parts.pick"), (button) ->
        {
            this.selecting = true;
            button.setEnabled(false);
        });

        this.form.flex().relative(this).w(1F).column(5).vertical().stretch().height(20).padding(10);
        this.selectors.flex().relative(this.form).y(1F).w(1F).hTo(this.flex(), 1F);

        GuiLabel title = Elements.label(IKey.lang("morphcore.gui.selectors.title"));
        GuiLabel name = Elements.label(IKey.lang("morphcore.gui.selectors.name"));
        GuiLabel type = Elements.label(IKey.lang("morphcore.gui.selectors.type"));
        GuiLabel match = Elements.label(IKey.lang("morphcore.gui.selectors.match"));

        name.marginTop(8);
        type.marginTop(8);
        match.marginTop(8);

        this.form.add(title.tooltip(IKey.lang("morphcore.gui.selectors.tooltip")), name, this.name, type, this.type, match, this.match, this.active);

        if (!this.menu)
        {
            this.form.add(this.pick);
        }

        this.markContainer().add(this.form, this.selectors);

        this.selectors.setIndex(0);
        this.fillData(this.selectors.getCurrent());
    }

    private void updateTime()
    {
        ModelRenderer.selectorsUpdate = System.currentTimeMillis();
    }

    public EntitySelector getSelector()
    {
        return this.selector;
    }

    private void addSelector()
    {
        EntityModelHandler.selectors.add(new EntitySelector());
        this.selectors.update();
        this.timer.mark();

        this.selectors.setIndex(this.selectors.getList().size() - 1);
        this.fillData(this.selectors.getCurrent());
    }

    private void removeSelector()
    {
        if (!this.selectors.current.isEmpty())
        {
            EntitySelector selector = this.selectors.getCurrent().get(0);

            selector.name = "";
            selector.type = "";
            selector.morph = null;
            this.updateTime();

            int current = this.selectors.current.get(0);

            EntityModelHandler.selectors.remove(current);
            this.selectors.setIndex(current - 1);
            this.fillData(this.selectors.getCurrent());
            this.selectors.update();
            this.timer.mark();
        }
    }

    private void fillData(List<EntitySelector> selectors)
    {
        this.selector = null;
        this.selecting = false;
        this.form.setVisible(!selectors.isEmpty());
        this.pick.setEnabled(true);

        if (selectors.isEmpty())
        {
            return;
        }

        EntitySelector selector = selectors.get(0);

        this.selector = selector;
        this.name.setText(selector.name);
        this.type.setText(selector.type);
        this.match.setText(selector.match == null ? "" : selector.match.toString());
        this.active.toggled(selector.enabled);
    }

    public void setMorph(AbstractMorph morph)
    {
        if (!this.isVisible() || this.selector == null)
        {
            return;
        }

        if (this.selecting || this.menu)
        {
            this.selector.morph = morph == null ? null : morph.toNBT();
        }

        this.pick.setEnabled(true);
        this.selecting = false;
        this.updateTime();
        this.timer.mark();
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.timer.checkReset())
        {
            ClientCoreProxy.models.saveSelectors();
        }

        this.area.draw(0xaa000000);

        super.draw(context);

        if (this.selectors.getList().isEmpty())
        {
            context.graphics.drawCenteredString(this.font, "Right click here...", this.selectors.area.mx(), this.selectors.area.my(), 0x888888);
        }
    }

    public static class GuiSelectorListElement extends GuiListElement<EntitySelector>
    {
        public GuiSelectorListElement(Minecraft mc, Consumer<List<EntitySelector>> callback)
        {
            super(mc, callback);

            this.scroll.scrollItemSize = 16;
        }

        @Override
        protected String elementToString(EntitySelector element)
        {
            return element.name + " (" + element.type + ") - " + (element.morph == null ? "null" : element.morph.getString("Name"));
        }
    }
}