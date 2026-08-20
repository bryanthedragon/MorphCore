package bryanthedragon.morph.core.registry;

import bryanthedragon.morph.api.IMorphFactory;
import bryanthedragon.morph.api.MorphManager;
import bryanthedragon.morph.api.abilities.Climb;
import bryanthedragon.morph.api.abilities.FireProof;
import bryanthedragon.morph.api.abilities.Fly;
import bryanthedragon.morph.api.abilities.Glide;
import bryanthedragon.morph.api.abilities.Hungerless;
import bryanthedragon.morph.api.abilities.IAbility;
import bryanthedragon.morph.api.abilities.IAction;
import bryanthedragon.morph.api.abilities.IAttackAbility;
import bryanthedragon.morph.api.abilities.Jumping;
import bryanthedragon.morph.api.abilities.NightVision;
import bryanthedragon.morph.api.abilities.PreventFall;
import bryanthedragon.morph.api.abilities.Rotten;
import bryanthedragon.morph.api.abilities.SnowWalk;
import bryanthedragon.morph.api.abilities.StepUp;
import bryanthedragon.morph.api.abilities.SunAllergy;
import bryanthedragon.morph.api.abilities.Swim;
import bryanthedragon.morph.api.abilities.WaterAllergy;
import bryanthedragon.morph.api.abilities.WaterBreath;
import bryanthedragon.morph.api.actions.ender.ShulkerBulletAction;
import bryanthedragon.morph.api.actions.ender.enderdragon.firebreath.FireBreathAction;
import bryanthedragon.morph.api.actions.ender.enderman.TeleportAction;
import bryanthedragon.morph.api.actions.ender.endermite.EndermiteAction;
import bryanthedragon.morph.api.actions.nether.fireball.FireballAction;
import bryanthedragon.morph.api.actions.nether.fireball.SmallFireballAction;
import bryanthedragon.morph.api.actions.overworld.hostile.creeper.ExplodeAction;
import bryanthedragon.morph.api.actions.overworld.hostile.silverfish.SliverfishAction;
import bryanthedragon.morph.api.actions.overworld.hostile.witch.PotionsAction;
import bryanthedragon.morph.api.actions.overworld.passive.horse.JumpBoostAction;
import bryanthedragon.morph.api.actions.overworld.passive.lama.SpitAction;
import bryanthedragon.morph.api.actions.overworld.passive.snowgolem.SnowballAction;
import bryanthedragon.morph.api.attacks.KnockbackAttack;
import bryanthedragon.morph.api.attacks.MobAttack;
import bryanthedragon.morph.api.attacks.PoisonAttack;
import bryanthedragon.morph.api.attacks.WitherAttack;
import bryanthedragon.morph.api.editors.GuiBlockMorph;
import bryanthedragon.morph.api.editors.GuiEntityMorph;
import bryanthedragon.morph.api.editors.GuiItemMorph;
import bryanthedragon.morph.api.editors.GuiLabelMorph;
import bryanthedragon.morph.api.editors.GuiPlayerMorph;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.api.morphs.main.BlockMorph;
import bryanthedragon.morph.api.morphs.main.EntityMorph;
import bryanthedragon.morph.api.morphs.main.ItemMorph;
import bryanthedragon.morph.api.morphs.main.LabelMorph;
import bryanthedragon.morph.api.morphs.main.PlayerMorph;
import bryanthedragon.morph.api.morphs.main.UndeadMorph;
import bryanthedragon.morph.client.gui.editor.GuiAbstractMorph;

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
