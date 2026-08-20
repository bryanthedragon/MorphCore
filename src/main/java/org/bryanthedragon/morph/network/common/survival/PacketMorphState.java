package bryanthedragon.morph.network.common.survival;

import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.api.morphs.main.EntityMorph;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.network.IMessage;

import io.netty.buffer.ByteBuf;

import net.minecraft.world.entity.player.Player;

public class PacketMorphState implements IMessage
{
    public int entityID = 0;
    public boolean hasSquidAir = true;
    public int squidAir = 300;

    public PacketMorphState()
    {
        
    }

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
    public void fromBytes(ByteBuf buf)
    {
        this.entityID = buf.readInt();
        this.hasSquidAir = buf.readBoolean();
        this.squidAir = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.entityID);
        buf.writeBoolean(this.hasSquidAir);
        buf.writeInt(this.squidAir);
    }

}
