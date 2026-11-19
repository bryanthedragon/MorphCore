package org.bryanthedragon.morph.events;

import net.minecraft.client.Minecraft;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
@OnlyIn(Dist.CLIENT)
public abstract class RenderOverlayEvent extends Event
{
    public final Minecraft mc;
    public final int width;
    public final int height;

    public RenderOverlayEvent(Minecraft mc, int width, int height)
    {
        this.mc = mc;
        this.width = width;
        this.height = height;
    }

    public static class Pre extends RenderOverlayEvent
    {
        public Pre(Minecraft mc, int width, int height)
        {
            super(mc, width, height);
        }
    }

    public static class Post extends RenderOverlayEvent
    {
        public Post(Minecraft mc, int width, int height)
        {
            super(mc, width, height);
        }
    }
}