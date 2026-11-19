package org.bryanthedragon.morph.client;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

public class Draw
{
    public static void axis(float length)
    {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        GL11.glLineWidth(5);
        buffer.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(0, 0, 0).color(0, 0, 0, 1F).endVertex();
        buffer.vertex(length, 0, 0).color(0, 0, 0, 1F).endVertex();
        buffer.vertex(0, 0, 0).color(0, 0, 0, 1F).endVertex();
        buffer.vertex(0, length, 0).color(0, 0, 0, 1F).endVertex();
        buffer.vertex(0, 0, 0).color(0, 0, 0, 1F).endVertex();
        buffer.vertex(0, 0, length).color(0, 0, 0, 1F).endVertex();
        tessellator.end();

        GL11.glLineWidth(3);
        buffer.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(0, 0, 0).color(1F, 0, 0, 1F).endVertex();
        buffer.vertex(length, 0, 0).color(1F, 0, 0, 1F).endVertex();
        buffer.vertex(0, 0, 0).color(0, 1F, 0, 1F).endVertex();
        buffer.vertex(0, length, 0).color(0, 1F, 0, 1F).endVertex();
        buffer.vertex(0, 0, 0).color(0, 0, 1F, 1F).endVertex();
        buffer.vertex(0, 0, length).color(0, 0, 1F, 1F).endVertex();
        tessellator.end();
        GL11.glLineWidth(1);

        point(0, 0, 0);
    }

    public static void point(double x, double y, double z)
    {
        GL11.glPointSize(12);
        GL11.glBegin(GL11.GL_POINTS);
        GL11.glColor3d(0, 0, 0);
        GL11.glVertex3d(0, 0, 0);
        GL11.glEnd();

        GL11.glPointSize(10);
        GL11.glBegin(GL11.GL_POINTS);
        GL11.glColor3d(1, 1, 1);
        GL11.glVertex3d(0, 0, 0);
        GL11.glEnd();
        GL11.glPointSize(1);
    }

    public static void cube(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha)
    {
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        cube(buffer, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        tessellator.end();
    }

    public static void cube(BufferBuilder buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha)
    {
        /* Top */
        buffer.vertex(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();

        /* Bottom */
        buffer.vertex(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();

        /* Left */
        buffer.vertex(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();

        /* Right */
        buffer.vertex(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();

        /* Front */
        buffer.vertex(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(minX, minY, minZ).color(red, green, blue, alpha).endVertex();

        /* Back */
        buffer.vertex(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.vertex(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
    }
}