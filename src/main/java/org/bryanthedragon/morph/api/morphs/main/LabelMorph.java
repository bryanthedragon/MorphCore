package bryanthedragon.morph.api.morphs.main;

import bryanthedragon.morph.utils.math.MathUtils;
import bryanthedragon.morph.utils.matrix.MatrixUtils;
import bryanthedragon.morph.utils.text.TextUtils;
import bryanthedragon.morph.utils.color.Color;
import bryanthedragon.morph.utils.color.ColorUtils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.Objects;

public class LabelMorph extends AbstractMorph
{
    public static final String DEFAULT_LABEL = "Lorem ipsum";
    public static final Matrix4f matrix = new Matrix4f();

    public String label = DEFAULT_LABEL;
    public int max = -1;
    public float anchorX = 0.5F;
    public float anchorY = 0.5F;
    public int color = 0xffffff;
    public boolean lighting = true;

    /* Shadow properties */
    public boolean shadow = false;
    public float shadowX = 1F;
    public float shadowY = 1F;
    public int shadowColor = 0;

    /* Background */
    public int background = 0x00000000;
    public float offset = 3;

    public boolean billboard;

    public LabelMorph()
    {
        this.name = "label";
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderOnScreen(Player player, int x, int y, float scale, float alpha)
    {
        var modelView = RenderSystem.getModelViewStack();
        modelView.pushPose();
        modelView.translate(x, y, 10);
        RenderSystem.applyModelViewMatrix();
        this.renderString(15728880);
        modelView.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(LivingEntity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        /* Approximately 16 letters per block */
        double scale = 1D / 6D / 8D;

        var modelView = RenderSystem.getModelViewStack();
        modelView.pushPose();
        modelView.translate(x, y, z);
        modelView.scale((float) scale, (float) -scale, (float) scale);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.disableCull();

        if (this.billboard)
        {
            Matrix4f matrix4f = MatrixUtils.readModelView(matrix);
            Vector4f zero = new Vector4f(0, 0, 0, 1);

            matrix4f.transform(zero);
            matrix4f.identity();
            matrix4f.setTranslation(new Vector3f(zero.x, zero.y, zero.z));
            matrix4f.transpose();

            MatrixUtils.loadModelView(matrix4f);
            modelView.scale((float) -scale, (float) scale, (float) scale);
            modelView.mulPose(Axis.YP.rotationDegrees(180.0F));
            modelView.mulPose(Axis.XP.rotationDegrees(180.0F));
            RenderSystem.applyModelViewMatrix();
        }

        int light = this.lighting ? LevelRenderer.getLightColor(entity.level(), entity.blockPosition()) : 15728880;
        this.renderString(light);
        modelView.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableCull();
    }

    @OnlyIn(Dist.CLIENT)
    private void renderString(int packedLight)
    {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        String text = TextUtils.processColoredText(this.label);

        if (this.max <= 0)
        {
            int w = font.width(text);
            int x = -(int) (w * this.anchorX);
            int y = -(int) (font.lineHeight * this.anchorY);

            this.drawShadow(x, y, w, font.lineHeight);

            if (this.shadow)
            {
                this.drawString(font, buffers, text, x + this.shadowX, y + this.shadowY, this.shadowColor, packedLight);
            }

            this.drawString(font, buffers, text, x, y, this.color, packedLight);
        }
        else
        {
            int min = 6;

            for (int i = 0; i < text.length(); i++)
            {
                min = Math.max(font.width(String.valueOf(text.charAt(i))), min);
            }
            
            int max = MathUtils.clamp(this.max, min, Integer.MAX_VALUE);
            List<FormattedCharSequence> labels = font.split(Component.literal(text), max);
            int h = MathUtils.clamp(labels.size() - 1, 0, 100) * 12 + font.lineHeight;
            int y = -(int) (h * this.anchorY);

            this.drawShadow(-(int) (max * this.anchorX), y, max, h);

            if (this.shadow)
            {
                for (FormattedCharSequence label : labels)
                {
                    int w = font.width(label);

                    this.drawString(font, buffers, label, -(int) (w * this.anchorX) + this.shadowX, y + this.shadowY, this.shadowColor, packedLight);
                    y += 12;
                }
            }

            y = -(int) (h * this.anchorY);

            for (FormattedCharSequence label : labels)
            {
                int w = font.width(label);

                this.drawString(font, buffers, label, -(int) (w * this.anchorX), y, this.color, packedLight);
                y += 12;
            }
        }

        buffers.endBatch();
    }

    private void drawString(Font font, MultiBufferSource.BufferSource buffers, String text, float x, float y, int color, int packedLight)
    {
        font.drawInBatch(text, x, y, color, false, RenderSystem.getModelViewMatrix(), buffers, Font.DisplayMode.NORMAL, 0, packedLight);
    }

    private void drawString(Font font, MultiBufferSource.BufferSource buffers, FormattedCharSequence text, float x, float y, int color, int packedLight)
    {
        font.drawInBatch(text, x, y, color, false, RenderSystem.getModelViewMatrix(), buffers, Font.DisplayMode.NORMAL, 0, packedLight);
    }

    @OnlyIn(Dist.CLIENT)
    private void drawShadow(int x, int y, int w, int h)
    {
        Color color = ColorUtils.COLOR.set(this.background, true);

        if (color.a <= 0)
        {
            return;
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        var modelView = RenderSystem.getModelViewStack();
        modelView.pushPose();
        modelView.translate(0, 0, -0.2F);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(modelView.last().pose(), x + w + this.offset, y - this.offset, 0).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.vertex(modelView.last().pose(), x - this.offset, y - this.offset, 0).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.vertex(modelView.last().pose(), x - this.offset, y + h + this.offset, 0).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.vertex(modelView.last().pose(), x + w + this.offset, y + h + this.offset, 0).color(color.r, color.g, color.b, color.a).endVertex();
        BufferUploader.drawWithShader(buffer.end());
        modelView.popPose();
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof LabelMorph)
        {
            LabelMorph label = (LabelMorph) obj;

            result = result && Objects.equals(this.label, label.label);
            result = result && this.max == label.max;
            result = result && this.anchorX == label.anchorX;
            result = result && this.anchorY == label.anchorY;
            result = result && this.color == label.color;
            result = result && this.shadow == label.shadow;
            result = result && this.shadowX == label.shadowX;
            result = result && this.shadowY == label.shadowY;
            result = result && this.shadowColor == label.shadowColor;
            result = result && this.lighting == label.lighting;
            result = result && this.background == label.background;
            result = result && this.offset == label.offset;
            result = result && this.billboard == label.billboard;
        }

        return result;
    }

    @Override
    public AbstractMorph create()
    {
        return new LabelMorph();
    }

    @Override
    public void copy(AbstractMorph from)
    {
        super.copy(from);

        if (from instanceof LabelMorph)
        {
            LabelMorph label = (LabelMorph) from;

            this.label = label.label;
            this.max = label.max;
            this.anchorX = label.anchorX;
            this.anchorY = label.anchorY;
            this.color = label.color;
            this.shadow = label.shadow;
            this.shadowX = label.shadowX;
            this.shadowY = label.shadowY;
            this.shadowColor = label.shadowColor;
            this.lighting = label.lighting;
            this.background = label.background;
            this.offset = label.offset;
            this.billboard = label.billboard;
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

        if (!this.label.equals(DEFAULT_LABEL)) tag.putString("Label", this.label);
        if (this.max > 0) tag.putInt("Max", this.max);
        if (this.anchorX != 0.5F) tag.putFloat("AnchorX", this.anchorX);
        if (this.anchorY != 0.5F) tag.putFloat("AnchorY", this.anchorY);
        if (this.color != 0xffffff) tag.putInt("Color", this.color);
        if (this.shadow) tag.putBoolean("Shadow", this.shadow);
        if (this.shadowX != 1F) tag.putFloat("ShadowX", this.shadowX);
        if (this.shadowY != 1F) tag.putFloat("ShadowY", this.shadowY);
        if (this.shadowColor != 0) tag.putInt("ShadowColor", this.shadowColor);
        if (!this.lighting) tag.putBoolean("Lighting", this.lighting);
        if (this.background != 0) tag.putInt("Background", this.background);
        if (this.offset != 3) tag.putFloat("Offset", this.offset);
        if (this.billboard) tag.putBoolean("Billboard", this.billboard);
    }

    @Override
    public void fromNBT(CompoundTag tag)
    {
        super.fromNBT(tag);

        if (tag.contains("Label")) this.label = tag.getString("Label");
        if (tag.contains("Max")) this.max = tag.getInt("Max");
        if (tag.contains("AnchorX")) this.anchorX = tag.getFloat("AnchorX");
        if (tag.contains("AnchorY")) this.anchorY = tag.getFloat("AnchorY");
        if (tag.contains("Color")) this.color = tag.getInt("Color");
        if (tag.contains("Shadow")) this.shadow = tag.getBoolean("Shadow");
        if (tag.contains("ShadowX")) this.shadowX = tag.getFloat("ShadowX");
        if (tag.contains("ShadowY")) this.shadowY = tag.getFloat("ShadowY");
        if (tag.contains("ShadowColor")) this.shadowColor = tag.getInt("ShadowColor");
        if (tag.contains("Lighting")) this.lighting = tag.getBoolean("Lighting");
        if (tag.contains("Background")) this.background = tag.getInt("Background");
        if (tag.contains("Offset")) this.offset = tag.getFloat("Offset");
        if (tag.contains("Billboard")) this.billboard = tag.getBoolean("Billboard");
    }
}