package bryanthedragon.morph.api.creative.sections;

import bryanthedragon.morph.api.creative.categories.MorphCategory;
import bryanthedragon.morph.client.gui.creative.GuiCreativeMorphsList;
import bryanthedragon.morph.client.gui.creative.GuiMorphSection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MorphSection
{
    public String title;
    public List<MorphCategory> categories = new ArrayList<MorphCategory>();
    public boolean hidden;

    public MorphSection(String title)
    {
        this.title = title;
    }

    @OnlyIn(Dist.CLIENT)
    public String getTitle()
    {
        return I18n.format("morph.section." + this.title);
    }

    public void add(MorphCategory category)
    {
        this.categories.add(category);
    }

    public void remove(MorphCategory category)
    {
        this.categories.remove(category);
    }

    /**
     * This method gets called when a new morph picker appears
     */
    public void update(Level world)
    {

    }

    /**
     * This method gets called when player exits to the main menu
     */
    public void reset()
    {
        
    }

    @OnlyIn(Dist.CLIENT)
    public GuiMorphSection getGUI(Minecraft mc, GuiCreativeMorphsList parent, Consumer<GuiMorphSection> callback)
    {
        return new GuiMorphSection(mc, parent, this, callback);
    }

    public boolean keyTyped(Player player, int keycode)
    {
        for (MorphCategory category : this.categories)
        {
            if (category.keyTyped(player, keycode))
            {
                return true;
            }
        }

        return false;
    }
}