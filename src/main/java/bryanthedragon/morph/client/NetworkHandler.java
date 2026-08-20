package bryanthedragon.morph.client;

import java.nio.charset.Charset;

import bryanthedragon.morph.api.MorphManager;
import bryanthedragon.morph.api.MorphSettings;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

/**
 * Custom payload network hander
 * 
 * This network handler allows plugins to give morphs to players
 */
@OnlyIn(Dist.CLIENT)
public class NetworkHandler
{
    /**
     * This handler handles custom payload messages 
     */
    public void handle(FriendlyByteBuf buffer, NetworkEvent.Context ctx)
    {
        try
        {
            byte[] array = new byte[buffer.capacity()];

            for (int i = 0, c = array.length; i < c; i++)
            {
                array[i] = buffer.readByte();
            }

            String data = new String(array, Charset.forName("UTF-8")).trim();
            String[] args = data.split(" ");

            if (args.length >= 1)
            {
                Minecraft.getInstance().execute(new MorphRunnable(args));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Morph runnable
     * 
     * This class is responsible for morphing a player into a morph or 
     * demorph him based on the arguments passed in the payload packet.
     */
    public static class MorphRunnable implements Runnable
    {
        public String[] args;

        public MorphRunnable(String[] args)
        {
            this.args = args;
        }

        @Override
        public void run()
        {
            Player player = Minecraft.getInstance().level.getPlayerByUUID(java.util.UUID.fromString(this.args[0]));

            if (player == null)
            {
                return;
            }

            if (this.args.length >= 3)
            {
                String data = this.args[2];

                for (int i = 3; i < this.args.length; i++)
                {
                    data += " " + this.args[i];
                }

                try
                {
                    CompoundTag tag = TagParser.parseTag(data);
                    tag.putString("Name", this.args[1]);
                    this.morph(tag, player);
                }
                catch (Exception e)
                {}
            }
            else if (this.args.length == 2)
            {
                CompoundTag tag = new CompoundTag();
                tag.putString("Name", this.args[1]);
                this.morph(tag, player);
            }
            else if (this.args.length == 1)
            {
                Morphing.get(player).setCurrentMorph(null, player, true);
            }
        }

        private void morph(CompoundTag tag, Player player)
        {
            AbstractMorph morph = MorphManager.INSTANCE.morphFromNBT(tag);

            if (morph != null)
            {
                /* No fancy stuff or actions */
                morph.forceSettings(MorphSettings.DEFAULT);

                Morphing.get(player).setCurrentMorph(morph, player, true);
            }
        }
    }
}