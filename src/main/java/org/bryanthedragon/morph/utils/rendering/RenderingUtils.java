package bryanthedragon.morph.utils.rendering;

import bryanthedragon.morph.utils.reflection.ReflectionUtils;
import bryanthedragon.morph.utils.color.Color;
import bryanthedragon.morph.utils.matrix.MatrixUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;

public class RenderingUtils
{
    /**
     * This method inverts the scale and rotation of the modelview matrix and
     * multiplies it on the OpenGL stack.
     */
    public static void glRevertRotationScale()
    {
        RenderSystem.getModelViewStack().mulPoseMatrix(getRevertRotationScale());
        RenderSystem.applyModelViewMatrix();
    }

    public static org.joml.Matrix4f getRevertRotationScale()
    {
        Matrix4d[] transformation = MatrixUtils.getTransformation();
        if (transformation == null)
        {
            return new org.joml.Matrix4f();
        }

        Matrix4d invertRotScale = new Matrix4d().identity();
        invertRotScale.m00(transformation[2].m00() == 0 ? 0 : 1 / transformation[2].m00());
        invertRotScale.m11(transformation[2].m11() == 0 ? 0 : 1 / transformation[2].m11());
        invertRotScale.m22(transformation[2].m22() == 0 ? 0 : 1 / transformation[2].m22());

        Matrix4d inverseRotation = new Matrix4d(transformation[1]);
        try
        {
            inverseRotation.invert();
        }
        catch (ArithmeticException e)
        {
            inverseRotation.identity();
        }

        invertRotScale.mul(inverseRotation);
        return new org.joml.Matrix4f(invertRotScale);
    }


    /**
     * This method inverts the scale and rotation using the provided parameters
     * and multiplies the OpenGL matrix stack with it.
     * @param scale
     * @param rotation angles in radians
     * @param rotationOrder the order of the rotation
     */
    public static void glRevertRotationScale(Vector3d rotation, Vector3d scale, MatrixUtils.RotationOrder rotationOrder)
    {
        double invSx = (scale.x != 0) ? 1 / scale.x : 0;
        double invSy = (scale.y != 0) ? 1 / scale.y : 0;
        double invSz = (scale.z != 0) ? 1 / scale.z : 0;

        org.joml.Matrix4f inverse = new org.joml.Matrix4f().scaling((float) invSx, (float) invSy, (float) invSz);

        float rotx = (float) -Math.toDegrees(rotation.x);
        float roty = (float) -Math.toDegrees(rotation.y);
        float rotz = (float) -Math.toDegrees(rotation.z);

        switch (rotationOrder)
        {
            case ZYX:
                inverse.rotate(new Quaternionf().rotationZ((float) Math.toRadians(rotz)));
                inverse.rotate(new Quaternionf().rotationY((float) Math.toRadians(roty)));
                inverse.rotate(new Quaternionf().rotationX((float) Math.toRadians(rotx)));

                break;
            case XYZ:
                inverse.rotate(new Quaternionf().rotationX((float) Math.toRadians(rotx)));
                inverse.rotate(new Quaternionf().rotationY((float) Math.toRadians(roty)));
                inverse.rotate(new Quaternionf().rotationZ((float) Math.toRadians(rotz)));

                break;
            case XZY:
                inverse.rotate(new Quaternionf().rotationX((float) Math.toRadians(rotx)));
                inverse.rotate(new Quaternionf().rotationZ((float) Math.toRadians(rotz)));
                inverse.rotate(new Quaternionf().rotationY((float) Math.toRadians(roty)));

                break;
            case YZX:
                inverse.rotate(new Quaternionf().rotationY((float) Math.toRadians(roty)));
                inverse.rotate(new Quaternionf().rotationZ((float) Math.toRadians(rotz)));
                inverse.rotate(new Quaternionf().rotationX((float) Math.toRadians(rotx)));

                break;
            case YXZ:
                inverse.rotate(new Quaternionf().rotationY((float) Math.toRadians(roty)));
                inverse.rotate(new Quaternionf().rotationX((float) Math.toRadians(rotx)));
                inverse.rotate(new Quaternionf().rotationZ((float) Math.toRadians(rotz)));

                break;
            case ZXY:
                inverse.rotate(new Quaternionf().rotationZ((float) Math.toRadians(rotz)));
                inverse.rotate(new Quaternionf().rotationX((float) Math.toRadians(rotx)));
                inverse.rotate(new Quaternionf().rotationY((float) Math.toRadians(roty)));

                break;
        }

        RenderSystem.getModelViewStack().mulPoseMatrix(inverse);
        RenderSystem.applyModelViewMatrix();

    }

