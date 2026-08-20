package bryanthedragon.morph.bodypart;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import bryanthedragon.morph.client.gui.framework.GuiBase;
import bryanthedragon.morph.client.gui.framework.elements.input.GuiTransformations;
import bryanthedragon.morph.utils.transformation.ITransformationObject;
import bryanthedragon.morph.utils.rendering.RenderingUtils;
import bryanthedragon.morph.utils.interpolation.Interpolation;
import bryanthedragon.morph.client.Draw;
import bryanthedragon.morph.client.gui.framework.elements.GuiModelRenderer;
import bryanthedragon.morph.utils.dummy.DummyUtils.DummyEntity;
import bryanthedragon.morph.utils.matrix.MatrixUtils;
import bryanthedragon.morph.utils.nbt.NBTUtils;
import bryanthedragon.morph.MorphCore;
import bryanthedragon.morph.api.Morph;
import bryanthedragon.morph.api.MorphUtils;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.api.morphs.utils.Animation;
import bryanthedragon.morph.api.morphs.utils.IAnimationProvider;
import bryanthedragon.morph.api.morphs.utils.IMorphGenerator;



import com.google.common.base.Objects;
import com.mojang.blaze3d.systems.RenderSystem;


import net.minecraft.client.Minecraft;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * Morph body part
 */
public class BodyPart implements ITransformationObject
{
    public static Vector3f cachedTranslation = new Vector3f();
    public static Vector3f cachedAngularVelocity = new Vector3f();
    public static Matrix4f modelViewMatrix = new Matrix4f();
    public static boolean recording = false;

