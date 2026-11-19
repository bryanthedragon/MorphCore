package org.bryanthedragon.morph.context;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

import org.bryanthedragon.morph.context.player.PlayerContext;

/**
 * Use {@link BlockPosContext} or {@link PlayerContext} when possible
 */
public interface IContext
{
    /**
     * World from where permission is requested. Can be null
     */
    @Nullable
    Level getWorld();

    /**
     * @return Player requesting permission. Can be null
     */
    @Nullable
    Player getPlayer();

    /**
     * @param key Context key
     * @return Context object
     */
    @Nullable
    <T> T get(ContextKey<T> key);

    /**
     * @param key Context key
     * @return true if context contains this key
     */
    boolean has(ContextKey<?> key);
}