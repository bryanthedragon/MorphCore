package bryanthedragon.morph.network.morphlib.server;

import bryanthedragon.morph.MorphCore.MorphLib;
import bryanthedragon.morph.network.ServerMessageHandler;
import bryanthedragon.morph.network.morphlib.common.PacketDropItem;
import bryanthedragon.morph.utils.OpHelper;
import net.minecraft.entity.player.ServerPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;

public class ServerHandlerDropItem extends ServerMessageHandler<PacketDropItem>
{
    @Override
    public void run(ServerPlayer player, PacketDropItem message)
    {
        if (player.isCreative() && MorphLib.opDropItems.get() || OpHelper.isPlayerOp(player))
        {
            ItemStack stack = message.stack;

            player.inventory.addItemStackToInventory(stack);
            player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, 1F);
            player.inventoryContainer.detectAndSendChanges();
        }
    }
}