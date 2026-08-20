package bryanthedragon.morph.utils.matrix;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.joml.Matrix3f;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import javax.annotation.Nullable;

import java.nio.FloatBuffer;

@OnlyIn(Dist.CLIENT)
public class MatrixUtils
{
    /**
     * Model view matrix buffer
     */
    public static final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);

    /**
     * Float array for transferring data from FloatBuffer to the matrix
     */
    public static final float[] floats = new float[16];

    /**
     * Model view matrix captured here
     */
    public static Matrix4f matrix;

    private static final Matrix4d camera = new Matrix4d();

    public static Matrix4d getCameraMatrix()
    {
        return new Matrix4d(camera);
    }

    /**
     * Read OpenGL's model view matrix
     */
    public static Matrix4f readModelView(Matrix4f matrix4f)
    {
        matrix4f.set(RenderSystem.getModelViewMatrix());

        return matrix4f;
    }

    /**
     * This method is called by the ASMAfterCamera method. It saves the camera modelview matrix after the camera is set up.
     * The camera matrix will be used to calculate global transformations of models.
     * Thank you to MiaoNLI for discovering this possibility.
     */
    private static void readCamera()
    {
        camera.set(RenderSystem.getModelViewMatrix());
    }

    public static Matrix4f readModelView()
    {
        return readModelView(new Matrix4f());
    }

    public static Matrix4d readModelViewDouble()
    {
        return new Matrix4d(RenderSystem.getModelViewMatrix());
    }

    /**
     * Replace model view matrix with given matrix
     */
    public static void loadModelView(Matrix4f matrix4f)
    {
        RenderSystem.getModelViewStack().last().pose().set(matrix4f);
        RenderSystem.applyModelViewMatrix();
    }

    /**
     * method to fill the float array with values from the matrix
     */
    public static void matrixToFloat(float[] floats, Matrix4f matrix4f)
    {
        matrix4f.get(floats);
    }

    /**
     * @param floatBuffer with the minimum size of 16
     * @param matrix
     */
    public static void matrixToFloatBuffer(FloatBuffer floatBuffer, Matrix4f matrix)
    {
        matrix.get(floatBuffer);

        floatBuffer.flip();
    }


    public static boolean captureMatrix()
    {
        if (matrix == null)
        {
            matrix = MatrixUtils.readModelView(new Matrix4f());

            return true;
        }

        return false;
    }

    public static void ASMAfterCamera()
    {
        readCamera();
    }

    public static void releaseMatrix()
    {
        matrix = null;
    }

    public static Quaterniond matrixToQuaternion(Matrix3f matrix)
    {
        float[] values = new float[9];
        matrix.get(values);
        double tr = values[0] + values[4] + values[8];
        double qw = 0;
        double qx = 0;
        double qy = 0;
        double qz = 0;

        if (tr > 0)
        {
            double S = Math.sqrt(tr+1.0) * 2; // S=4*qw
            qw = 0.25 * S;
            qx = (values[5] - values[7]) / S;
            qy = (values[2] - values[6]) / S;
            qz = (values[3] - values[1]) / S;
        }
        else if ((values[0] > values[4]) & (values[0] > values[8]))
        {
            double S = Math.sqrt(1.0 + values[0] - values[4] - values[8]) * 2; // S=4*qx
            qw = (values[5] - values[7]) / S;
            qx = 0.25 * S;
                qy = (values[1] + values[3]) / S;
                qz = (values[2] + values[6]) / S;
        }
            else if (values[4] > values[8])
        {
            double S = Math.sqrt(1.0 + values[4] - values[0] - values[8]) * 2; // S=4*qy
            qw = (values[2] - values[6]) / S;
            qx = (values[1] + values[3]) / S;
            qy = 0.25 * S;
                qz = (values[5] + values[7]) / S;
        }
        else
        {
            double S = Math.sqrt(1.0 + values[8] - values[0] - values[4]) * 2; // S=4*qz
            qw = (values[3] - values[1]) / S;
            qx = (values[2] + values[6]) / S;
            qy = (values[5] + values[7]) / S;
            qz = 0.25 * S;
        }

        return new Quaterniond(qx, qy, qz, qw);
    }

    /**
     * This method calculates the angular velocity around the arbitrary axis.
     * The arbitrary axis and the angle around it can be obtained from this result.
     * @param rotation
     * @return the angular velocity vector.
     */
    public static Vector3f getAngularVelocity(Matrix3f rotation)
    {
        Matrix3f step = new Matrix3f(rotation);
        Matrix3f angularVelocity = new Matrix3f();
        Matrix3f i = new Matrix3f();

        i.identity();
        angularVelocity.identity();
        angularVelocity.scale(2);

        step.add(i);
        step.invert();
        step.scale(4);

        angularVelocity.sub(step);

        Vector3f angularV = new Vector3f(angularVelocity.get(2, 1),
                        -angularVelocity.get(2, 0),
                         angularVelocity.get(1, 0));

        return angularV;
    }

    /**
     * Calculate an intrinsic rotation matrix based on the given order and the angles.
     * @param x angle in radians
     * @param y angle in radians
     * @param z angle in radians
     * @param order order of the rotation matrix
     * @return intrinsic rotation matrix
     */
    public static Matrix4f getRotationMatrix(float x, float y, float z, RotationOrder order)
    {
        Matrix4f mat = new Matrix4f();
        Matrix4f rot = new Matrix4f();

        mat.identity();

        switch (order)
        {
            case XYZ:
                rot.rotationZ(z);
                mat.mul(rot);
                rot.rotationY(y);
                mat.mul(rot);
                rot.rotationX(x);
                mat.mul(rot);

                break;
            case ZYX:
                rot.rotationX(x);
                mat.mul(rot);
                rot.rotationY(y);
                mat.mul(rot);
                rot.rotationZ(z);
                mat.mul(rot);

                break;
            case XZY:
                rot.rotationY(y);
                mat.mul(rot);
                rot.rotationZ(z);
                mat.mul(rot);
                rot.rotationX(x);
                mat.mul(rot);

                break;
            case YZX:
                rot.rotationX(x);
                mat.mul(rot);
                rot.rotationZ(z);
                mat.mul(rot);
                rot.rotationY(y);
                mat.mul(rot);

                break;
            case YXZ:
                rot.rotationZ(z);
                mat.mul(rot);
                rot.rotationX(x);
                mat.mul(rot);
                rot.rotationY(y);
                mat.mul(rot);

                break;
            case ZXY:
                rot.rotationY(y);
                mat.mul(rot);
                rot.rotationX(x);
                mat.mul(rot);
                rot.rotationZ(z);
                mat.mul(rot);

                break;
        }

        return mat;
    }

    /**
     * Calculates the current global transformations
     * @return Matrix4d array {translation, rotation, scale} or null if singular matrix exception occured during inverting the camera matrix.
     */
    public static Matrix4d[] getTransformation()
    {
        Matrix4d parent = new Matrix4d(camera);
        Matrix4d translation = new Matrix4d();
        Matrix4d rotation = new Matrix4d();
        Matrix4d scale = new Matrix4d();

        translation.identity();
        rotation.identity();
        scale.identity();

        try
        {
            parent.invert();
        }
        catch (ArithmeticException e)
        {
            return null;
        }

        parent.mul(parent, readModelViewDouble());

        Entity renderViewEntity = Minecraft.getInstance().getCameraEntity();

        Matrix4d cameraTrans = new Matrix4d();

        cameraTrans.identity();
        cameraTrans.m03(renderViewEntity.getPosition(Minecraft.getInstance().getPartialTick()).x);
        cameraTrans.m13(renderViewEntity.getPosition(Minecraft.getInstance().getPartialTick()).y);
        cameraTrans.m23(renderViewEntity.getPosition(Minecraft.getInstance().getPartialTick()).z);

        parent.mul(cameraTrans, parent);

        Vector4d rx = new Vector4d(parent.m00(), parent.m10(), parent.m20(), 0);
        Vector4d ry = new Vector4d(parent.m01(), parent.m11(), parent.m21(), 0);
        Vector4d rz = new Vector4d(parent.m02(), parent.m12(), parent.m22(), 0);

        rx.normalize();
        ry.normalize();
        rz.normalize();
        rotation.setRow(0, rx);
        rotation.setRow(1, ry);
        rotation.setRow(2, rz);

        translation.setTranslation(new Vector3d(parent.m03(), parent.m13(), parent.m23()));

        scale.m00(Math.sqrt(parent.m00() * parent.m00() + parent.m10() * parent.m10() + parent.m20() * parent.m20()));
        scale.m11(Math.sqrt(parent.m01() * parent.m01() + parent.m11() * parent.m11() + parent.m21() * parent.m21()));
        scale.m22(Math.sqrt(parent.m02() * parent.m02() + parent.m12() * parent.m12() + parent.m22() * parent.m22()));

        return new Matrix4d[]{translation, rotation, scale};
    }

    public static Transformation getTransformation(Matrix4d transform)
    {
        Matrix4d translation = new Matrix4d();
        Matrix4d rotation = new Matrix4d();
        Matrix4d scale = new Matrix4d();

        translation.identity();
        rotation.identity();
        scale.identity();

        Vector4d rx = new Vector4d(transform.m00(), transform.m10(), transform.m20(), 0);
        Vector4d ry = new Vector4d(transform.m01(), transform.m11(), transform.m21(), 0);
        Vector4d rz = new Vector4d(transform.m02(), transform.m12(), transform.m22(), 0);

        rx.normalize();
        ry.normalize();
        rz.normalize();
        rotation.setRow(0, rx);
        rotation.setRow(1, ry);
        rotation.setRow(2, rz);

        translation.setTranslation(new Vector3d(transform.m03(), transform.m13(), transform.m23()));

        scale.m00(Math.sqrt(transform.m00() * transform.m00() + transform.m10() * transform.m10() + transform.m20() * transform.m20()));
        scale.m11(Math.sqrt(transform.m01() * transform.m01() + transform.m11() * transform.m11() + transform.m21() * transform.m21()));
        scale.m22(Math.sqrt(transform.m02() * transform.m02() + transform.m12() * transform.m12() + transform.m22() * transform.m22()));

        return new Transformation(translation, rotation, scale);
    }


    /**
     * This method extracts the rotation, translation and scale from the modelview matrix. It needs the view matrix to work properly
     * @author Christian F. (known as Chryfi)
     * @param cameraMatrix The cameraMatrix from rendering so you can extract modelView from OpenGL's matrix.
     * @param modelView The matrix containing the transformations that should be extracted.
     * @return Transformation contains translation, rotation and scale as 4x4 matrices. It also has getter methods for the 3x3 matrices.
     */
    public static Transformation extractTransformations(@Nullable Matrix4f cameraMatrix, Matrix4f modelView)
    {
        return extractTransformations(cameraMatrix, modelView, MatrixMajor.ROW);
    }

    public static Transformation extractTransformations(@Nullable Matrix4f cameraMatrix, Matrix4f modelView, MatrixMajor major)
    {
        Matrix4f parent = new Matrix4f(modelView);

        if (cameraMatrix != null)
        {
            parent.set(cameraMatrix);

            try
            {
                parent.invert();
            }
            catch (ArithmeticException e)
            {
                Transformation transformation = new Transformation();
                transformation.creationException = e;

                return transformation;
            }

            parent.mul(modelView);
        }

        Matrix4f translation = new Matrix4f();
        Matrix4f scale = new Matrix4f();
        Matrix4f rotation = new Matrix4f();

        translation.identity();
        rotation.identity();
        scale.identity();

        translation.m03(parent.m03());
        translation.m13(parent.m13());
        translation.m23(parent.m23());

        Vector4f ax = new Vector4f(parent.m00(), parent.m01(), parent.m02(), 0);
        Vector4f ay = new Vector4f(parent.m10(), parent.m11(), parent.m12(), 0);
        Vector4f az = new Vector4f(parent.m20(), parent.m21(), parent.m22(), 0);

        if (major == MatrixMajor.COLUMN)
        {
            ax = new Vector4f(parent.m00(), parent.m10(), parent.m20(), 0.0F);
            ay = new Vector4f(parent.m01(), parent.m11(), parent.m21(), 0.0F);
            az = new Vector4f(parent.m02(), parent.m12(), parent.m22(), 0.0F);
        }

        ax.normalize();
        ay.normalize();
        az.normalize();
        rotation.setRow(0, ax);
        rotation.setRow(1, ay);
        rotation.setRow(2, az);

        if (major == MatrixMajor.COLUMN)
        {
            rotation.transpose();
        }

        scale.m00((float) Math.sqrt(parent.m00() * parent.m00() + parent.m01() * parent.m01() + parent.m02() * parent.m02()));
        scale.m11((float) Math.sqrt(parent.m10() * parent.m10() + parent.m11() * parent.m11() + parent.m12() * parent.m12()));
        scale.m22((float) Math.sqrt(parent.m20() * parent.m20() + parent.m21() * parent.m21() + parent.m22() * parent.m22()));

        if (major == MatrixMajor.COLUMN)
        {
            scale.m00((float) Math.sqrt(parent.m00() * parent.m00() + parent.m10() * parent.m10() + parent.m20() * parent.m20()));
            scale.m11((float) Math.sqrt(parent.m01() * parent.m01() + parent.m11() * parent.m11() + parent.m21() * parent.m21()));
            scale.m22((float) Math.sqrt(parent.m02() * parent.m02() + parent.m12() * parent.m12() + parent.m22() * parent.m22()));
        }

        return new Transformation(translation, rotation, scale);
    }

    public static class Transformation
    {
        public Matrix4f translation = new Matrix4f();
        public Matrix4f rotation = new Matrix4f();
        public Matrix4f scale = new Matrix4f();
        /**
         * contains the exception that may have
         * occurred during calculation of transformation data
         */
        private Exception creationException = null;

        public Transformation(Matrix4f translation, Matrix4f rotation, Matrix4f scale)
        {
            this.translation.set(translation);
            this.rotation.set(rotation);
            this.scale.set(scale);
        }

        public Transformation(Matrix4d translation, Matrix4d rotation, Matrix4d scale)
        {
            this.translation.set(translation);
            this.rotation.set(rotation);
            this.scale.set(scale);
        }

        public Transformation()
        {
            this.translation.identity();
            this.rotation.identity();
            this.scale.identity();
        }

        public Matrix3f getScale3f()
        {
            Matrix3f scale3f = new Matrix3f();

            scale3f.identity();

            scale3f.m00 = this.scale.m00();
            scale3f.m11 = this.scale.m11();
            scale3f.m22 = this.scale.m22();

            return scale3f;
        }

        public Vector3f getTranslation3f()
        {
            Vector3f translation3f = new Vector3f();

            translation3f.set(this.translation.m03(),this.translation.m13(), this.translation.m23());

            return translation3f;
        }

        public Matrix3f getRotation3f()
        {
            Matrix3f rotation3f = new Matrix3f();

            rotation3f.identity();
            rotation3f.setRow(0, this.rotation.m00(), this.rotation.m01(), this.rotation.m02());
            rotation3f.setRow(1, this.rotation.m10(), this.rotation.m11(), this.rotation.m12());
            rotation3f.setRow(2, this.rotation.m20(), this.rotation.m21(), this.rotation.m22());

            return rotation3f;
        }

        public Exception getCreationException()
        {
            return this.creationException;
        }

        public Vector3f getRotation(RotationOrder order)
        {
            return this.getRotation(order, null);
        }
        public Vector3f getRotation(RotationOrder order, Vector3f ref)
        {
            return this.getRotation(order, ref, 0);
        }
        public Vector3f getRotation(RotationOrder order, int invAxis)
        {
            return this.getRotation(order, null, invAxis);
        }

        /**
         * Get rotation values from matrix<br>
         * <b>It must be called first to determine if the value can be obtained
         * properly</b>
         * 
         * @param order   Rotation Order
         * @param ref     The vector used for reference. Null if nothing to refer. Make
         *                sure that the incoming matrix is not affected by negative
         *                scaling.
         * @param invAxis The axis to be inverted when the matrix is a left-handed
         *                coordinate system. 012 equals xyz
         * @return A vector includes rotation values with specific order, null if matrix
         *         is a illegal rotation matrix.<br>
         *         The returned vector will be as close to the reference vector as
         *         possible
         */
        public Vector3f getRotation(RotationOrder order, Vector3f ref, int invAxis)
        {
            Matrix3f mat = this.getRotation3f();
            float[] rotation = new float[3];
            float[] refFloats = null;
            if (ref != null)
            {
                refFloats = new float[3];
                refFloats[0] = ref.x();
                refFloats[1] = ref.y();
                refFloats[2] = ref.z();
            }

            // DirectX -> OpenGL
            // If the scaling value has an odd number of negative values, this will cause it
            // to become a left-handed coordinate system.
            Vector3f x = new Vector3f(mat.m00(), mat.m10(), mat.m20());
            Vector3f y = new Vector3f(mat.m01(), mat.m11(), mat.m21());
            Vector3f z = new Vector3f(mat.m02(), mat.m12(), mat.m22());
            Vector3f crossY = new Vector3f();
            Vector3f originalY = new Vector3f();
            originalY.normalize(y);
            crossY.cross(z, x);
            crossY.normalize();

            if (crossY.dot(originalY) < 0)
            {
                mat.mul(getInvertAxisMatrix(invAxis));
            }

            Float angle = order.doTest(order.thirdIndex, mat);

            /* if the second rotation value is +/-90, here will be null. */
            if (angle != null)
            {
                if (refFloats != null)
                {
                    angle = refFloats[order.thirdIndex] + Mth.wrapDegrees(2F * (angle - refFloats[order.thirdIndex])) / 2F;
                }
                rotation[order.thirdIndex] = angle;
                mat.mul(getRotationMatrix(order.thirdIndex, -angle), mat);
            }
            else if (refFloats != null)
            {
                rotation[order.thirdIndex] = angle = refFloats[order.thirdIndex];
                mat.mul(getRotationMatrix(order.thirdIndex, -angle), mat);
            }
            angle = order.doTest(order.secondIndex, mat);

            if (angle == null)
            {
                // Illegal: Scale is zero, no rotation information here.
                return null;
            }

            if (refFloats != null)
            {
                angle = refFloats[order.secondIndex] + Mth.wrapDegrees(angle - refFloats[order.secondIndex]);
            }

            rotation[order.secondIndex] = angle;
            mat.mul(getRotationMatrix(order.secondIndex, -angle), mat);

            angle = order.doTest(order.firstIndex, mat);

            if (angle == null)
            {
                return null;
            }
            else if (refFloats != null)
            {
                angle = refFloats[order.firstIndex] + Mth.wrapDegrees(angle - refFloats[order.firstIndex]);
            }

            rotation[order.firstIndex] = angle;

            return new Vector3f(rotation);
        }

        public Vector3f getScale()
        {
            return this.getScale(0);
        }

        public Vector3f getScale(int invAxis)
        {
            Vector3f scale = new Vector3f(this.scale.m00(), this.scale.m11(), this.scale.m22());
            Vector3f x = new Vector3f(this.rotation.m00(), this.rotation.m10(), this.rotation.m20());
            Vector3f y = new Vector3f(this.rotation.m01(), this.rotation.m11(), this.rotation.m21());
            Vector3f z = new Vector3f(this.rotation.m02(), this.rotation.m12(), this.rotation.m22());
            Vector3f crossY = new Vector3f();
            Vector3f originalY = new Vector3f();

            originalY.normalize(y);
            crossY.cross(z, x);
            crossY.normalize();

            if (crossY.dot(originalY) < 0)
            {
                getInvertAxisMatrix(invAxis).transform(scale);
            }

            return scale;
        }

        public static Matrix3f getRotationMatrix(int axis, double degrees)
        {
            Matrix3f mat = new Matrix3f();

            switch (axis)
            {
                case 0:
                    mat.rotationX((float) Math.toRadians(degrees));
                    break;
                case 1:
                    mat.rotationY((float) Math.toRadians(degrees));
                    break;
                case 2:
                    mat.rotationZ((float) Math.toRadians(degrees));
                    break;
            }

            return mat;
        }

        public static Matrix3f getInvertAxisMatrix(int axis)
        {
            Matrix3f mat = new Matrix3f();

            mat.identity();

            switch (axis)
            {
                case 0:
                    mat.m00 = -1;
                    break;
                case 1:
                    mat.m11 = -1;
                    break;
                case 2:
                    mat.m22 = -1;
                    break;
            }

            return mat;
        }
    }

    public enum MatrixMajor
    {
        ROW,
        COLUMN
    }

    public enum RotationOrder
    {
        XYZ, XZY, YXZ, YZX, ZXY, ZYX;

        public final int firstIndex;
        public final int secondIndex;
        public final int thirdIndex;

        private RotationOrder()
        {
            String order = this.name().toUpperCase();
            firstIndex = order.charAt(0) - 'X';
            secondIndex = order.charAt(1) - 'X';
            thirdIndex = order.charAt(2) - 'X';
        }

        public Float doTest(int index, Matrix3f test)
        {
            float[] buffer = new float[3];

            buffer[index == firstIndex ? secondIndex : firstIndex] = 1;

            Vector3f in = new Vector3f(buffer);
            Vector3f out = new Vector3f();

            test.transform(in, out);
            buffer[0] = out.x();
            buffer[1] = out.y();
            buffer[2] = out.z();
            buffer[index] = 0;
            out.set(buffer);

            if (out.length() < 1E-07)
            {
                return null;
            }

            out.normalize();

            float cos = in.dot(out);

            out.cross(in, out);
            buffer[0] = out.x();
            buffer[1] = out.y();
            buffer[2] = out.z();

            float sin = out.length() * Math.signum(buffer[index]);

            return (float) Math.toDegrees(Math.atan2(sin, cos));
        }
    }
}