    public static void renderCircle(Vector3d center, Vector3d normal, float radius, int divisions, Color color, float thickness)
    {
        renderCircleDotted(center, normal, radius, divisions, color, thickness, 0);
    }

    public static void renderCircleDotted(Vector3d center, Vector3d normal, float radius, int divisions, Color color, float thickness, int skipDivision)
    {
        Matrix3d rotation = new Matrix3d();
        rotation.setIdentity();
        Matrix3d transform = new Matrix3d();
        transform.rotY(Math.toRadians(getYaw(normal)));
        rotation.mul(transform);
        transform.rotX(Math.toRadians(getPitch(normal)));
        rotation.mul(transform);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.lineWidth(thickness);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);

        for (int i = 1; i <= divisions; i += skipDivision + 1)
        {
            double angle0 = 2 * Math.PI / divisions * (i - 1);
            double angle1 = 2 * Math.PI / divisions * i;

            Vector3d a = new Vector3d(radius * Math.cos(angle0), radius * Math.sin(angle0), 0);
            Vector3d b = new Vector3d(radius * Math.cos(angle1), radius * Math.sin(angle1), 0);
            rotation.transform(a);
            rotation.transform(b);
            a.add(center);
            b.add(center);

            buffer.vertex(a.x, a.y, a.z).color(color.r, color.g, color.b, color.a).endVertex();
            buffer.vertex(b.x, b.y, b.z).color(color.r, color.g, color.b, color.a).endVertex();
        }

        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.lineWidth(1.0F);
    }

    public static float getYaw(Vector3f direction)
    {
        return (float) getYaw(new Vector3d(direction));
    }

    public static float getPitch(Vector3f direction)
    {
        return (float) getPitch(new Vector3d(direction));
    }

    public static double getYaw(Vector3d direction)
    {
        double yaw = Math.atan2(-direction.x, direction.z);
        yaw = Math.toDegrees(yaw);
        if (yaw < -180)
        {
            yaw += 360;
        }
        else if (yaw > 180)
        {
            yaw -= 360;
        }
        return -yaw;
    }

    public static double getPitch(Vector3d direction)
    {
        double pitch = Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z));
        return -Math.toDegrees(pitch);
    }

    public static void renderImage(ResourceLocation image, float scale)
    {
        renderImage(image, scale, new Color(1F, 1F, 1F, 1F));
    }

    public static void renderImage(ResourceLocation image, float scale, Color color)
    {
        RenderSystem.setShaderTexture(0, image);

        boolean isCulling = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        if (ReflectionUtils.isOptifineShadowPass())
        {
            RenderSystem.disableCull();
        }
        else
        {
            RenderSystem.enableCull();
        }

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        boolean mirrored = Minecraft.getInstance().options.getCameraType().isMirrored();
        float width = scale * (mirrored ? -1 : 1) * 0.5F;
        float height = scale * 0.5F;

        /* Frontface */
        addImageVertex(buffer, -width, height, 0, 0, 0, color);
        addImageVertex(buffer, -width, -height, 0, 0, 1, color);
        addImageVertex(buffer, width, -height, 0, 1, 1, color);
        addImageVertex(buffer, width, height, 0, 1, 0, color);
        /* backface */
        addImageVertex(buffer, width, height, 0, 1, 0, color);
        addImageVertex(buffer, width, -height, 0, 1, 1, color);
        addImageVertex(buffer, -width, -height, 0, 0, 1, color);
        addImageVertex(buffer, -width, height, 0, 0, 0, color);

        BufferUploader.drawWithShader(buffer.end());

        if (isCulling)
        {
            RenderSystem.enableCull();
        }
        else
        {
            RenderSystem.disableCull();
        }

        RenderSystem.disableBlend();
    }

    private static void addImageVertex(BufferBuilder buffer, float x, float y, float z, float u, float v, Color color)
    {
        buffer.vertex(x, y, z).uv(u, v).color(color.r, color.g, color.b, color.a).endVertex();
    }

    public static Matrix4f getFacingRotation(Facing facing, Vector3f position)
    {
        return getFacingRotation(facing, position, null);
    }

    public static Matrix4f getFacingRotation(Facing facing, Vector3f position, Vector3f direction)
    {
        if (facing.isDirection && direction == null)
        {
            throw new IllegalArgumentException("Argument direction cannot be null when the facing mode has isDirection=true");
        }

        Minecraft mc = Minecraft.getInstance();
        Entity camera = mc.getCameraEntity();
        float partialTicks = mc.getFrameTime();
        Matrix4f transform = new Matrix4f();
        Matrix4f rotation = new Matrix4f();

        transform.identity();

        float cYaw = -Mth.rotLerp(partialTicks, camera.yRotO, camera.getYRot());
        float cPitch = Mth.lerp(partialTicks, camera.xRotO, camera.getXRot());
        double cX = Mth.lerp(partialTicks, camera.xOld, camera.getX());
        double cY = Mth.lerp(partialTicks, camera.yOld, camera.getY()) + camera.getEyeHeight();
        double cZ = Mth.lerp(partialTicks, camera.zOld, camera.getZ());

        if (facing.isLookAt && !facing.isDirection)
        {
            double dX = cX - position.x;
            double dY = cY - position.y;
            double dZ = cZ - position.z;
            double horizontalDistance = Math.sqrt(dX * dX + dZ * dZ);

            cYaw = 180 - (float) (Math.toDegrees(Mth.atan2(dZ, dX)) - 90.0F);
            cPitch = (float) Math.toDegrees(Mth.atan2(dY, horizontalDistance));
        }

        if (facing.isDirection)
        {
            double lengthSq = direction.lengthSquared();
            if (lengthSq < 0.0001)
            {
                direction.set(1, 0, 0);
            }
            else if (Math.abs(lengthSq - 1) > 0.0001)
            {
                direction.normalize();
            }
        }

        switch (facing)
        {
            case LOOKAT_XYZ:
            case ROTATE_XYZ:
                rotation.rotateX((float) Math.toRadians(cPitch));
                transform.mul(rotation);
                rotation.rotateY((float) Math.toRadians(180 - cYaw));
                transform.mul(rotation);
                break;
            case ROTATE_Y:
            case LOOKAT_Y:
                rotation.rotateY((float) Math.toRadians(180 - cYaw));
                transform.mul(rotation);
                break;
            case LOOKAT_DIRECTION:
                rotation.identity();
                rotation.rotateY((float) Math.toRadians(getYaw(direction)));
                transform.mul(rotation);
                rotation.rotateX((float) Math.toRadians(getPitch(direction) + 90));
                transform.mul(rotation);

                Vector3f cameraDir = new Vector3f((float) (cX - position.x), (float) (cY - position.y), (float) (cZ - position.z));

                Vector4f rotatedNormal4 = transform.transform(new Vector4f(0, 0, 1, 0));
                Vector3f rotatedNormal = new Vector3f(rotatedNormal4.x, rotatedNormal4.y, rotatedNormal4.z);

                /*
                 * The direction vector is the normal of the plane used for calculating the rotation around local y Axis.
                 * Project the cameraDir onto that plane to find out the axis angle (direction vector is the y axis).
                 */
                Vector3f projectDir = new Vector3f(direction);
                projectDir.scale(cameraDir.dot(direction));
                cameraDir.sub(projectDir);

                if (cameraDir.lengthSquared() < 1.0e-30) break;

                cameraDir.normalize();

                /*
                 * The angle between two vectors is only between 0 and 180 degrees.
                 * RotationDirection will be parallel to direction but pointing in different directions depending
                 * on the rotation of cameraDir. Use this to find out the sign of the angle
                 * between cameraDir and the rotatedNormal.
                 */
                Vector3f rotationDirection = new Vector3f();
                rotationDirection.cross(cameraDir, rotatedNormal);

                rotation.rotateY(-Math.copySign(cameraDir.angle(rotatedNormal), rotationDirection.dot(direction)));
                transform.mul(rotation);
                break;
        }

        return transform;
    }

    /**
     * This method multiples the openGL matrix stack with a rotation matrix
     * according to the facing parameter
     */
    public static void glFacingRotation(Facing facing, Vector3f position, Vector3f direction)
    {
        RenderSystem.getModelViewStack().mulPoseMatrix(getFacingRotation(facing, position, direction));
        RenderSystem.applyModelViewMatrix();
    }

    public static void glFacingRotation(Facing facing, Vector3f position)
    {
        glFacingRotation(facing, position, null);
    }

    public enum Facing
    {
        ROTATE_XYZ("rotate_xyz"),
        ROTATE_Y("rotate_y"),
        LOOKAT_XYZ("lookat_xyz", true, false),
        LOOKAT_Y("lookat_y", true, false),
        LOOKAT_DIRECTION("lookat_direction", true, true);


        public final String id;
        public final boolean isLookAt;
        public final boolean isDirection;

        public static Facing fromString(String string)
        {
            for (Facing facing : values())
            {
                if (facing.id.equals(string))
                {
                    return facing;
                }
            }

            return null;
        }

        Facing(String id, boolean isLookAt, boolean isDirection)
        {
            this.id = id;
            this.isLookAt = isLookAt;
            this.isDirection = isDirection;
        }

        Facing(String id)
        {
            this(id, false, false);
        }
    }
}