    public Morph morph = new Morph();
    public ItemStack[] slots = new ItemStack[] {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
    public Vector3f translate = new Vector3f();
    public Vector3f scale = new Vector3f(1, 1, 1);
    public Vector3f rotate = new Vector3f(180, 0, 0);
    public boolean useTarget = false;
    public boolean enabled = true;
    public boolean animate = true;
    public String limb = "";

    private LivingEntity entity;

    private Vector3f lastTranslate;
    private Vector3f lastScale;
    private Vector3f lastRotate;
    public Matrix4f lastMatrix = null;

    @OnlyIn(Dist.CLIENT)
    public static void recordMatrix(AbstractMorph parent, LivingEntity entity, float partialTicks)
    {
        recording = true;

        int lastMatrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        MorphUtils.renderDirect(parent, entity, 0, 0, 0, 0, partialTicks);
        GL11.glPopMatrix();

        GL11.glMatrixMode(lastMatrixMode);

        recording = false;
    }

    @OnlyIn(Dist.CLIENT)
    public void init()
    {
        this.entity = new DummyEntity(Minecraft.getInstance().level);
        this.entity.setYRot(this.entity.yRotO);
        this.entity.setYHeadRot(this.entity.yHeadRotO);
        this.entity.setOnGround(true);

        this.updateEntity();
    }

    @OnlyIn(Dist.CLIENT)
    public void updateEntity()
    {
        if (this.entity == null)
        {
            return;
        }

        for (int i = 0; i < this.slots.length; i++)
        {
            this.entity.setItemSlot(EquipmentSlot.values()[i], this.slots[i]);
        }
    }

    @Override
    public void addTranslation(double x, double y, double z, GuiTransformations.TransformOrientation orientation)
    {
        Vector4f trans = new Vector4f((float) x, (float) y, (float) z, 1);

        if (orientation == GuiTransformations.TransformOrientation.LOCAL)
        {
            float rotX = (float) Math.toRadians(this.rotate.x);
            float rotY = (float) Math.toRadians(this.rotate.y);
            float rotZ = (float) Math.toRadians(this.rotate.z);

            MatrixUtils.getRotationMatrix(rotX, rotY, rotZ, MatrixUtils.RotationOrder.XYZ).transform(trans);
        }

        this.translate.add(new Vector3f(trans.x, trans.y, trans.z));
    }

    @OnlyIn(Dist.CLIENT)
    public void render(AbstractMorph parent, LivingEntity entity, float partialTicks)
    {
    	if (recording)
    	{
    		this.lastMatrix = MatrixUtils.readModelView(new Matrix4f());
    		return;
    	}
    	
        entity = this.useTarget ? entity : this.entity;

        if (this.morph.get() == null || entity == null || !this.enabled)
        {
            return;
        }

        Animation animation = parent instanceof IAnimationProvider ? ((IAnimationProvider) parent).getAnimation() : null;

        float tx = this.translate.x;
        float ty = this.translate.y;
        float tz = this.translate.z;
        float sx = this.scale.x;
        float sy = this.scale.y;
        float sz = this.scale.z;
        float rx = this.rotate.x;
        float ry = this.rotate.y;
        float rz = this.rotate.z;

        if (animation != null && animation.isInProgress() && this.lastTranslate != null && this.animate)
        {
            Interpolation inter = animation.interp;
            float factor = animation.getFactor(partialTicks);

            tx = inter.interpolate(this.lastTranslate.x, tx, factor);
            ty = inter.interpolate(this.lastTranslate.y, ty, factor);
            tz = inter.interpolate(this.lastTranslate.z, tz, factor);
            sx = inter.interpolate(this.lastScale.x, sx, factor);
            sy = inter.interpolate(this.lastScale.y, sy, factor);
            sz = inter.interpolate(this.lastScale.z, sz, factor);
            rx = inter.interpolate(this.lastRotate.x, rx, factor);
            ry = inter.interpolate(this.lastRotate.y, ry, factor);
            rz = inter.interpolate(this.lastRotate.z, rz, factor);
        }

        if (!this.morph.isEmpty())
        {
            MatrixUtils.Transformation modelView = new MatrixUtils.Transformation();

            if (MatrixUtils.matrix != null)
            {
                modelView = MatrixUtils.extractTransformations(MatrixUtils.matrix, MatrixUtils.readModelView(modelViewMatrix));
            }

            /* translation */

            Matrix3f transformation = modelView.getRotation3f().mul(modelView.getScale3f());
            org.joml.Vector3f transformedTranslation = transformation.transform(new org.joml.Vector3f(tx, ty, tz));

            this.morph.get().cachedTranslation.set(cachedTranslation.x, cachedTranslation.y, cachedTranslation.z);
            this.morph.get().cachedTranslation.add(transformedTranslation);

            /* angular velocity */
            /*Matrix3f rotation1 = MatrixUtils.getZYXrotationMatrix(rx, ry, rz);
            Matrix3f rotation0 = MatrixUtils.getZYXrotationMatrix(this.previousRotation.x, this.previousRotation.y, this.previousRotation.z);
            rotation0.invert();
            rotation1.mul(rotation0);
            this.morph.get().angularVelocity.set(MatrixUtils.getAngularVelocity(rotation1));
            this.morph.get().angularVelocity.add(cachedAngularVelocity);
            if (this.morph.get().age != this.lastAge) this.previousRotation.set(rx, ry, rz);
            this.lastAge = this.morph.get().age;*/
        }

        cachedTranslation.set(0,0,0);
        //cachedAngularVelocity.set(0,0,0);

        GL11.glPushMatrix();
        GL11.glTranslatef(tx, ty, tz);

        GL11.glRotatef(rz, 0, 0, 1);
        GL11.glRotatef(ry, 0, 1, 0);
        GL11.glRotatef(rx, 1, 0, 0);

        GL11.glScalef(sx, sy, sz);

        float yaw = entity.getYRot();
        float prevYaw = entity.yRotO;
        float rotationYaw = entity.yBodyRot;
        float prevRotationYaw = entity.yBodyRotO;
        float rotationYawHead = entity.getYHeadRot();
        float prevRotationYawHead = entity.yHeadRotO;

        entity.setYRot(yaw - rotationYaw);
        entity.yRotO = prevYaw - prevRotationYaw;
        entity.setYHeadRot(rotationYawHead - rotationYaw);
        entity.yHeadRotO = prevRotationYawHead - prevRotationYaw;
        entity.setYBodyRot(0);
        entity.yBodyRotO = 0;

        MorphUtils.render(this.morph.get(), entity, 0, 0, 0, 0, partialTicks);

        this.drawAxis();

        entity.setYRot(yaw);
        entity.yRotO = prevYaw;
        entity.setYBodyRot(rotationYaw);
        entity.yBodyRotO = prevRotationYaw;
        entity.setYHeadRot(rotationYawHead);
        entity.yHeadRotO = prevRotationYawHead;

        GL11.glPopMatrix();
    }

    protected void drawAxis()
    {
        List<GuiBodyPartEditor> childList = GuiBase.getCurrentChildren(GuiBodyPartEditor.class);

        /* Draw axis point for body part renderer */
        if (GuiModelRenderer.isRendering() && childList != null && childList.get(0).isSelected(this))
        {
            GL11.glPushMatrix();

            if (GuiTransformations.GuiStaticTransformOrientation.getOrientation() == GuiTransformations.TransformOrientation.GLOBAL)
            {
                Vector3d rot = new Vector3d(Math.toRadians(this.rotate.x), Math.toRadians(this.rotate.y), Math.toRadians(this.rotate.z));

                RenderingUtils.glRevertRotationScale(rot, new Vector3d(this.scale), MatrixUtils.RotationOrder.XYZ);
            }

            RenderSystem.disableDepthTest();

            Draw.point(0, 0, 0);

            if (MorphCore.renderBodyPartAxis.get())
            {
                Draw.axis(0.2F);
            }

            GL11.glPopMatrix();

            RenderSystem.enableDepthTest();
        }
    }

    public void update(AbstractMorph parent, LivingEntity entity)
    {
        entity = this.useTarget ? entity : this.entity;

        if (entity != null && this.enabled)
        {
            if (!this.useTarget)
            {
                this.entity.tickCount++;
            }

            AbstractMorph morph = this.morph.get();

            if (morph != null)
            {
                float bodyYaw = entity.yBodyRot;
                float previousBodyYaw = entity.yBodyRotO;
                float headYaw = entity.getYHeadRot();
                float previousHeadYaw = entity.yHeadRotO;

                entity.setYHeadRot(headYaw - bodyYaw);
                entity.yHeadRotO = previousHeadYaw - previousBodyYaw;
                entity.setYBodyRot(0);
                entity.yBodyRotO = 0;

                morph.update(entity);

                entity.setYBodyRot(bodyYaw);
                entity.yBodyRotO = previousBodyYaw;
                entity.setYHeadRot(headYaw);
                entity.yHeadRotO = previousHeadYaw;
            }
        }
    }

    public boolean canMerge(BodyPart part)
    {
        this.morph.set(part.morph.copy());

        if (Objects.equal(this.limb, part.limb))
        {
            this.lastTranslate = new Vector3f(this.translate);
            this.lastScale = new Vector3f(this.scale);
            this.lastRotate = new Vector3f(this.rotate);
        }
        else
        {
            this.lastTranslate = null;
            this.lastScale = null;
            this.lastRotate = null;
        }

        this.translate.set(part.translate);
        this.scale.set(part.scale);
        this.rotate.set(part.rotate);
        this.useTarget = part.useTarget;
        this.enabled = part.enabled;
        this.animate = part.animate;

        for (int i = 0; i < part.slots.length; i++)
        {
            this.slots[i] = part.slots[i];
        }

        this.limb = part.limb;

        return true;
    }

    public void pause(BodyPart previous, int offset)
    {
        if (previous != null && Objects.equal(this.limb, previous.limb))
        {
            this.lastTranslate = new Vector3f(previous.translate);
            this.lastScale = new Vector3f(previous.scale);
            this.lastRotate = new Vector3f(previous.rotate);
        }

        MorphUtils.pause(this.morph.get(), previous == null ? null : previous.morph.get(), offset);
    }

    public BodyPart genCurrentBodyPart(AbstractMorph morph, float partialTicks)
    {
        BodyPart part = this.copy();

        if (morph instanceof IAnimationProvider)
        {
            Animation animation = ((IAnimationProvider) morph).getAnimation();

            if (animation.isInProgress() && this.lastTranslate != null && this.animate)
            {
                Interpolation inter = animation.interp;
                float factor = animation.getFactor(partialTicks);

                part.translate.x = inter.interpolate(this.lastTranslate.x, this.translate.x, factor);
                part.translate.y = inter.interpolate(this.lastTranslate.y, this.translate.y, factor);
                part.translate.z = inter.interpolate(this.lastTranslate.z, this.translate.z, factor);
                part.scale.x = inter.interpolate(this.lastScale.x, this.scale.x, factor);
                part.scale.y = inter.interpolate(this.lastScale.y, this.scale.y, factor);
                part.scale.z = inter.interpolate(this.lastScale.z, this.scale.z, factor);
                part.rotate.x = inter.interpolate(this.lastRotate.x, this.rotate.x, factor);
                part.rotate.y = inter.interpolate(this.lastRotate.y, this.rotate.y, factor);
                part.rotate.z = inter.interpolate(this.lastRotate.z, this.rotate.z, factor);
            }
        }

        if (this.morph.get() instanceof IMorphGenerator)
        {
            IMorphGenerator generator = (IMorphGenerator) this.morph.get();

            if (generator.canGenerate())
            {
                part.morph.setDirect(generator.genCurrentMorph(partialTicks));
            }
        }

        return part;
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof BodyPart)
        {
            BodyPart part = (BodyPart) obj;

            result = Objects.equal(this.morph, part.morph);
            result = result && Objects.equal(this.translate, part.translate);
            result = result && Objects.equal(this.scale, part.scale);
            result = result && Objects.equal(this.rotate, part.rotate);
            result = result && this.useTarget == part.useTarget;
            result = result && this.enabled == part.enabled;
            result = result && this.animate == part.animate;

            for (int i = 0; i < this.slots.length; i++)
            {
                result = result && ItemStack.matches(this.slots[i], part.slots[i]);
            }

            result = result && Objects.equal(this.limb, part.limb);
        }

        return result;
    }

