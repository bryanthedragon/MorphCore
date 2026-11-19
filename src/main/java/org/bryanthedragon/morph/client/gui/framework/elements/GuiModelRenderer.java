package org.bryanthedragon.morph.client.gui.framework.elements;

import org.bryanthedragon.morph.MorphLib;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiContext;
import org.bryanthedragon.morph.client.gui.framework.elements.utils.gui.GuiDraw;
import org.bryanthedragon.morph.utils.dummy.DummyUtils.DummyEntity;
import org.bryanthedragon.morph.utils.math.MathUtils;
import org.bryanthedragon.morph.utils.matrix.MatrixUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.math.Axis;

import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * Model renderer GUI element
 * 
 * This base class can be used for full screen model viewer. 
 */
public abstract class GuiModelRenderer extends GuiElement
{
    private static boolean rendering;
    private static Vector3d vec = new Vector3d();
    private static Matrix3d mat = new Matrix3d();
    protected Matrix4d cameraMatrix = new Matrix4d();
    private Matrix4f previousProjection;

    protected LivingEntity entity;
    protected BlockState block = Blocks.GRASS.defaultBlockState();

    protected int timer;
    protected boolean dragging;
    protected boolean position;
    protected Vector3f temp = new Vector3f();

    public float fov = 70.0F;
    public float scale;
    public float yaw;
    public float pitch;
    public Vector3f pos = new Vector3f();
    public boolean flight;

    public boolean hideModel;
    public boolean fullScreen;

    public Consumer<GuiContext> beforeRender;
    public Consumer<GuiContext> afterRender;

    public boolean customEntity;
    public float entityPitch;
    public float entityYawHead;
    public float entityYawBody;
    public int entityTicksExisted;

    protected float lastX;
    protected float lastY;

    /* Picking */
    protected boolean tryPicking;
    protected Consumer<String> callback;

    private long tick;

    public static boolean isRendering()
    {
        return rendering;
    }

    public static void disableRenderingFlag()
    {
        rendering = false;
    }

    public GuiModelRenderer(Minecraft mc)
    {
        super(mc);

        this.entity = new DummyEntity(mc.level);
        this.entity.setYRot(0.0F);
        this.entity.yRotO = 0.0F;
        this.entity.setXRot(0.0F);
        this.entity.xRotO = 0.0F;
        this.entity.setYHeadRot(0.0F);
        this.entity.yHeadRotO = 0.0F;
        this.entity.setYBodyRot(0.0F);
        this.entity.yBodyRotO = 0.0F;
        this.entity.setOnGround(true);
        this.reset();
    }

    public Matrix4d getCameraMatrix()
    {
        return new Matrix4d(this.cameraMatrix);
    }

    public GuiModelRenderer picker(Consumer<String> callback)
    {
        this.callback = callback;

        return this;
    }

    public void setRotation(float yaw, float pitch)
    {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public void setPosition(float x, float y, float z)
    {
        this.pos.set(x, y, z);
    }

    public void setScale(float scale)
    {
        this.scale = scale;
    }

    public LivingEntity getEntity()
    {
        return this.entity;
    }

    public void reset()
    {
        this.yaw = 0;
        this.pitch = 0;
        this.scale = 2;
        this.pos = new Vector3f(0, 1, 0);

        this.hideModel = false;
        this.fullScreen = false;

        this.beforeRender = null;
        this.afterRender = null;

        this.customEntity = false;
        this.entityPitch = 0F;
        this.entityYawHead = 0F;
        this.entityYawBody = 0F;
        this.entityTicksExisted = 0;
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseClicked(context))
        {
            return true;
        }

        if (this.area.isInside(context) && (context.mouseButton == 0 || context.mouseButton == 2))
        {
            this.dragging = true;
            this.flight = false;
            this.position = Screen.hasShiftDown() || context.mouseButton == 2;
            this.lastX = context.mouseX;
            this.lastY = context.mouseY;

            if (Screen.hasControlDown())
            {
                this.tryPicking = true;
                this.dragging = false;
            }
        }

        return this.area.isInside(context);
    }

