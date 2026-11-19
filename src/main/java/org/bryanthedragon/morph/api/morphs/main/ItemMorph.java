package org.bryanthedragon.morph.api.morphs.main;


import org.bryanthedragon.morph.client.gui.framework.elements.GuiModelRenderer;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiInventoryElement;
import org.bryanthedragon.morph.utils.resources.RLUtils;
import org.bryanthedragon.morph.api.render.CachedExtrusion;
import org.bryanthedragon.morph.api.render.ItemExtruder;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;

public class ItemMorph extends ItemStackMorph
{
    @OnlyIn(Dist.CLIENT)
    private static BiMap<String, ItemDisplayContext> transformTypes;

    public ItemStack stack = new ItemStack(Items.DIAMOND_HOE, 1);
    public String transform = "";
    public ResourceLocation texture;
    public boolean animation;
    public boolean itemFromEquipment;
    public EquipmentSlot equipmentSlot = EquipmentSlot.MAINHAND;

    @OnlyIn(Dist.CLIENT)
    public static BiMap<String, ItemDisplayContext> getTransformTypes()
    {
        if (transformTypes == null)
        {
            transformTypes = HashBiMap.create();

            transformTypes.put("none", ItemDisplayContext.NONE);
            transformTypes.put("third_person_left_hand", ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
            transformTypes.put("third_person_right_hand", ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
            transformTypes.put("first_person_left_hand", ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
            transformTypes.put("first_person_right_hand", ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
            transformTypes.put("head", ItemDisplayContext.HEAD);
            transformTypes.put("gui", ItemDisplayContext.GUI);
            transformTypes.put("ground", ItemDisplayContext.GROUND);
            transformTypes.put("fixed", ItemDisplayContext.FIXED);
        }

        return transformTypes;
    }

    public ItemMorph()
    {
        this.name = "item";
    }

    @OnlyIn(Dist.CLIENT)
    private ItemStack getStackForRender(LivingEntity entity)
    {
        if (this.itemFromEquipment)
        {
            if (GuiModelRenderer.isRendering())
            {
                entity = Minecraft.getInstance().player;
            }

            return entity.getItemBySlot(this.equipmentSlot);
        }

        return this.stack;
    }

    @Override
    public void setStack(ItemStack stack)
    {
        this.stack = stack;
    }

    @Override
    public ItemStack getStack()
    {
        return this.stack;
    }

    @OnlyIn(Dist.CLIENT)
    public ItemDisplayContext getTransformType()
    {
        if (this.transform == null || this.transform.isEmpty())
        {
            return ItemDisplayContext.NONE;
        }
        
        ItemDisplayContext transformType = getTransformTypes().get(this.transform);

        return transformType == null ? ItemDisplayContext.NONE : transformType;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderOnScreen(Player player, int x, int y, float scale, float alpha)
    {
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

        PoseStack modelView = RenderSystem.getModelViewStack();
        modelView.pushPose();
        modelView.translate(x, y - 12, 0);
        RenderSystem.applyModelViewMatrix();

        if (this.texture != null)
        {
            modelView.scale(scale, -scale, scale);
            RenderSystem.applyModelViewMatrix();
            CachedExtrusion extrusion = ItemExtruder.extrude(this.texture);

            if (extrusion != null)
            {
                extrusion.render();
            }
        }
        else
        {
            modelView.scale(scale / 16F, scale / 16F, scale / 16F);
            RenderSystem.applyModelViewMatrix();
            GuiInventoryElement.drawItemStack(this.stack, -8, -8, 0, null);
        }

        modelView.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(LivingEntity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        Minecraft mc = Minecraft.getInstance();
        ItemStack stack = this.getStackForRender(entity);
        PoseStack modelView = RenderSystem.getModelViewStack();
        modelView.pushPose();
        modelView.translate(x, y, z);
        if (this.animation)
        {
            float bobbing = (float) Math.sin((entity.level().getGameTime() + partialTicks) / 10F) * 0.1F + 0.1F;
            modelView.translate(0F, bobbing, 0F);
            modelView.mulPose(Axis.YP.rotationDegrees((entity.tickCount + partialTicks) * 2F));
        }

        RenderSystem.applyModelViewMatrix();
        if (this.texture != null)
        {
            CachedExtrusion extrusion = ItemExtruder.extrude(this.texture);

            if (extrusion != null)
            {
                extrusion.render();
            }
        }
        else
        {
            MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
            int light = this.lighting
                ? LevelRenderer.getLightColor(entity.level(), entity.blockPosition())
                : 15728880;
            mc.getItemRenderer().renderStatic(
                entity,
                stack,
                this.getTransformType(),
                false,
                modelView,
                buffers,
                entity.level(),
                light,
                OverlayTexture.NO_OVERLAY,
                entity.getId());
            buffers.endBatch();
        }

        modelView.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof ItemMorph)
        {
            ItemMorph item = (ItemMorph) obj;

            result = result && ItemStack.matches(this.stack, item.stack);
            result = result && Objects.equals(this.transform, item.transform);
            result = result && Objects.equals(this.texture, item.texture);
            result = result && this.animation == item.animation;
            result = result && this.itemFromEquipment == item.itemFromEquipment;
            result = result && this.equipmentSlot == item.equipmentSlot;
        }

        return result;
    }

    @Override
    public AbstractMorph create()
    {
        return new ItemMorph();
    }

    @Override
    public void copy(AbstractMorph from)
    {
        super.copy(from);

        if (from instanceof ItemMorph)
        {
            ItemMorph item = (ItemMorph) from;

            this.stack = item.stack.copy();
            this.transform = item.transform;
            this.texture = item.texture;
            this.animation = item.animation;
            this.itemFromEquipment = item.itemFromEquipment;
            this.equipmentSlot = item.equipmentSlot;
        }
    }

    @Override
    public float getWidth(LivingEntity target)
    {
        return target.getBbWidth();
    }

    @Override
    public float getHeight(LivingEntity target)
    {
        return target.getBbHeight();
    }

    @Override
    public void toNBT(CompoundTag tag)
    {
        super.toNBT(tag);

        if (!this.stack.isEmpty())
        {
            tag.put("Stack", this.stack.save(new CompoundTag()));
        }

        tag.putString("Transform", this.transform);

        if (this.texture != null)
        {
            tag.putString("Texture", this.texture.toString());
        }

        if (this.animation)
        {
            tag.putBoolean("Animation", this.animation);
        }

        if (this.itemFromEquipment)
        {
            tag.putBoolean("ItemFromEquipment", this.itemFromEquipment);
        }

        if (this.equipmentSlot != EquipmentSlot.MAINHAND)
        {
            tag.putString("EquipmentSlot", this.equipmentSlot.getName());
        }
    }

    @Override
    public void fromNBT(CompoundTag tag)
    {
        super.fromNBT(tag);

        if (tag.contains("Stack"))
        {
            this.stack = ItemStack.of(tag.getCompound("Stack"));
        }

        this.transform = tag.getString("Transform");

        if (tag.contains("Texture"))
        {
            this.texture = RLUtils.create(tag.getString("Texture"));
        }

        if (tag.contains("Animation"))
        {
            this.animation = tag.getBoolean("Animation");
        }

        if (tag.contains("ItemFromEquipment"))
        {
            this.itemFromEquipment = tag.getBoolean("ItemFromEquipment");
        }

        if (tag.contains("EquipmentSlot"))
        {
            this.equipmentSlot = EquipmentSlot.byName(tag.getString("EquipmentSlot"));
        }
    }
}
