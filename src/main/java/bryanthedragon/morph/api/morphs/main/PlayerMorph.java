package bryanthedragon.morph.api.morphs.main;

import com.mojang.authlib.GameProfile;

import bryanthedragon.morph.api.morphs.AbstractMorph;
import bryanthedragon.morph.api.morphs.EntityMorph;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Player morph
 * 
 * This morph basically does nothing than providing some code for 
 * bootstrapping a player isolated in this morph.
 */
public class PlayerMorph extends EntityMorph
{
    /**
     * Player's profile 
     */
    public GameProfile profile;

    public PlayerMorph()
    {
        this.name = "player";
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected String getSubclassDisplayName()
    {
        return this.profile == null ? super.getSubclassDisplayName() : this.profile.getName();
    }

    public void setProfile(String username)
    {
        this.profile = new GameProfile(null, username);
        SkullBlockEntity.updateGameprofile(this.profile, profile -> this.profile = profile);
    }

    /**
     * Create a player morph 
     */
    @Override
    public void setupEntity(Level world)
    {
        LivingEntity created = null;

        if (world.isClientSide)
        {
            created = this.getPlayerClient(world);
        }
        else
        {
            created = new Player(world, BlockPos.ZERO, 0.0F, this.profile)
            {
                @Override
                public boolean isSpectator()
                {
                    return true;
                }

                @Override
                public boolean isCreative()
                {
                    return false;
                }
            };
        }

        created.deserializeNBT(this.entityData);
        created.deathTime = 0;
        created.hurtTime = 0;
        created.walkAnimation.setSpeed(0);
        created.clearFire();

        this.setEntity(created);

        if (world.isClientSide)
        {
            this.setupRenderer();
        }
    }

    /**
     * Encapsulate the code into removable (on client side) method 
     */
    @OnlyIn(Dist.CLIENT)
    private Player getPlayerClient(Level world)
    {
        return new PlayerMorphClientEntity((ClientLevel) world, this.profile);
    }

    /**
     * Updates the player entity, but not using its update methods, but 
     * rather some code that only updates player's cape and some other 
     * stuff.
     */
    @Override
    protected void updateEntity(LivingEntity target)
    {
        Player entity = (Player) this.entity;
        HumanoidArm hand = target.getMainArm();

        hand = hand == HumanoidArm.LEFT ? HumanoidArm.RIGHT : HumanoidArm.LEFT;
        entity.setMainArm(hand);
    }

    @Override
    public AbstractMorph create()
    {
        return new PlayerMorph();
    }

    @Override
    public void copy(AbstractMorph from)
    {
        super.copy(from);

        if (from instanceof PlayerMorph)
        {
            PlayerMorph morph = (PlayerMorph) from;

            this.profile = morph.profile;
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof PlayerMorph)
        {
            PlayerMorph morph = (PlayerMorph) obj;

            result = result && morph.profile.equals(this.profile);
        }

        return result;
    }

    @Override
    public void reset()
    {
        super.reset();

        this.profile = null;
    }

    @Override
    public void fromNBT(CompoundTag tag)
    {
        super.fromNBT(tag);

        if (tag.contains("PlayerProfile", 10))
        {
            this.profile = NbtUtils.readGameProfile(tag.getCompound("PlayerProfile"));
        }
        else if (tag.contains("Username"))
        {
            this.setProfile(tag.getString("Username"));
        }
    }

    @Override
    public void toNBT(CompoundTag tag)
    {
        super.toNBT(tag);

        if (this.profile != null)
        {
            CompoundTag profileTag = new CompoundTag();

            NbtUtils.writeGameProfile(profileTag, this.profile);
            tag.put("PlayerProfile", profileTag);
        }
    }

    /**
     * Placeholder player morph entity
     * 
     * This player entity is used for overriding some of its methods to 
     * provide stable functionality of player morph in its isolated 
     * environment.
     */
    @OnlyIn(Dist.CLIENT)
    public static class PlayerMorphClientEntity extends AbstractClientPlayer
    {
        public GameProfile profile;
        public boolean isBaby;
        public String skinType = "";

        public PlayerMorphClientEntity(ClientLevel world, GameProfile profile)
        {
            super(world, profile);

            this.profile = profile;
        }

        /**
         * Initiate network info property thing 
         */
        @Override
        public boolean isBaby()
        {
            return this.isBaby;
        }

        @Override
        public String getModelName()
        {
            return this.skinType.isEmpty() ? super.getModelName() : this.skinType;
        }

        /**
         * Get this player's skin 
         */
        @Override
        public ResourceLocation getSkinTextureLocation()
        {
            return this.profile == null ? DefaultPlayerSkin.getDefaultSkin() : super.getSkinTextureLocation();
        }

        @Override
        public boolean isModelPartShown(PlayerModelPart part)
        {
            return true;
        }

        @Override
        public void readAdditionalSaveData(CompoundTag compound)
        {
            super.readAdditionalSaveData(compound);

            this.isBaby = compound.getBoolean("IsBaby");
            this.skinType = compound.getString("SkinType");
        }

        @Override
        public void addAdditionalSaveData(CompoundTag compound)
        {
            super.addAdditionalSaveData(compound);

            compound.putBoolean("IsBaby", this.isBaby);
            compound.putString("SkinType", this.skinType);
        }
    }
}