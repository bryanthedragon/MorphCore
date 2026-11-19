package org.bryanthedragon.morph.entity;

import org.bryanthedragon.morph.MorphCore;
import org.bryanthedragon.morph.api.morphs.main.AbstractMorph;
import org.bryanthedragon.morph.capabilities.morphing.IMorphing;
import org.bryanthedragon.morph.capabilities.morphing.Morphing;
import org.bryanthedragon.morph.utils.obfuscated.ObfuscatedUtils.ObfuscatedName;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Replaces the sounds that players usually make when they are in morphs
 */
public class SoundHandler
{
    public static final ObfuscatedName GET_HURT_SOUND = new ObfuscatedName("func_184601_bQ" /* getHurtSound */);
    public static final ObfuscatedName GET_DEATH_SOUND = new ObfuscatedName("func_184615_bR" /* getDeathSound */);
    public static final ObfuscatedName PLAY_STEP_SOUND = new ObfuscatedName("func_180429_a" /* playStepSound */);

    public static final SoundEvent NO_SOUND = SoundEvent.createVariableRangeEvent(new ResourceLocation(MorphCore.MOD_ID, "no_sound"));

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerHurt(LivingAttackEvent event)
    {
        if (event.isCanceled())
        {
            return;
        }

        Entity entity = event.getEntity();
        if (!(entity instanceof Player))
        {
            return;
        }
        IMorphing morphing = Morphing.get((Player) entity);
        if (morphing == null)
        {
            return;
        }

        morphing.setLastDamageSource(event.getSource());
    }

    @SubscribeEvent
    public void onPlaySound(PlayLevelSoundEvent.AtEntity event)
    {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player))
        {
            return;
        }
        Player player = (Player) entity;
        IMorphing morphing = Morphing.get(player);
        if (morphing == null)
        {
            return;
        }
        AbstractMorph morph = morphing.getCurrentMorph();
        if (morph == null)
        {
            return;
        }

        SoundEvent sound = event.getSound().value();
        ResourceLocation soundResource = sound.getLocation();
        String soundType = soundResource.getPath();
        
        if (soundType.endsWith(".hurt"))
        {
            SoundEvent newSound = morph.getHurtSound(player, morphing.getLastDamageSource());
            if (newSound == NO_SOUND)
            {
                event.setNewVolume(0);
            }
            else if (newSound != null)
            {
                event.setSound(Holder.direct(newSound));
            }
        }
        else if (soundType.endsWith(".death"))
        {
            SoundEvent newSound = morph.getDeathSound(player);
            if (newSound == NO_SOUND)
            {
                event.setNewVolume(0);
            }
            else if (newSound != null)
            {
                event.setSound(Holder.direct(newSound));
            }
        }
        else if (soundType.endsWith(".step"))
        {
            if (player.getBbWidth() != morph.getWidth(player) || player.getBbHeight() != morph.getHeight(player))
            {
                /*
                 * Check if the player's current hitbox does not
                 * match the morph hitbox.
                 * 
                 * Each tick, the player entity attempts to revert
                 * back to the default player hitbox. If the current
                 * morph hitbox is smaller than the player hitbox,
                 * movement can be triggered, which in turn triggers
                 * a step sound.
                 */
                event.setNewVolume(0);
            }
            else if (morph.hasCustomStepSound(player))
            {
                event.setNewVolume(0);
                morph.playStepSound(player);
            }
        }
    }
}