    @Override
    public boolean mouseScrolled(GuiContext context)
    {
        if (super.mouseScrolled(context))
        {
            return true;
        }

        if (this.area.isInside(context))
        {
            this.scale += Math.copySign(this.getZoomFactor(), context.mouseWheel);
            this.scale = MathUtils.clamp(this.scale, 0, 100);
        }

        return this.area.isInside(context);
    }

    protected float getZoomFactor()
    {
        if (this.scale < 1) return 0.05F;
        if (this.scale > 30) return 5F;
        if (this.scale > 10) return 1F;
        if (this.scale > 3) return 0.5F;

        return 0.1F;
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        this.dragging = false;
        this.tryPicking = false;

        if (this.flight)
        {
            this.flight = false;

            vec.set(0, 0, -this.scale);
            this.rotateVector(vec);

            this.pos.x -= vec.x;
            this.pos.y -= vec.y;
            this.pos.z -= vec.z;
        }

        super.mouseReleased(context);
    }

    @Override
    public boolean keyTyped(GuiContext context)
    {
        if (this.dragging && !this.position)
        {
            if (context.keyCode == GLFW.GLFW_KEY_W || context.keyCode == GLFW.GLFW_KEY_S || context.keyCode == GLFW.GLFW_KEY_A ||
                    context.keyCode == GLFW.GLFW_KEY_D || context.keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || context.keyCode == GLFW.GLFW_KEY_SPACE)
            {
                if (!this.flight)
                {
                    this.flight = true;

                    vec.set(0, 0, -this.scale);
                    this.rotateVector(vec);

                    this.pos.x += vec.x;
                    this.pos.y += vec.y;
                    this.pos.z += vec.z;
                }

                return true;
            }
        }

        return super.keyTyped(context);
    }

    @Override
    public void draw(GuiContext context)
    {
        this.updateLogic(context);

        rendering = true;

        GuiDraw.scissor(this.area.x, this.area.y, this.area.w, this.area.h, context);
        this.drawModel(context);
        GuiDraw.unscissor(context);

        rendering = false;

        super.draw(context);

        this.updatePosition(context);
    }

    private void updateLogic(GuiContext context)
    {
        long i = context.tick - this.tick;

        if (i > 10)
        {
            i = 10;
        }

        while (i > 0)
        {
            this.update();
            i --;
        }

        this.tick = context.tick;
    }

    /**
     * Update logic
     */
    protected void update()
    {
        this.timer = this.mc.player != null ? this.mc.player.tickCount : this.timer + 1;
        this.entity.tickCount = this.timer;
    }

    /**
     * Draw currently edited model
     */
    private void drawModel(GuiContext context)
    {
        this.setupViewport(context);
        this.setupPosition(context);
        this.setupEntity();

        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        /* Setup transformations */
        var modelView = RenderSystem.getModelViewStack();
        modelView.pushPose();
        modelView.setIdentity();
        modelView.mulPose(Axis.XP.rotationDegrees(this.pitch));
        modelView.mulPose(Axis.YP.rotationDegrees(this.yaw));
        modelView.translate(-this.temp.x, -this.temp.y, -this.temp.z);
        RenderSystem.applyModelViewMatrix();
        this.cameraMatrix = MatrixUtils.readModelViewDouble();

        /* Custom render settings */
        if (this.hideModel)
        {
            RenderSystem.depthFunc(GL11.GL_NEVER);
        }

        /* Drawing begins */
        this.drawGround();

        if (this.beforeRender != null)
        {
            this.beforeRender.accept(context);
        }

        this.drawUserModel(context);

        if (this.afterRender != null)
        {
            this.afterRender.accept(context);
        }

        /* Reset custom settings */
        if (this.hideModel)
        {
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
        }

        modelView.popPose();
        RenderSystem.applyModelViewMatrix();

        /* Disable rendering states */
        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        /* Return back to orthographic projection */
        RenderSystem.viewport(0, 0, this.mc.getWindow().getWidth(), this.mc.getWindow().getHeight());
        RenderSystem.setProjectionMatrix(this.previousProjection, VertexSorting.ORTHOGRAPHIC_Z);
        this.previousProjection = null;
    }

