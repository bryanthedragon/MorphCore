package bryanthedragon.morph.api.abilities;

import bryanthedragon.morph.api.abilities.Ability;

import net.minecraft.block.material.Material;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

/**
 * Snow walk ability
 * 
 * This ability grants player snowy walk. This ability is really cool and 
 * probably only will be used for snow man morph.
 * 
 * Totally not taken from {@link EntitySnowman#onLivingUpdate()}.
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

        if (target.level().getBlockState(blockpos).getMaterial() == Material.AIR && Blocks.SNOW_LAYER.canPlaceBlockAt(target.level(), blockpos))
        {
            target.level().setBlockState(blockpos, Blocks.SNOW_LAYER.getDefaultState());
        }
    }
}