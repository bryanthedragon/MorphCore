package bryanthedragon.morph.api.morphs.main;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import bryanthedragon.morph.client.gui.framework.elements.GuiModelRenderer;
import bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiInventoryElement;
import bryanthedragon.morph.utils.resources.RLUtils;
import bryanthedragon.morph.api.render.CachedExtrusion;
import bryanthedragon.morph.api.render.ItemExtruder;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;

public class ItemMorph extends ItemStackMorph
{
    @OnlyIn(Dist.CLIENT)
    private static BiMap<String, ItemCameraTransforms.TransformType> transformTypes;

    public ItemStack stack = new ItemStack(Items.DIAMOND_HOE, 1);
    public String transform = "";
    public ResourceLocation texture;
    public boolean animation;
    public boolean itemFromEquipment;
    public EquipmentSlot equipmentSlot = EquipmentSlot.MAINHAND;

    @OnlyIn(Dist.CLIENT)
    public static BiMap<String, ItemCameraTransforms.TransformType> getTransformTypes()
    {
        if (transformTypes == null)
        {
            transformTypes = HashBiMap.create();

            transformTypes.put("none", ItemCameraTransforms.TransformType.NONE);
            transformTypes.put("third_person_left_hand", ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
            transformTypes.put("third_person_right_hand", ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
            transformTypes.put("first_person_left_hand", ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
            transformTypes.put("first_person_right_hand", ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
            transformTypes.put("head", ItemCameraTransforms.TransformType.HEAD);
            transformTypes.put("gui", ItemCameraTransforms.TransformType.GUI);
            transformTypes.put("ground", ItemCameraTransforms.TransformType.GROUND);
            transformTypes.put("fixed", ItemCameraTransforms.TransformType.FIXED);
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

            return entity.getItemStackFromSlot(this.equipmentSlot);
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
    public ItemCameraTransforms.TransformType getTransformType()
    {
        if (this.transform == null || this.transform.isEmpty())
        {
            return ItemCameraTransforms.TransformType.NONE;
        }
        
        ItemCameraTransforms.TransformType transformType = getTransformTypes().get(this.transform);

        return transformType == null ? ItemCameraTransforms.TransformType.NONE : transformType;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderOnScreen(Player player, int x, int y, float scale, float alpha)
    {
        GlStateManager.disableCull();
        GlStateManager.enableDepth();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        scale = scale / 16F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y - 12, 0);

        if (this.texture != null)
        {
            GlStateManager.color(1, 1, 1);
            GlStateManager.scale(scale * 16F, -scale * 16F, scale * 16F);

            CachedExtrusion extrusion = ItemExtruder.extrude(this.texture);

            if (extrusion != null)
            {
                extrusion.render();
            }
        }
        else
        {
            GlStateManager.scale(scale, scale, scale);

            RenderHelper.enableGUIStandardItemLighting();

            GuiInventoryElement.drawItemStack(this.stack, -8, -8, 0, null);

            RenderHelper.disableStandardItemLighting();
        }

        GlStateManager.popMatrix();
        GlStateManager.enableCull();
        GlStateManager.disableDepth();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(LivingEntity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        float lastBrightnessX = OpenGlHelper.lastBrightnessX;
        float lastBrightnessY = OpenGlHelper.lastBrightnessY;

        if (!this.lighting)
        {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        }

        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        Minecraft.getInstance().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        RenderItem render = Minecraft.getInstance().getRenderItem();
        ItemStack stack = this.getStackForRender(entity);
        IBakedModel model = render.getItemModelWithOverrides(stack, entity.world, entity);

        ItemCameraTransforms.TransformType transform = this.getTransformType();

        if (transform != ItemCameraTransforms.TransformType.NONE)
        {
            model = ForgeHooksClient.handleCameraTransforms(model, transform, false);
        }

        if (this.animation)
        {
            long ticks = entity.world.getTotalWorldTime();
            float bobbing = MathHelper.sin((ticks + partialTicks) / 10F) * 0.1F + 0.1F;

            GlStateManager.translate(0F, bobbing, 0F);
            GlStateManager.rotate((entity.ticksExisted + partialTicks) * 2F, 0F, 1F, 0F);
        }

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
            render.renderItem(stack, model);
        }

        GlStateManager.popMatrix();

        if (!this.lighting)
        {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof ItemMorph)
        {
            ItemMorph item = (ItemMorph) obj;

            result = result && ItemStack.areItemStacksEqualUsingNBTShareTag(this.stack, item.stack);
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
        return target.width;
    }

    @Override
    public float getHeight(LivingEntity target)
    {
        return target.height;
    }

    @Override
    public void toNBT(CompoundTag tag)
    {
        super.toNBT(tag);

        if (!this.stack.isEmpty())
        {
            tag.setTag("Stack", this.stack.serializeNBT());
        }

        tag.setString("Transform", this.transform);

        if (this.texture != null)
        {
            tag.setString("Texture", this.texture.toString());
        }

        if (this.animation)
        {
            tag.setBoolean("Animation", this.animation);
        }

        if (this.itemFromEquipment)
        {
            tag.setBoolean("ItemFromEquipment", this.itemFromEquipment);
        }

        if (this.equipmentSlot != EquipmentSlot.MAINHAND)
        {
            tag.setString("EquipmentSlot", this.equipmentSlot.getName());
        }
    }

    @Override
    public void fromNBT(CompoundTag tag)
    {
        super.fromNBT(tag);

        if (tag.hasKey("Stack"))
        {
            this.stack = new ItemStack(tag.getCompoundTag("Stack"));
        }

        this.transform = tag.getString("Transform");

        if (tag.hasKey("Texture"))
        {
            this.texture = RLUtils.create(tag.getString("Texture"));
        }

        if (tag.hasKey("Animation"))
        {
            this.animation = tag.getBoolean("Animation");
        }

        if (tag.hasKey("ItemFromEquipment"))
        {
            this.itemFromEquipment = tag.getBoolean("ItemFromEquipment");
        }

        if (tag.hasKey("EquipmentSlot"))
        {
            this.equipmentSlot = EquipmentSlot.fromString(tag.getString("EquipmentSlot"));
        }
    }
}
