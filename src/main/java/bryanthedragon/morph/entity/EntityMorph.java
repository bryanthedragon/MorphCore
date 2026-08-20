package bryanthedragon.morph.entity;

import bryanthedragon.morph.api.MorphAPI;
import bryanthedragon.morph.api.MorphManager;
import bryanthedragon.morph.api.MorphUtils;
import bryanthedragon.morph.api.models.IMorphProvider;
import bryanthedragon.morph.api.morphs.AbstractMorph;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

import net.minecraftforge.entity.IEntityAdditionalSpawnData;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

/**
 * Entity morph
 * 
 * This entity is responsible for showing up the morph which will player 
 * acquire.
 * 
 * This entity is similar to {@link EntityXPOrb} or {@link EntityItem}, in terms 
 * of picking up.
 */
public class EntityMorph extends LivingEntity implements IEntityAdditionalSpawnData, IMorphProvider
{
    private String username;
    private UUID owner;
    private boolean ownerless = true;

    private Player player;

    public int timer = 30;
    public int lifetime = 2400;
    public AbstractMorph morph;

    /**
     * Initiate the morph and make this entity invulnerable
     */
    public EntityMorph(EntityType<? extends LivingEntity> type, Level worldIn)
    {
        super(type, worldIn);

        this.setInvulnerable(true);
        this.setCustomName(Component.literal("Morph"));
    }

    /**
     * Initiate the morph with morph and owner's UUID 
     */
    public EntityMorph(EntityType<? extends LivingEntity> type, Level worldIn, UUID owner, AbstractMorph morph)
    {
        this(type, worldIn);

        this.owner = owner;
        this.morph = morph;
        this.ownerless = false;

        this.setSize(morph);
    }


    public AbstractMorph getMorph()
    {
        return this.morph;
    }

    /**
     * Get display name
     */

    public Component getDisplayName()
    {
        if (this.morph != null)
        {
            return Component.translatable("entity." + this.morph.name + ".name");
        }

        return super.getDisplayName();
    }

    /**
     * Set size based on the morph's characteristics
     */
    @SuppressWarnings("unused")
    private void setSize(AbstractMorph morph)
    {
        if (morph != null)
        {
            float width = Math.max(0, Math.min(1.5F, morph.getWidth(this)));
            float height = Math.max(0, Math.min(2.0F, morph.getHeight(this)));
            this.refreshDimensions();
        }
    }


    protected boolean canTriggerWalking()
    {
        return false;
    }


    public boolean canBeCollidedWith()
    {
        return false;
    }


    public boolean canBePushed()
    {
        return false;
    }

    /**
     * Can't collide with other entities 
     */

    protected void collideWithNearbyEntities()
    {

    }

    /**
     * Update method
     * 
     * This method is responsible for looking for player owner and advancing 
     * itself toward the player. It will also die and grant the morph to the 
     * player when it will be very close to player.
     */

    public void tick()
    {
        /* Don't allow it move horizontally */
        this.setDeltaMovement(0, this.getDeltaMovement().y, 0);

        super.tick();

        if (this.timer > 0)
        {
            this.timer--;

            return;
        }

        /* Find an owner */
        if (!this.level().isClientSide && !this.isRemoved())
        {
            if (this.lifetime > 0)
            {
                this.lifetime--;
            }
            else if (this.lifetime == 0)
            {
                /* I.e. kill only if lifetime was above 0 (in this case
                 * only /summon'd morph with custom name tag can have 
                 * negative lifetime) */
                this.discard();
            }

            this.updateMorph();
        }
    }

    /**
     * Do morph things which morphs should do on update. 
     */
    private void updateMorph()
    {
        /* Grant ownerless morph to the first collided player */
        if (this.ownerless)
        {
            for (Player player : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox()))
            {
                this.grantMorph(player);

                break;
            }
        }
        else
        {
            /* Find the owner */
            if (this.player == null || this.player.isDeadOrDying())
            {
                if (this.owner != null)
                {
                    this.player = this.level().getPlayerByUUID(this.owner);
                }
                else if (this.username != null)
                {
                    this.player = this.level().getPlayerByUUID(UUID.fromString(this.username));
                }
            }

            /* Acquire morph when owner collides with a morph */
            if (this.player != null && this.getBoundingBox().intersects(this.player.getBoundingBox()))
            {
                this.grantMorph(this.player);
            }
        }
    }

    /**
     * Grant morph to the player
     * 
     * This method is responsible for giving this morph to the player. 
     */
    protected void grantMorph(Player player)
    {
        if (this.level().isClientSide)
        {
            return;
        }

        if (MorphAPI.acquire(player, this.morph))
        {
            this.level().playSound(player, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.AMBIENT, 1.0F, 1.0F);

            /* Make the pickup animation */
            // Server-side pickup animation needs to be updated for 1.20.1
        }

        this.discard();
    }

    /* Read / write */

    public void addAdditionalSaveData(CompoundTag compound)
    {
        compound.putInt("LifeTime", this.lifetime);
        compound.putBoolean("Ownerless", this.ownerless);

        if (this.username != null && !this.username.isEmpty())
        {
            compound.putString("Username", this.username);
        }

        if (this.owner != null)
        {
            compound.putString("Owner", this.owner.toString());
        }

        if (this.morph != null)
        {
            CompoundTag tag = new CompoundTag();

            this.morph.toNBT(tag);
            compound.put("Morph", tag);
        }
    }


    public void readAdditionalSaveData(CompoundTag compound)
    {
        String owner = compound.getString("Owner");

        this.owner = owner.isEmpty() ? null : UUID.fromString(owner);

        if (compound.contains("LifeTime"))
        {
            this.lifetime = compound.getInt("LifeTime");
        }

        if (this.owner != null)
        {
            this.ownerless = false;
        }

        if (compound.contains("Username"))
        {
            this.username = compound.getString("Username");
            this.ownerless = false;
        }
        else if (compound.contains("Ownerless"))
        {
            this.ownerless = compound.getBoolean("Ownerless");
        }

        if (compound.contains("Morph"))
        {
            this.morph = MorphManager.INSTANCE.morphFromNBT(compound.getCompound("Morph"));
        }

        this.setSize(morph);
    }

    /* Spawn data read / write */

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer)
    {
        buffer.writeUtf(this.owner != null ? this.owner.toString() : "");
        MorphUtils.morphToBuf(buffer, this.morph);
    }


    @Override
    public void readSpawnData(FriendlyByteBuf buffer)
    {
        String owner = buffer.readUtf();

        this.owner = owner.isEmpty() ? null : UUID.fromString(owner);
        this.morph = MorphUtils.morphFromBuf(buffer);

        this.setSize(morph);
    }

    /* Unused methods */
    public Iterable<ItemStack> getArmorInventoryList()
    {
        return Arrays.<ItemStack>asList();
    }

    public void setItemStackToSlot(EquipmentSlot slotIn, ItemStack stack)
    {

    }

    @Override
    public HumanoidArm getMainArm()
    {
        return HumanoidArm.RIGHT;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return Collections.emptyList();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) 
    {

    }
}