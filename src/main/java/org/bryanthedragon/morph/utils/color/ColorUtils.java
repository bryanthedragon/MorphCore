package org.bryanthedragon.morph.utils.color;

import org.apache.commons.lang3.StringUtils;

import org.bryanthedragon.morph.utils.copy.ICopy;
import org.bryanthedragon.morph.utils.math.MathUtils;
import org.bryanthedragon.morph.utils.interpolation.Interpolations;

import com.mojang.blaze3d.systems.RenderSystem;


public class ColorUtils
{
    public static class Color implements ICopy<Color>
    {
        public float r;
        public float g;
        public float b;
        public float a = 1;

        public Color()
        {
            
        }

        public Color(float r, float g, float b)
        {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public Color(float r, float g, float b, float a)
        {
            this(r, g, b);

            this.a = a;
        }

        public Color(int color)
        {
            this(color, true);
        }

        public Color(int color, boolean alpha)
        {
            this.set(color, alpha);
        }

        public Color set(float r, float g, float b, float a)
        {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;

            return this;
        }

        public Color set(float value, int component)
        {
            switch (component)
            {
                case 1:
                    this.r = value;
                break;

                case 2:
                    this.g = value;
                break;

                case 3:
                    this.b = value;
                break;

                default:
                    this.a = value;
                break;
            }

            return this;
        }

        public Color set(int color)
        {
            return this.set(color, true);
        }

        public Color set(int color, boolean alpha)
        {
            this.set((color >> 16 & 0xff) / 255F, (color >> 8 & 0xff) / 255F, (color & 0xff)  / 255F, alpha ? (color >> 24 & 0xff)  / 255F : 1F);

            return this;
        }

        @Override
        public Color copy()
        {
            Color copy = new Color();

            copy.copy(this);

            return copy;
        }

        @Override
        public void copy(Color color)
        {
            this.set(color.r, color.g, color.b, color.a);
        }

        public int getRGBAColor()
        {
            float r = MathUtils.clamp(this.r, 0, 1);
            float g = MathUtils.clamp(this.g, 0, 1);
            float b = MathUtils.clamp(this.b, 0, 1);
            float a = MathUtils.clamp(this.a, 0, 1);

            return ((int) (a * 255) << 24) | ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
        }

        public int getRGBColor()
        {
            return this.getRGBAColor() & 0xffffff;
        }

        public String stringify()
        {
            return this.stringify(false);
        }

        public String stringify(boolean alpha)
        {
            if (alpha)
            {
                return "#" + StringUtils.leftPad(Integer.toHexString(this.getRGBAColor()), 8, '0');
            }

            return "#" + StringUtils.leftPad(Integer.toHexString(this.getRGBColor()), 6, '0');
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof Color)
            {
                Color color = (Color) obj;

                return color.getRGBAColor() == this.getRGBAColor();
            }

            return super.equals(obj);
        }
    }

    public static final int HALF_BLACK = 0x88000000;

    public static final Color COLOR = new Color();

    public static int multiplyColor(int color, float factor)
    {
        COLOR.set(color, true);
        COLOR.r *= factor;
        COLOR.g *= factor;
        COLOR.b *= factor;

        return COLOR.getRGBAColor();
    }

    public static int setAlpha(int color, float alpha)
    {
        COLOR.set(color, true);
        COLOR.a = alpha;

        return COLOR.getRGBAColor();
    }

    public static void interpolate(Color target, int a, int b, float x)
    {
        interpolate(target, a, b, x, true);
    }

    public static void interpolate(Color target, int a, int b, float x, boolean alpha)
    {
        target.set(a, alpha);
        COLOR.set(b, alpha);

        target.r = Interpolations.lerp(target.r, COLOR.r, x);
        target.g = Interpolations.lerp(target.g, COLOR.g, x);
        target.b = Interpolations.lerp(target.b, COLOR.b, x);

        if (alpha)
        {
            target.a = Interpolations.lerp(target.a, COLOR.a, x);
        }
    }

    public static void bindColor(int color)
    {
        COLOR.set(color, true);

        RenderSystem.setShaderColor(COLOR.r, COLOR.g, COLOR.b, COLOR.a);
    }

    public static int rgbaToInt(float r, float g, float b, float a)
    {
        COLOR.set(r, g, b, a);

        return COLOR.getRGBAColor();
    }

    public static int parseColor(String color)
    {
        return parseColor(color, 0);
    }

    public static int parseColor(String color, int orDefault)
    {
        try
        {
            return parseColorWithException(color);
        }
        catch (Exception e)
        {}

        return orDefault;
    }

    public static int parseColorWithException(String color) throws Exception
    {
        if (color.startsWith("#"))
        {
            color = color.substring(1);
        }

        if (color.length() == 6 || color.length() == 8)
        {
            if (color.length() == 8)
            {
                String alpha = color.substring(0, 2);
                String rest = color.substring(2);

                int a = Integer.parseInt(alpha, 16) << 24;
                int rgb = Integer.parseInt(rest, 16);

                return a + rgb;
            }

            return Integer.parseInt(color, 16);
        }

        throw new Exception("Given color \"" + color + "\" can't be parsed!");
    }
}