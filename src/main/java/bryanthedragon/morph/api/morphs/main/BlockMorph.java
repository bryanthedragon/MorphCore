package bryanthedragon.morph.api.morphs.main;

import com.google.common.base.Objects;

import com.mojang.blaze3d.platform.GlStateManager;

import bryanthedragon.morph.capabilities.morphing.IMorphing;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Block morph
 * 
 * This morph allows players to disguise themselves as blocks.
 */
public class BlockMorph extends ItemStackMorph
{
    /**
     * Block state to render, doesn't really mean anything on the server 
     * side. Used for rendering 
     */
    public BlockState block = Blocks.STONE.defaultBlockState();

    /**
     * Block position to lock the target in
     */
    public BlockPos blockPos;

    /**
     * Set the name 
     */
    public BlockMorph()
    {
        this.name = "block";
    }

    @Override
    public void setStack(ItemStack stack)
    {
        if (stack.getItem() instanceof BlockItem)
        {
            this.block = ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
        }
    }

    @Override
    public ItemStack getStack()
    {
        return new ItemStack(this.block.getBlock(), 1);
    }

    /**
     * Render in GUIs just like any other entity, 45 degree rotate  by 
     * X and Y. 
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderOnScreen(Player player, int x, int y, float scale, float alpha)
    {
        Minecraft mc = Minecraft.getInstance();

        GlStateManager.enableDepth();
        BlockEntityRenderDispatcher blockrendererdispatcher = mc.getBlockEntityRenderDispatcher();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale((-scale), -scale, -scale);
        GlStateManager.rotate(45.0F, -1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0, 1, 0);

        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        blockrendererdispatcher.renderBlockBrightness(this.block, 1.0F);
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
    }

    /**
     * Render the block morph on player's position
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(LivingEntity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        Minecraft mc = Minecraft.getInstance();

        float lastBrightnessX = OpenGlHelper.lastBrightnessX;
        float lastBrightnessY = OpenGlHelper.lastBrightnessY;

        if (!this.lighting)
        {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        }

        GlStateManager.enableCull();
        GlStateManager.enableRescaleNormal();
        BlockRendererDispatcher blockrendererdispatcher = mc.getBlockEntityRenderDispatcher();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 0.5F, (float) z);

        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, 0.5F);
        blockrendererdispatcher.renderBlockBrightness(this.block, 1.0F);
        GlStateManager.translate(0.0F, 0.0F, 1.0F);
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableCull();

        if (!this.lighting)
        {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        }
    }

    /**
     * Update the entity
     * 
     * This particular morph sets the AABB to almost 1 (so it would be 
     * easy enough to slip into 1x1 block area, and also so other players 
     * won't see the AABB when debug AABB is enabled).
     * 
     * This method as well locks the character in block position if 
     * it has the block position.
     */
    @Override
    public void update(LivingEntity target)
    {
        super.update(target);

        if (this.blockPos != null)
        {
            target.setDeltaMovement(0, 0, 0);
            target.setPos(this.blockPos.getX() + 0.5, this.blockPos.getY(), this.blockPos.getZ() + 0.5);
        }

        this.updateSize(target, getWidth(target), getHeight(target));
    }

    @Override
    public AbstractMorph create()
    {
        return new BlockMorph();
    }

    @Override
    public void copy(AbstractMorph from)
    {
        super.copy(from);

        if (from instanceof BlockMorph)
        {
            BlockMorph morph = (BlockMorph) from;

            this.block = morph.block;
            this.blockPos = morph.blockPos;
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof BlockMorph)
        {
            BlockMorph morph = (BlockMorph) obj;

            result = result && Objects.equal(morph.block, this.block);
            result = result && Objects.equal(morph.blockPos, this.blockPos);
        }

        return result;
    }

    @Override
    public void fromNBT(CompoundTag tag)
    {
        super.fromNBT(tag);

        if (tag.contains("Block"))
        {
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("Block")));

            if (block != null)
            {
                this.block = tag.contains("Meta") ? block.getStateFromMeta(tag.getByte("Meta")) : block.defaultBlockState();
            }
        }

        if (tag.contains("Pos"))
        {
            int[] pos = tag.getIntArray("Pos");

            if (pos.length == 3)
            {
                this.blockPos = new BlockPos(pos[0], pos[1], pos[2]);
            }
        }
    }

    @Override
    public void toNBT(CompoundTag tag)
    {
        super.toNBT(tag);

        if (this.block != null)
        {
            tag.putString("Block", ForgeRegistries.BLOCKS.getKey(this.block.getBlock()).toString());
            tag.putByte("Meta", (byte) this.block.getBlock().getMetaFromState(this.block));
        }

        if (this.blockPos != null)
        {
            tag.putIntArray("Pos", new int[] {this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ()});
        }
    }

    @Override
    public float getWidth(LivingEntity target)
    {
        return 0.99F;
    }

    @Override
    public float getHeight(LivingEntity target)
    {
        return 0.99F;
    }
}