package org.bryanthedragon.morph.api.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Snow walk ability
 * 
 * This ability grants player snowy walk. This ability is really cool and 
 * probably only will be used for snow man morph.
 * 
 * Totally not taken from {@link SnowGolem#aiStep()}.
 */
public class SnowWalk extends Ability
{
    @Override
    public void update(LivingEntity target)
    {
        if (!target.onGround())
        {
            return;
        }

        int i = Mth.floor(target.getX());
        int j = Mth.floor(target.getY());
        int k = Mth.floor(target.getZ());

        BlockPos blockpos = new BlockPos(i, j, k);
        BlockState snow = Blocks.SNOW.defaultBlockState();

        if (target.level().isEmptyBlock(blockpos) && snow.canSurvive(target.level(), blockpos))
        {
            target.level().setBlockAndUpdate(blockpos, snow);
        }
    }
}
