package bryanthedragon.morph.api.actions;

import bryanthedragon.morph.api.MorphAPI;
import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.api.morphs.main.BlockMorph;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;

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
        if (target.level().isClientSide())
        {
            return;
        }

        float reachDistance = 5;

        Vec3 pos = new Vec3(target.getX(), target.getY() + target.getEyeHeight(), target.getZ());
        Vec3 look = target.getLookAngle();
        Vec3 vec = pos.add(look.x * reachDistance, look.y * reachDistance, look.z * reachDistance);

        HitResult result = target.level().clip(new ClipContext(pos, vec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, target));

        if (result != null && result.getType() == HitResult.Type.BLOCK && target instanceof Player)
        {
            BlockMorph block = new BlockMorph();

            block.blockPos = ((BlockHitResult) result).getBlockPos();
            block.block = target.level().getBlockState(block.blockPos);
            block.name = "morphcore.Block";

            target.level().setBlockAndUpdate(block.blockPos, Blocks.AIR.defaultBlockState());

            MorphAPI.morph((Player) target, block, true);
        }
    }
}