    protected void updatePosition(GuiContext context)
    {
        int mouseX = context.mouseX;
        int mouseY = context.mouseY;

        if (this.dragging)
        {
            if (this.position)
            {
                float x = this.pos.x;
                float y = this.pos.y;
                float z = this.pos.z;

                double xx = -(this.lastX - mouseX) / 60F;
                double yy = -(this.lastY - mouseY) / 60F;
                float factor = this.getZoomFactor();

                xx *= factor / 0.1F;
                yy *= factor / 0.1F;

                if (xx != 0 || yy != 0)
                {
                    vec.set(xx, yy, 0);
                    this.rotateVector(vec);

                    x += vec.x;
                    y += vec.y;
                    z += vec.z;

                    this.pos.set(x, y, z);
                }
            }
            else
            {
                this.yaw -= this.lastX - mouseX;
                this.pitch -= this.lastY - mouseY;
            }

            this.lastX = mouseX;
            this.lastY = mouseY;
        }

        if (this.dragging && !this.position)
        {
            float multiplier = 4F / Math.max(Minecraft.getInstance().getFps(), 1);

            if (GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS)
            {
                multiplier *= 5;
            }
            else if (GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS)
            {
                multiplier *= 0.2F;
            }

            vec.set(0, 0, 0);

            if (GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS)
            {
                vec.z++;
            }

            if (GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS)
            {
                vec.z--;
            }

            if (GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS)
            {
                vec.x++;
            }

            if (GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS)
            {
                vec.x--;
            }

            mat.rotateY((180 - this.yaw) / 180 * (float) Math.PI);
            mat.transform(vec);

            if (GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS)
            {
                vec.y++;
            }

            if (GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS)
            {
                vec.y--;
            }

            if (vec.length() > 0)
            {
                vec.normalize();
            }

            this.pos.x += vec.x * multiplier;
            this.pos.y += vec.y * multiplier;
            this.pos.z += vec.z * multiplier;
        }
    }

    protected void setupPosition(GuiContext context)
    {
        this.temp = new Vector3f(this.pos);

        if (this.flight)
        {
            return;
        }

        vec.set(0, 0, -this.scale);
        this.rotateVector(vec);

        this.temp.x += vec.x;
        this.temp.y += vec.y;
        this.temp.z += vec.z;
    }

    private void rotateVector(Vector3d vec)
    {
        mat.rotateX(this.pitch / 180 * (float) Math.PI);
        mat.transform(vec);
        mat.rotateY((180 - this.yaw) / 180 * (float) Math.PI);
        mat.transform(vec);
    }

    protected void setupViewport(GuiContext context)
    {
        /* Changing projection mode to perspective. In order for this to
         * work, depth buffer must also be cleared. Thanks to Gegy for
         * pointing this out (depth buffer)! */
        this.previousProjection = new Matrix4f(RenderSystem.getProjectionMatrix());
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);

        float rx = (float) Math.ceil(mc.getWindow().getGuiScaledWidth() / (double) context.screen.width);
        float ry = (float) Math.ceil(mc.getWindow().getGuiScaledHeight() / (double) context.screen.height);

        int vx = this.fullScreen ? 0 : (int) (this.area.x * rx);
        int vy = this.fullScreen ? 0 : (int) (this.mc.getWindow().getHeight() - (this.area.y + this.area.h) * ry);
        int vw = this.fullScreen ? this.mc.getWindow().getWidth() : (int) (this.area.w * rx);
        int vh = this.fullScreen ? this.mc.getWindow().getHeight() : (int) (this.area.h * ry);

