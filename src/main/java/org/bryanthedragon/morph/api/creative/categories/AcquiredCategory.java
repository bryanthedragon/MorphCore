package org.bryanthedragon.morph.api.creative.categories;

import org.bryanthedragon.morph.api.creative.sections.MorphSection;
import org.bryanthedragon.morph.api.morphs.main.AbstractMorph;
import org.bryanthedragon.morph.network.Dispatcher;
import org.bryanthedragon.morph.network.common.creative.PacketAcquireMorph;
import org.bryanthedragon.morph.network.common.creative.PacketClearAcquired;
import org.bryanthedragon.morph.network.common.creative.PacketSyncMorph;
import org.bryanthedragon.morph.network.common.survival.PacketRemoveMorph;
import org.bryanthedragon.morph.network.common.survival.PacketSelectMorph;

import net.minecraft.world.entity.player.Player;

import java.util.List;

public class AcquiredCategory extends MorphCategory
{
    public AcquiredCategory(MorphSection parent, String title)
    {
        super(parent, title);
    }

    public void setMorphs(List<AbstractMorph> morphs)
    {
        this.morphs = morphs;
    }

    @Override
    public void clear()
    {
        super.clear();

        Dispatcher.sendToServer(new PacketClearAcquired());
    }

    @Override
    protected void addMorph(AbstractMorph morph)
    {
        super.addMorph(morph);

        Dispatcher.sendToServer(new PacketAcquireMorph(morph, false));
    }

    @Override
    public boolean isEditable(AbstractMorph morph)
    {
        return this.morphs.indexOf(morph) != -1;
    }

    @Override
    public void edit(AbstractMorph morph)
    {
        int index = this.morphs.indexOf(morph);

        if (index >= 0)
        {
            Dispatcher.sendToServer(new PacketSyncMorph(morph, index));
        }
    }

    @Override
    public boolean remove(AbstractMorph morph)
    {
        int index = this.morphs.indexOf(morph);
        boolean has = index != -1;

        if (has)
        {
            Dispatcher.sendToServer(new PacketRemoveMorph(index));
        }

        return has;
    }

    @Override
    protected boolean morph(Player player, AbstractMorph morph)
    {
        int index = this.morphs.indexOf(morph);

        if (index >= 0)
        {
            Dispatcher.sendToServer(new PacketSelectMorph(index));
        }

        return true;
    }
}