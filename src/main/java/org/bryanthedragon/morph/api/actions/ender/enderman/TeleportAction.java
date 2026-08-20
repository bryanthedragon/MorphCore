package bryanthedragon.morph.api.actions.ender.enderman;

import javax.annotation.Nullable;

import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

/**
 * Teleport action
 * 
 * This action will teleport given player there where he's looking.
 * 
 * If the point where player is about to teleport have a block above, it will 
 * teleport player beside the block. Optionally, you can sneak to teleport 
 * beside the block.
 * 
 * Teleport action also has cooldown and limited distance to teleport in 
 * radius of 32 blocks.
 */
public class TeleportAction implements IAction
{
    @Override
    public void execute(LivingEntity target, @Nullable AbstractMorph morph)
    {
        float reachDistance = 32;

        Vec3 pos = new Vec3(target.getX(), target.getY() + target.getEyeHeight(), target.getZ());
        Vec3 look = target.getLookAngle();
        Vec3 vec = pos.add(look.x * reachDistance, look.y * reachDistance, look.z * reachDistance);

        HitResult result = target.level().clip(new net.minecraft.world.level.ClipContext(pos, vec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, target));

        if (result != null && result.getType() == Type.BLOCK)
        {
            BlockPos block = ((BlockHitResult) result).getBlockPos();

            if (target instanceof Player && ((Player) target).getAttackStrengthScale(0.0F) < 1)
            {
                return;
            }

            if (target.isShiftKeyDown() || !target.level().getBlockState(block.above()).getBlock().equals(Blocks.AIR))
            {
                block = block.relative(((BlockHitResult) result).getDirection());
            }

            double x = block.getX() + 0.5F;
            double y = block.getY() + 1.0F;
            double z = block.getZ() + 0.5F;

            target.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0F, 1.0F);
            target.teleportTo(x, y, z);

            if (target instanceof Player)
            {
                ((Player) target).resetAttackStrengthTicker();
            }

            target.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0F, 1.0F);
        }
    }
}