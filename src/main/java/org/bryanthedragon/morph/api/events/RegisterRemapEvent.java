package org.bryanthedragon.morph.api.events;

import net.minecraftforge.eventbus.api.Event;

import java.util.HashMap;
import java.util.Map;

/**
 * Register remap event
 *
 * This event is fired when MorphCore is signaled to reload morph ID mappings.
 * During this event you can register names of morphs which you want
 * to be remapped to another names.
 */
public class RegisterRemapEvent extends Event
{
    /**
     * Map of which morph IDs should be remapped
     */
    public Map<String, String> map = new HashMap<String, String>();
}
