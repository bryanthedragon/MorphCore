package org.bryanthedragon.morph.core.registry;

import org.bryanthedragon.morph.api.IMorphFactory;
import org.bryanthedragon.morph.api.MorphManager;
import org.bryanthedragon.morph.api.abilities.Climb;
import org.bryanthedragon.morph.api.abilities.FireProof;
import org.bryanthedragon.morph.api.abilities.Fly;
import org.bryanthedragon.morph.api.abilities.Glide;
import org.bryanthedragon.morph.api.abilities.Hungerless;
import org.bryanthedragon.morph.api.abilities.IAbility;
import org.bryanthedragon.morph.api.abilities.IAction;
import org.bryanthedragon.morph.api.abilities.IAttackAbility;
import org.bryanthedragon.morph.api.abilities.Jumping;
import org.bryanthedragon.morph.api.abilities.NightVision;
import org.bryanthedragon.morph.api.abilities.PreventFall;
import org.bryanthedragon.morph.api.abilities.Rotten;
import org.bryanthedragon.morph.api.abilities.SnowWalk;
import org.bryanthedragon.morph.api.abilities.StepUp;
import org.bryanthedragon.morph.api.abilities.SunAllergy;
import org.bryanthedragon.morph.api.abilities.Swim;
import org.bryanthedragon.morph.api.abilities.WaterAllergy;
import org.bryanthedragon.morph.api.abilities.WaterBreath;
import org.bryanthedragon.morph.api.actions.ender.shulker.ShulkerBulletAction;
import org.bryanthedragon.morph.api.actions.ender.enderdragon.firebreath.FireBreathAction;
import org.bryanthedragon.morph.api.actions.ender.enderman.TeleportAction;
import org.bryanthedragon.morph.api.actions.ender.endermite.EndermiteAction;
import org.bryanthedragon.morph.api.actions.nether.fireball.FireballAction;
import org.bryanthedragon.morph.api.actions.nether.fireball.SmallFireballAction;
import org.bryanthedragon.morph.api.actions.overworld.hostile.creeper.ExplodeAction;
import org.bryanthedragon.morph.api.actions.overworld.hostile.silverfish.SliverfishAction;
import org.bryanthedragon.morph.api.actions.overworld.hostile.witch.PotionsAction;
import org.bryanthedragon.morph.api.actions.overworld.passive.horse.JumpBoostAction;
import org.bryanthedragon.morph.api.actions.overworld.passive.lama.SpitAction;
import org.bryanthedragon.morph.api.actions.overworld.passive.snowgolem.SnowballAction;
import org.bryanthedragon.morph.api.attacks.KnockbackAttack;
import org.bryanthedragon.morph.api.attacks.MobAttack;
import org.bryanthedragon.morph.api.attacks.PoisonAttack;
import org.bryanthedragon.morph.api.attacks.WitherAttack;
import org.bryanthedragon.morph.api.editors.GuiBlockMorph;
import org.bryanthedragon.morph.api.editors.GuiEntityMorph;
import org.bryanthedragon.morph.api.editors.GuiItemMorph;
import org.bryanthedragon.morph.api.editors.GuiLabelMorph;
import org.bryanthedragon.morph.api.editors.GuiPlayerMorph;
import org.bryanthedragon.morph.api.morphs.main.AbstractMorph;
import org.bryanthedragon.morph.api.morphs.main.BlockMorph;
import org.bryanthedragon.morph.api.morphs.main.EntityMorph;
import org.bryanthedragon.morph.api.morphs.main.ItemMorph;
import org.bryanthedragon.morph.api.morphs.main.LabelMorph;
import org.bryanthedragon.morph.api.morphs.main.PlayerMorph;
import org.bryanthedragon.morph.api.morphs.main.UndeadMorph;
import org.bryanthedragon.morph.client.gui.editor.GuiAbstractMorph;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;

/**
 * MorphCore factory
 * 
 * This is underlying morph factory. It's responsible for registering vanilla actions
 * and morph sections
 */