        RenderSystem.viewport(vx, vy, vw, vh);
        Matrix4f projection = new Matrix4f().perspective((float) Math.toRadians(this.fov), (float) vw / vh, 0.05F, 1000.0F);
        RenderSystem.setProjectionMatrix(projection, VertexSorting.DISTANCE_TO_ORIGIN);
    }

    protected void setupEntity()
    {
        if (this.customEntity)
        {
            this.entity.setXRot(this.entityPitch);
            this.entity.xRotO = this.entityPitch;
            this.entity.setYHeadRot(this.entityYawHead);
            this.entity.yHeadRotO = this.entityYawHead;
            this.entity.setYBodyRot(this.entityYawBody);
            this.entity.yBodyRotO = this.entityYawBody;
            this.entity.tickCount = this.entityTicksExisted;
        }
        else
        {
            this.entity.setXRot(0);
            this.entity.xRotO = 0;
            this.entity.setYHeadRot(0);
            this.entity.yHeadRotO = 0;
            this.entity.setYBodyRot(0);
            this.entity.yBodyRotO = 0;
        }
    }

    /**
     * Draw your model here 
     */
    protected abstract void drawUserModel(GuiContext context);

    /**
     * IMPORTANT: this method should be called manually by the subclass right
     * after rendering the model
     */
    protected void tryPicking(GuiContext context)
    {
        if (!this.tryPicking)
        {
            return;
        }

        float rx = (float) Math.ceil(mc.getWindow().getGuiScaledWidth() / (double) context.screen.width);
        float ry = (float) Math.ceil(mc.getWindow().getGuiScaledHeight() / (double) context.screen.height);

        int x = (int) (context.mouseX * rx);
        int y = (int) (mc.getWindow().getGuiScaledHeight() - (context.mouseY) * ry);

        GL11.glClearStencil(0);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        if (this.hideModel)
        {
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
        }

        GL11.glColorMask(false, false, false, false);
        this.drawForStencil(context);
        GL11.glColorMask(true, true, true, true);

        if (this.hideModel)
        {
            RenderSystem.depthFunc(GL11.GL_NEVER);
        }

        ByteBuffer buffer = ByteBuffer.allocateDirect(1);
        GL11.glReadPixels(x, y, 1, 1, GL11.GL_STENCIL_INDEX, GL11.GL_UNSIGNED_BYTE, buffer);

        buffer.rewind();

        if (this.callback != null)
        {
            int value = Byte.toUnsignedInt(buffer.get());

            if (value > 0)
            {
                this.callback.accept(this.getStencilValue(value));
            }
        }

        this.tryPicking = false;
    }

    /**
     * Here you should draw your own things into stencil
     */
    protected void drawForStencil(GuiContext context)
    {}

    protected String getStencilValue(int value)
    {
        return null;
    }

    /**
     * Render block of grass under the model (which signify where 
     * located the ground below the model) 
     */
    @SuppressWarnings("deprecation")
    protected void drawGround()
    {
        if (MorphLib.enableGridRendering.get())
        {
            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder buffer = tessellator.getBuilder();

            GL11.glLineWidth(3);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            buffer.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);

            for (int x = 0; x <= 10; x ++)
            {
                if (x == 0)
                {
                    buffer.vertex(x - 5, 0, -5).color(0F, 0F, 1F, 0.75F).endVertex();
                    buffer.vertex(x - 5, 0, 5).color(0F, 0F, 1F, 0.75F).endVertex();
                }
                else
                {
                    buffer.vertex(x - 5, 0, -5).color(0.25F, 0.25F, 0.25F, 0.75F).endVertex();
                    buffer.vertex(x - 5, 0, 5).color(0.25F, 0.25F, 0.25F, 0.75F).endVertex();
                }
            }

            for (int x = 0; x <= 10; x ++)
            {
                if (x == 10)
                {
                    buffer.vertex(-5, 0, x - 5).color(1F, 0F, 0F, 0.75F).endVertex();
                    buffer.vertex(5, 0, x - 5).color(1F, 0F, 0F, 0.75F).endVertex();
                }
                else
                {
                    buffer.vertex(-5, 0, x - 5).color(0.25F, 0.25F, 0.25F, 0.75F).endVertex();
                    buffer.vertex(5, 0, x - 5).color(0.25F, 0.25F, 0.25F, 0.75F).endVertex();
                }
            }

            BufferUploader.drawWithShader(buffer.end());
        }
        else
        {
            BlockRenderDispatcher renderer = this.mc.getBlockRenderer();
            var modelView = RenderSystem.getModelViewStack();
            modelView.pushPose();
            modelView.translate(0, -0.5F, 0);
            modelView.mulPose(Axis.YP.rotationDegrees(-90.0F));
            modelView.translate(-0.5F, -0.5F, 0.5F);

            MultiBufferSource.BufferSource buffers = this.mc.renderBuffers().bufferSource();
            renderer.renderSingleBlock(this.block, modelView, buffers, 15728880, OverlayTexture.NO_OVERLAY);
            buffers.endBatch();

            modelView.translate(0.0F, 0.0F, 1.0F);
            modelView.popPose();
            RenderSystem.applyModelViewMatrix();
        }
    }
}