    public BodyPart copy()
    {
        BodyPart part = new BodyPart();

        part.morph.copy(this.morph);
        part.translate.set(this.translate);
        part.scale.set(this.scale);
        part.rotate.set(this.rotate);
        part.useTarget = this.useTarget;
        part.enabled = this.enabled;
        part.animate = this.animate;

        for (int i = 0; i < this.slots.length; i++)
        {
            part.slots[i] = this.slots[i];
        }

        part.limb = this.limb;

        return part;
    }

    public void fromNBT(CompoundTag tag)
    {
        if (tag.contains("Morph", Tag.TAG_COMPOUND))
        {
            this.morph.fromNBT(tag.getCompound("Morph"));
        }

        if (tag.contains("Items", Tag.TAG_LIST))
        {
            ListTag items = tag.getList("Items", Tag.TAG_COMPOUND);

            for (int i = 0, c = items.size(); i < c && i < this.slots.length; i++)
            {
                CompoundTag compound = items.getCompound(i);
                ItemStack stack = ItemStack.of(compound);

                this.slots[i] = stack;
            }
        }

        float[] values = {this.translate.x, this.translate.y, this.translate.z};
        ListTag translation = tag.getList("T", Tag.TAG_FLOAT);
        if (translation.size() == 3)
        {
            NBTUtils.readFloatList(translation, values);
            this.translate.set(values[0], values[1], values[2]);
        }

        values[0] = this.scale.x;
        values[1] = this.scale.y;
        values[2] = this.scale.z;
        ListTag scale = tag.getList("S", Tag.TAG_FLOAT);
        if (scale.size() == 3)
        {
            NBTUtils.readFloatList(scale, values);
            this.scale.set(values[0], values[1], values[2]);
        }

        values[0] = this.rotate.x;
        values[1] = this.rotate.y;
        values[2] = this.rotate.z;
        ListTag rotation = tag.getList("R", Tag.TAG_FLOAT);
        if (rotation.size() == 3)
        {
            NBTUtils.readFloatList(rotation, values);
            this.rotate.set(values[0], values[1], values[2]);
        }

        if (tag.contains("Target")) this.useTarget = tag.getBoolean("Target");
        if (tag.contains("Enabled")) this.enabled = tag.getBoolean("Enabled");
        if (tag.contains("Animate")) this.animate = tag.getBoolean("Animate");
        if (tag.contains("Limb")) this.limb = tag.getString("Limb");
    }

