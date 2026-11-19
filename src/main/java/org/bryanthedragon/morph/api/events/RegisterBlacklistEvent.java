package org.bryanthedragon.morph.api.events;

import java.util.Set;
import java.util.TreeSet;

import net.minecraftforge.eventbus.api.Event;


/**
 * Register blacklist event
 * 
 * This event is fired when MorphCore is signaled to reload blacklist. 
 * During this event you can register names of morphs which you want 
 * to be disabled. 
 */
public class RegisterBlacklistEvent extends Event
{
    /**
     * Blacklist. You should use this field to fill the morph names you 
     * want to disable.
     */
    public Set<String> blacklist = new TreeSet<String>();
}