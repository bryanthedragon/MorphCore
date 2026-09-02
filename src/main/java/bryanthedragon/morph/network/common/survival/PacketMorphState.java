package bryanthedragon.morph.network.common.survival;

import bryanthedragon.morph.network.IMessage;

import net.minecraft.network.FriendlyByteBuf;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import bryanthedragon.morph.api.morphs.EntityMorph;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import net.minecraft.world.entity.player.Player;

public class PacketMorphState implements IMessage
{
    public int entityID = 0;
    public boolean hasSquidAir = true;
    public int squidAir = 300;

    public PacketMorphState()
    {}

    public PacketMorphState(Player player, IMorphing morphing)
    {
        if (morphing != null)
        {
            AbstractMorph morph = morphing.getCurrentMorph();

            if (morph instanceof EntityMorph)
            {
                entityID = ((EntityMorph) morph).getEntity(player.level()).getId();
            }

            this.hasSquidAir = morphing.getHasSquidAir();
            this.squidAir = morphing.getSquidAir();
        }
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf)
    {
        this.entityID = buf.readInt();
        this.hasSquidAir = buf.readBoolean();
        this.squidAir = buf.readInt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeInt(this.entityID);
        buf.writeBoolean(this.hasSquidAir);
        buf.writeInt(this.squidAir);
    }

}