    public void toNBT(CompoundTag tag)
    {
        CompoundTag morph = this.morph.toNBT();

        if (morph != null)
        {
            tag.put("Morph", morph);
        }

        ListTag list = new ListTag();
        int empty = 0;

        for (int i = 0; i < this.slots.length; i++)
        {
            CompoundTag compound = new CompoundTag();
            ItemStack stack = this.slots[i];

            if (!stack.isEmpty())
            {
                compound = stack.save(compound);
            }
            else
            {
                empty += 1;
            }

            list.add(compound);
        }

        if (empty != this.slots.length)
        {
            tag.put("Items", list);
        }

        if (this.translate.x != 0 || this.translate.y != 0 || this.translate.z != 0)
        {
            tag.put("T", NBTUtils.writeFloatList(new ListTag(), new float[] {this.translate.x, this.translate.y, this.translate.z}));
        }

        if (this.scale.x != 1 || this.scale.y != 1 || this.scale.z != 1)
        {
            tag.put("S", NBTUtils.writeFloatList(new ListTag(), new float[] {this.scale.x, this.scale.y, this.scale.z}));
        }

        if (this.rotate.x != 180 || this.rotate.y != 0 || this.rotate.z != 0)
        {
            tag.put("R", NBTUtils.writeFloatList(new ListTag(), new float[] {this.rotate.x, this.rotate.y, this.rotate.z}));
        }

        if (this.useTarget) tag.putBoolean("Target", this.useTarget);
        if (!this.enabled) tag.putBoolean("Enabled", this.enabled);
        if (!this.animate) tag.putBoolean("Animate", this.animate);
        if (!this.limb.isEmpty()) tag.putString("Limb", this.limb);
    }
}