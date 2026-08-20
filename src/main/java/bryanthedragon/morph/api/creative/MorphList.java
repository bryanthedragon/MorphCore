package bryanthedragon.morph.api.creative;

import bryanthedragon.morph.api.creative.sections.MorphSection;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Morph list
 */
public class MorphList
{
    /**
     * Morph sections
     */
    public List<MorphSection> sections = new ArrayList<MorphSection>();

    /**
     * Add a morph section to this list
     */
    public void register(MorphSection section)
    {
        this.sections.add(section);
    }

    /**
     * This method gets called when a new morph picker appears
     */
    public void update(Level world)
    {
        for (MorphSection section : this.sections)
        {
            section.update(world);
        }
    }

    /**
     * This method gets called when player exits to the main menu
     */
    public void reset()
    {
        for (MorphSection section : this.sections)
        {
            section.reset();
        }
    }

    /**
     * If any of the morphs have keybind attached, use it
     */
    public boolean keyTyped(Player player, int keycode)
    {
        for (MorphSection section : this.sections)
        {
            if (section.keyTyped(player, keycode))
            {
                return true;
            }
        }

        return false;
    }

    public <T> T getSection(Class<T> clazz)
    {
        for (MorphSection section : this.sections)
        {
            if (section.getClass() == clazz)
            {
                return clazz.cast(section);
            }
        }

        return null;
    }
}