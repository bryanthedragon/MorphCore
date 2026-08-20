package bryanthedragon.vanilla_pack.actions;

import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.morphs.AbstractMorph;
import bryanthedragon.morph.api.morphs.EntityMorph;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class Spit implements IAction
{
    @Override
    public void execute(LivingEntity target, AbstractMorph morph)
    {
        if (target.level().isRemote)
        {
            return;
        }

        EntityLlamaSpit spit = new EntityLlamaSpit(target.level());

        Vec3d vec3d = target.getLook(1.0F);

        double d1 = 4.0D;
        double d2 = vec3d.x;
        double d3 = vec3d.y;
        double d4 = vec3d.z;

        spit.setPosition(target.posX + d2 * 1.5, target.posY + target.height * 0.8F, target.posZ + d4 * 1.5);
        spit.shoot(d2, d3, d4, (float) 1.0F, 1.0F);

        if (morph instanceof EntityMorph)
        {
            LivingEntity entity = ((EntityMorph) morph).getEntity();

            if (entity instanceof EntityLlama)
            {
                spit.owner = (EntityLlama) entity;
            }
        }

        target.level().playSound((Player) null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_LLAMA_SPIT, target.getSoundCategory(), 1.0F, 1.0F + (target.getRNG().nextFloat() - target.getRNG().nextFloat()) * 0.2F);
        target.level().spawnEntity(spit);
    }
}