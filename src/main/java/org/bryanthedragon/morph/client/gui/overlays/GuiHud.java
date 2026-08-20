package bryanthedragon.morph.client.gui.overlays;

import net.minecraft.util.Mth;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.client.gui.overlay.ForgeGui;

public class GuiHud
{
    private static final ResourceLocation GUI_ICONS = new ResourceLocation("minecraft", "textures/gui/icons.png");

    /**
     * Whether or not squid air should be rendered
     * in place of regular player air.
     * Checked by RenderingHandler
     */
    public boolean renderSquidAir = false;
    public int squidAir = 300;

    public void renderSquidAir(GuiGraphics graphics, int width, int height)
    {
        if (squidAir < 300)
        {
            ForgeGui gui = (ForgeGui) Minecraft.getInstance().gui;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            int left = width / 2 + 91;
            int top = height - gui.rightHeight;

            int full = Mth.ceil((squidAir - 2) * 10.0F / 300.0F);
            int partial = Mth.ceil(squidAir * 10.0F / 300.0F) - full;

            for (int i = 0; i < full + partial; ++i)
            {
                graphics.blit(GUI_ICONS, left - i * 8 - 9, top, 0, (i < full ? 16 : 25), 9, 9);
            }

            gui.rightHeight += 10;
            RenderSystem.disableBlend();
        }
    }
}