public class MorphCoreFactory implements IMorphFactory
{
    /**
     * Nothing to register here, since all of the morphs are generated on 
     * runtime 
     */
    @Override
    public void register(MorphManager manager)
    {
        /* Define shortcuts */
        Map<String, IAbility> abilities = manager.abilities;
        Map<String, IAttackAbility> attacks = manager.attacks;
        Map<String, IAction> actions = manager.actions;

        /* Register default abilities */
        abilities.put("climb", (IAbility) new Climb());
        abilities.put("fire_proof", (IAbility) new FireProof());
        abilities.put("fly", (IAbility) new Fly());
        abilities.put("glide", (IAbility) new Glide());
        abilities.put("hungerless", (IAbility) new Hungerless());
        abilities.put("jumping", (IAbility) new Jumping());
        abilities.put("night_vision", (IAbility) new NightVision());
        abilities.put("prevent_fall", (IAbility) new PreventFall());
        abilities.put("rotten", (IAbility) new Rotten());
        abilities.put("snow_walk", (IAbility) new SnowWalk());
        abilities.put("step_up", (IAbility) new StepUp());
        abilities.put("sun_allergy", (IAbility) new SunAllergy());
        abilities.put("swim", (IAbility) new Swim());
        abilities.put("water_allergy", (IAbility) new WaterAllergy());
        abilities.put("water_breath", (IAbility) new WaterBreath());

        /* Register default actions */
        actions.put("endermite", (IAction) new EndermiteAction());
        actions.put("explode", (IAction) new ExplodeAction());
        actions.put("fireball", (IAction) new FireballAction());
        actions.put("fire_breath", (IAction) new FireBreathAction());
        actions.put("jump", (IAction) new JumpBoostAction());
        actions.put("potions", (IAction) new PotionsAction());
        actions.put("shulker_bullet", (IAction) new ShulkerBulletAction());
        actions.put("silverfish", (IAction) new SliverfishAction());
        actions.put("small_fireball", (IAction) new SmallFireballAction());
        actions.put("snowball", (IAction) new SnowballAction());
        actions.put("spit", (IAction) new SpitAction());
        actions.put("teleport", (IAction) new TeleportAction());

        /* Register default attacks */
        attacks.put("knockback", (IAttackAbility) new KnockbackAttack());
        attacks.put("mob", (IAttackAbility) new MobAttack());
        attacks.put("poison", (IAttackAbility) new PoisonAttack());
        attacks.put("wither", (IAttackAbility) new WitherAttack());

        /* Register main section */
        manager.list.register(new MorphCoreSection(this, "entity"));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("rawtypes")
    public void registerMorphEditors(Minecraft mc, List<GuiAbstractMorph> editors)
    {
        editors.add(new GuiLabelMorph(mc));
        editors.add(new GuiItemMorph(mc));
        editors.add(new GuiBlockMorph(mc));
        editors.add(new GuiPlayerMorph(mc));
        editors.add(new GuiEntityMorph(mc));
        editors.add(new GuiAbstractMorph(mc));
    }

    /**
     * Checks if the {@link EntityList} has an entity with given name does 
     * exist and the entity is a living base.
     */
    @Override
    public boolean hasMorph(String name)
    {
        if (name.equalsIgnoreCase("player"))
        {
            return true;
        }

        if (name.equals("block") || name.equals("item") || name.equals("label"))
        {
            return true;
        }

        Class<? extends Entity> clazz = null;
        ResourceLocation key = new ResourceLocation(name);

        for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES.getValues())
        {
            if (ForgeRegistries.ENTITY_TYPES.getKey(entityType).equals(key))
            {
                clazz = entityType.getBaseClass();
            }
        }

        return clazz == null ? false : LivingEntity.class.isAssignableFrom(clazz);
    }

    /**
     * Create an {@link EntityMorph} from NBT
     */
    @Override
    public AbstractMorph getMorphFromNBT(CompoundTag tag)
    {
        String name = tag.getString("Name");
        AbstractMorph morph;

        if (tag.getString("Name").equalsIgnoreCase("player"))
        {
            PlayerMorph player = new PlayerMorph();

            player.fromNBT(tag);

            return player.profile != null ? player : null;
        }

        if (name.equals("block"))
        {
            morph = new BlockMorph();
        }
        else if (name.equals("item"))
        {
            morph = new ItemMorph();
        }
        else if (name.equals("label"))
        {
            morph = new LabelMorph();
        }
        else
        {
            morph = morphFromName(name);
        }

        morph.fromNBT(tag);

        return morph;
    }

    /**
     * Get a morph from a name 
     */
    public EntityMorph morphFromName(String name)
    {
        if (name.equals("minecraft:zombie") || name.equals("minecraft:skeleton") || name.equals("minecraft:zombie_villager"))
        {
            return new UndeadMorph();
        }

        return new EntityMorph();
    }
}
