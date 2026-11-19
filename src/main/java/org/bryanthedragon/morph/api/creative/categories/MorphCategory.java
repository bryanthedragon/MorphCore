package org.bryanthedragon.morph.api.creative.categories;

import org.bryanthedragon.morph.MorphCore;
import org.bryanthedragon.morph.api.MorphManager;
import org.bryanthedragon.morph.api.creative.sections.MorphSection;
import org.bryanthedragon.morph.api.morphs.main.AbstractMorph;
import org.bryanthedragon.morph.network.Dispatcher;
import org.bryanthedragon.morph.network.common.creative.PacketMorph;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MorphCategory
{
    public MorphSection parent;

    public String title;
    public boolean hidden;
    protected List<AbstractMorph> morphs = new ArrayList<AbstractMorph>();

    public MorphCategory(MorphSection parent, String title)
    {
        this.parent = parent;
        this.title = title;
    }

    @OnlyIn(Dist.CLIENT)
    public String getTitle()
    {
        return I18n.get("morph.category." + this.title);
    }

    public List<AbstractMorph> getMorphs()
    {
        return this.morphs;
    }

    public boolean isHidden()
    {
        return this.morphs.isEmpty() || this.hidden;
    }

    public AbstractMorph getEqual(AbstractMorph morph)
    {
        for (AbstractMorph child : this.morphs)
        {
            if (child.equals(morph))
            {
                return child;
            }
        }

        return null;
    }

    public void clear()
    {
        this.morphs.clear();
    }

    public void sort()
    {
        Collections.sort(this.morphs, (a, b) -> a.name.compareToIgnoreCase(b.name));
    }

    public final void add(AbstractMorph morph)
    {
        if (MorphManager.isBlacklisted(morph.name))
        {
            return;
        }

        MorphManager.INSTANCE.applySettings(morph);

        this.addMorph(morph);
    }

    protected void addMorph(AbstractMorph morph)
    {
        this.morphs.add(morph);
    }

    public boolean isEditable(AbstractMorph morph)
    {
        return false;
    }

    public void edit(AbstractMorph morph)
    {}

    public boolean remove(AbstractMorph morph)
    {
        return this.morphs.remove(morph);
    }

    public boolean keyTyped(Player player, int keycode)
    {
        for (AbstractMorph morph : this.morphs)
        {
            if (morph.keybind == keycode && this.morph(player, morph))
            {
                return true;
            }
        }

        return false;
    }

    protected boolean morph(Player player, AbstractMorph morph)
    {
        if (MorphCore.proxy.canUse(player))
        {
            Dispatcher.sendToServer(new PacketMorph(morph));

            return true;
        }

        return false;
    }
}
