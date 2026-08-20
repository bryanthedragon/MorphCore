package bryanthedragon.morph.api.actions.overworld.passive.lama;

import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.api.morphs.main.EntityMorph;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;

public class SpitAction implements IAction
{
    @Override
    public void execute(LivingEntity target, AbstractMorph morph)
    {
        if (target.level().isClientSide())
        {
            return;
        }

        LlamaSpit spit = new LlamaSpit(target.level(), (Llama) target);

        Vec3 vec3 = target.getLookAngle();

        double d2 = vec3.x;
        double d3 = vec3.y;
        double d4 = vec3.z;

        spit.setPos(target.getX() + d2 * 1.5, target.getY() + target.getEyeHeight() * 0.8F, target.getZ() + d4 * 1.5);
        spit.shoot(d2, d3, d4, (float) 1.0F, 1.0F);

        if (morph instanceof EntityMorph)
        {
            LivingEntity entity = ((EntityMorph) morph).getEntity();

            if (entity instanceof Llama)
            {
                spit.setOwner((Llama) entity);
            }
        }

        target.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.LLAMA_SPIT, target.getSoundSource(), 1.0F, 1.0F + (target.getRandom().nextFloat() - target.getRandom().nextFloat()) * 0.2F);
        target.level().addFreshEntity(spit);
    }
}