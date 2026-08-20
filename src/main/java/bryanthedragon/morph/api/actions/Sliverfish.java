package bryanthedragon.morph.api.actions;

import bryanthedragon.morph.api.MorphAPI;
import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.api.morphs.main.BlockMorph;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;

/**
 * Silverfish action
 * 
 * Turns silverfish into immovable block at which it was looking
 */
public class Sliverfish implements IAction
{
    @Override
    public void execute(LivingEntity target, AbstractMorph morph)
    {
        if (target.level().isRemote)
        {
            return;
        }

        float reachDistance = 5;

        Vec3d pos = new Vec3d(target.posX, target.posY + target.getEyeHeight(), target.posZ);
        Vec3d look = target.getLook(1.0F);
        Vec3d vec = pos.addVector(look.x * reachDistance, look.y * reachDistance, look.z * reachDistance);

        RayTraceResult result = target.level().rayTraceBlocks(pos, vec, false, false, true);

        if (result != null && result.typeOfHit == Type.BLOCK && target instanceof Player)
        {
            BlockMorph block = new BlockMorph();

            block.blockPos = result.getBlockPos();
            block.block = target.level().getBlockState(block.blockPos);
            block.name = "morphcore.Block";

            target.level().setBlockToAir(block.blockPos);

            MorphAPI.morph((Player) target, block, true);
        }
    }
}