package bryanthedragon.morph.client.gui.framework.elements.buttons;

import bryanthedragon.morph.MorphLib;
import bryanthedragon.morph.client.gui.framework.GuiBase;
import bryanthedragon.morph.client.gui.framework.elements.context.GuiContextMenu;
import bryanthedragon.morph.client.gui.framework.elements.context.GuiSimpleContextMenu;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiDraw;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiInventoryElement;
import bryanthedragon.morph.client.gui.utils.Area;
import bryanthedragon.morph.client.gui.utils.Icons;
import bryanthedragon.morph.client.gui.utils.keys.IKey;
import bryanthedragon.morph.network.morphlib.Dispatcher;
import bryanthedragon.morph.network.morphlib.common.PacketDropItem;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class GuiSlotElement extends GuiClickElement<ItemStack>
{
    public static final ResourceLocation SHIELD = new ResourceLocation("minecraft", "textures/item/empty_armor_slot_shield.png");
    public static final ResourceLocation BOOTS = new ResourceLocation("minecraft", "textures/item/empty_armor_slot_boots.png");
    public static final ResourceLocation LEGGINGS = new ResourceLocation("minecraft", "textures/item/empty_armor_slot_leggings.png");
    public static final ResourceLocation CHESTPLATE = new ResourceLocation("minecraft", "textures/item/empty_armor_slot_chestplate.png");
    public static final ResourceLocation HELMET = new ResourceLocation("minecraft", "textures/item/empty_armor_slot_helmet.png");

    public GuiInventoryElement inventory;
    public final int slot;

    private ItemStack stack = ItemStack.EMPTY;

    public boolean drawDisabled = true;
    public int lastSlot;

    public GuiSlotElement(Minecraft mc, int slot, Consumer<ItemStack> callback)
    {
        super(mc, callback);

        this.slot = slot;
        this.inventory = new GuiInventoryElement(mc, this);

        this.flex().wh(24, 24);
    }

    public ItemStack getStack()
    {
        return this.stack;
    }

    public void setStack(ItemStack stack)
    {
        this.lastSlot = -1;
        this.stack = stack.copy();

        if (this.inventory.hasParent())
        {
            this.inventory.updateInventory();
        }
    }

    public void acceptStack(ItemStack stack, int slot)
    {
        this.lastSlot = slot;
        this.stack = stack.copy();

        if (this.callback != null)
        {
            this.callback.accept(stack);
        }
    }

    @Override
    public GuiContextMenu createContextMenu(GuiContext context)
    {
        if (this.contextMenu == null)
        {
            return this.createDefaultSlotContextMenu();
        }

        return super.createContextMenu(context);
    }

    public GuiSimpleContextMenu createDefaultSlotContextMenu()
    {
        GuiSimpleContextMenu menu = new GuiSimpleContextMenu(this.mc).action(Icons.COPY, IKey.lang("morphlib.gui.item_slot.context.copy"), this::copyNBT);

        try
        {
            ItemStack stack = ItemStack.of(TagParser.parseTag(this.mc.keyboardHandler.getClipboard()));

            if (!stack.isEmpty())
            {
                menu.action(Icons.PASTE, IKey.lang("morphlib.gui.item_slot.context.paste"), () -> this.pasteItem(stack));
            }
        }
        catch (CommandSyntaxException e)
        {}

        return menu
            .action(Icons.DOWNLOAD, IKey.lang("morphlib.gui.item_slot.context.drop"), this::dropItem)
            .action(Icons.CLOSE, IKey.lang("morphlib.gui.item_slot.context.clear"), this::clearItem);
    }

    private void copyNBT()
    {
        if (!this.stack.isEmpty())
        {
            this.mc.keyboardHandler.setClipboard(this.stack.save(new CompoundTag()).toString());
        }
    }

    private void pasteItem(ItemStack stack)
    {
        this.acceptStack(stack, -1);
    }

    private void dropItem()
    {
        if (!this.stack.isEmpty())
        {
            Dispatcher.sendToServer(new PacketDropItem(this.stack));
        }
    }

    private void clearItem()
    {
        this.acceptStack(ItemStack.EMPTY, -1);
    }

    @Override
    protected void click(int mouseButton)
    {
        this.inventory.removeFromParent();

        GuiContext context = GuiBase.getCurrent();

        this.inventory.flex().relative(context.screen.root).xy(0.5F, 0.5F).anchor(0.5F, 0.5F);
        this.inventory.resize();
        this.inventory.updateInventory();

        context.screen.root.add(this.inventory);
    }

    @Override
    protected ItemStack get()
    {
        return this.stack;
    }

    @Override
    protected void drawSkin(GuiContext context)
    {
        int border = this.inventory.hasParent() ? 0xff000000 + MorphLib.primaryColor.get() : 0xffffffff;

        if (MorphLib.enableBorders.get())
        {
            context.graphics.fill(this.area.x + 1, this.area.y, this.area.ex() - 1, this.area.ey(), 0xff000000);
            context.graphics.fill(this.area.x, this.area.y + 1, this.area.ex(), this.area.ey() - 1, 0xff000000);
            context.graphics.fill(this.area.x + 1, this.area.y + 1, this.area.ex() - 1, this.area.ey() - 1, border);
            context.graphics.fill(this.area.x + 2, this.area.y + 2, this.area.ex() - 2, this.area.ey() - 2, 0xffc6c6c6);
        }
        else
        {
            context.graphics.fill(this.area.x, this.area.y, this.area.ex(), this.area.ey(), border);
            context.graphics.fill(this.area.x + 1, this.area.y + 1, this.area.ex() - 1, this.area.ey() - 1, 0xffc6c6c6);
        }

        int x = this.area.mx() - 8;
        int y = this.area.my() - 8;

        if (this.stack.isEmpty() && this.slot != 0)
        {
            ResourceLocation placeholder = switch (this.slot)
            {
                case 1 -> SHIELD;
                case 2 -> BOOTS;
                case 3 -> LEGGINGS;
                case 4 -> CHESTPLATE;
                case 5 -> HELMET;
                default -> null;
            };

            if (placeholder != null)
            {
                context.graphics.blit(placeholder, x, y, 0, 0, 16, 16, 16, 16);
            }
        }
        else
        {
            context.graphics.renderItem(this.stack, x, y);
            context.graphics.renderItemDecorations(context.font, this.stack, x, y);

            if (this.area.isInside(context))
            {
                context.tooltip.set(context, this);
            }
        }

        if (this.drawDisabled)
        {
            GuiDraw.drawLockedArea(this, MorphLib.enableBorders.get() ? 1 : 0);
        }
    }

    @Override
    public void drawTooltip(GuiContext context, Area area)
    {
        super.drawTooltip(context, area);

        context.graphics.renderTooltip(context.font, this.stack, context.mouseX, context.mouseY);
    }
}