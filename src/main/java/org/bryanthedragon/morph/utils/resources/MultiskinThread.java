package org.bryanthedragon.morph.utils.resources;

import org.bryanthedragon.morph.utils.reflection.ReflectionUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;

import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Stack;

public class MultiskinThread implements Runnable
{
    private static MultiskinThread instance;
    private static Thread thread;

    public Stack<MultiResourceLocation> locations = new Stack<MultiResourceLocation>();

    public static synchronized void add(MultiResourceLocation location)
    {
        if (instance != null && !thread.isAlive())
        {
            instance = null;
        }

        if (instance == null)
        {
            instance = new MultiskinThread();
            instance.addLocation(location);
            thread = new Thread(instance);
            thread.start();
        }
        else
        {
            instance.addLocation(location);
        }
    }

    public static void clear()
    {
        instance = null;
    }

    /**
     * Create a byte buffer from buffered image
     */
    public static ByteBuffer bytesFromBuffer(BufferedImage image)
    {
        int w = image.getWidth();
        int h = image.getHeight();

        ByteBuffer buffer = ByteBuffer.allocateDirect(w * h * 4);
        int[] pixels = new int[w * h];

        image.getRGB(0, 0, w, h, pixels, 0, w);

        for (int y = 0; y < h; y++)
        {
            for (int x = 0; x < w; x++)
            {
                int pixel = pixels[y * w + x];

                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        buffer.flip();

        return buffer;
    }

    public synchronized void addLocation(MultiResourceLocation location)
    {
        if (this.locations.contains(location))
        {
            return;
        }

        this.locations.add(location);
    }

    @Override
    public void run()
    {
        while (!this.locations.isEmpty() && instance != null)
        {
            MultiResourceLocation location = this.locations.peek();
            AbstractTexture texture = ReflectionUtils.getTextures(Minecraft.getInstance().getTextureManager()).get(location);

            try
            {
                if (texture != null)
                {
                    this.locations.pop();

                    BufferedImage image = TextureProcessor.postProcess(location);
                    int w = image.getWidth();
                    int h = image.getHeight();
                    ByteBuffer buffer = bytesFromBuffer(image);

                    Minecraft.getInstance().execute(() ->
                    {
                        int textureId = texture.getId();

                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
                    });
                }

                Thread.sleep(100);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        instance = null;
        thread = null;
    }
}