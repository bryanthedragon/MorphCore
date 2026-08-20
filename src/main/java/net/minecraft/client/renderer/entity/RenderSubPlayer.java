package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.player.PlayerRenderer;

/**
 * Player renderer extension retained for compatibility with MorphCore hooks.
 */
public class RenderSubPlayer extends PlayerRenderer
{
    public RenderSubPlayer(EntityRendererProvider.Context context, boolean slim)
    {
        super(context, slim);
    }
}
