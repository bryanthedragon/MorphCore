package org.bryanthedragon.morph.core.registry;

import org.bryanthedragon.morph.MorphCore;
import org.bryanthedragon.morph.api.utils.entity.EntityUtils;
import org.bryanthedragon.morph.api.MorphManager;
import org.bryanthedragon.morph.api.creative.categories.MorphCategory;
import org.bryanthedragon.morph.api.creative.sections.MorphSection;
import org.bryanthedragon.morph.api.morphs.main.BlockMorph;
import org.bryanthedragon.morph.api.morphs.main.EntityMorph;
import org.bryanthedragon.morph.api.morphs.main.ItemMorph;
import org.bryanthedragon.morph.api.morphs.main.LabelMorph;
import org.bryanthedragon.morph.api.morphs.main.PlayerMorph;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MorphCoreSection extends MorphSection
{
    private MorphCoreFactory factory;
    private Map<String, MorphCategory> categoryMap = new HashMap<String, MorphCategory>();

    public MorphCoreSection(MorphCoreFactory factory, String title)
    {
        super(title);

        this.factory = factory;
    }

    @Override
    public void update(Level world)
    {
        boolean full = !this.categories.isEmpty();
        boolean loading = MorphCore.loadEntityMorphs.get();

        if (full || !loading)
        {
            if (full && !loading)
            {
                this.reset();
            }

            return;
        }

        for (EntityType<?> entityType : ForgeRegistries.ENTITY_TYPES.getValues())
        {
            ResourceLocation rl = ForgeRegistries.ENTITY_TYPES.getKey(entityType);

            if (rl == null)
            {
                continue;
            }

            String name = MorphManager.INSTANCE.remap(rl.toString());

            if (this.factory.hasMorph(name))
            {
                this.addMorph(world, name);
            }
        }

        /* Miscellaneous morphs */
        PlayerMorph notch = new PlayerMorph();
        CompoundTag tag = new CompoundTag();

        tag.putString("Name", "player");
        tag.putString("Username", "Notch");
        notch.fromNBT(tag);

        this.get("generic").add(new BlockMorph());
        this.get("generic").add(new ItemMorph());
        this.get("generic").add(new LabelMorph());
        this.get("generic").add(notch);

        /* Add categories to the main list */
        Iterator<MorphCategory> it = this.categoryMap.values().iterator();

        while (it.hasNext())
        {
            if (it.next().getMorphs().isEmpty())
            {
                it.remove();
            }
        }

        this.categories.addAll(this.categoryMap.values());
        this.categoryMap.clear();

        for (MorphCategory category : this.categories)
        {
            category.sort();
        }
    }

    /**
     * Add an entity morph to the morph list
     */
    private void addMorph(Level world, String name)
    {
        try
        {
            EntityMorph morph = this.factory.morphFromName(name);
            EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(name));
            LivingEntity entity = type != null && type.create(world) instanceof LivingEntity living ? living : null;

            if (entity == null)
            {
                System.out.println("Couldn't add morph " + name + ", because it's null!");
                return;
            }

            CompoundTag data = entity.serializeNBT();

            morph.name = name;

            /* Setting up a category */
            String category = "generic";

            /* Category for third-party modded mobs */
            if (!name.startsWith("minecraft:"))
            {
                category = name.substring(0, name.indexOf(":"));
            }
            else if (entity instanceof EnderDragon || entity instanceof WitherBoss || name.equals("minecraft:giant"))
            {
                category = "boss";
            }
            else if (entity instanceof Animal || name.equals("minecraft:bat") || name.equals("minecraft:squid"))
            {
                category = "animal";
            }
            else if (entity instanceof Monster || name.equals("minecraft:ghast") || name.equals("minecraft:magma_cube") || name.equals("minecraft:slime") || name.equals("minecraft:shulker"))
            {
                category = "hostile";
            }

            EntityUtils.stripEntityNBT(data);
            morph.setEntityData(data);

            this.get(category).add(morph);
        }
        catch (Exception e)
        {
            System.out.println("An error occured during insertion of " + name + " morph!");
            e.printStackTrace();
        }
    }

    /**
     * Get a temporary category
     */
    private MorphCategory get(String name)
    {
        MorphCategory cat = this.categoryMap.get(name);

        if (cat == null)
        {
            this.categoryMap.put(name, cat = new MorphCategory(this, name));
        }

        return cat;
    }

    @Override
    public void reset()
    {
        this.categories.clear();
    }
}