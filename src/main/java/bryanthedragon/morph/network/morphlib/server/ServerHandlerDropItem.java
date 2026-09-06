package bryanthedragon.morph.network.morphlib.server;

import bryanthedragon.morph.MorphCore.MorphLib;
import bryanthedragon.morph.network.ServerMessageHandler;
import bryanthedragon.morph.network.morphlib.common.PacketDropItem;
import bryanthedragon.morph.utils.OpHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

public class ServerHandlerDropItem extends ServerMessageHandler<PacketDropItem>
{
    @Override
    public void run(ServerPlayer player, PacketDropItem message)
    {
        if (player.isCreative() && MorphLib.opDropItems.get() || OpHelper.isPlayerOp(player))
        {
            ItemStack stack = message.stack;

            player.getInventory().add(stack);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, 1F);
            player.inventoryMenu.broadcastChanges();
        }
    }
}