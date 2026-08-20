package bryanthedragon.morph.api.events;

import java.util.HashMap;
import java.util.Map;

import bryanthedragon.morph.api.settings.MorphSettings;

import net.minecraftforge.eventbus.api.Event;

/**
 * Register settings event
 * 
 * This event should be fired when MorphCore reloads morph settings 
 * (this happens once the server is starting up or when admin executes 
 * "/MorphCore reload settings" command).
 * 
 * Use this event to add your own custom morph settings.  
 */
public class RegisterSettingsEvent extends Event
{
    public Map<String, MorphSettings> settings = new HashMap<String, MorphSettings>();
}