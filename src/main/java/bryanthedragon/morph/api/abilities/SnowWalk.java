package bryanthedragon.vanilla_pack.abilities;

import bryanthedragon.morph.api.abilities.Ability;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

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
        if (!target.onGround)
        {
            return;
        }

        int i = MathHelper.floor(target.posX);
        int j = MathHelper.floor(target.posY);
        int k = MathHelper.floor(target.posZ);

        BlockPos blockpos = new BlockPos(i, j, k);

        if (target.level().getBlockState(blockpos).getMaterial() == Material.AIR && Blocks.SNOW_LAYER.canPlaceBlockAt(target.level(), blockpos))
        {
            target.level().setBlockState(blockpos, Blocks.SNOW_LAYER.getDefaultState());
        }
    }
}