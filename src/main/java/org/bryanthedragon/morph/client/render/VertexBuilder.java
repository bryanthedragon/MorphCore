package org.bryanthedragon.morph.client.render;

import com.google.common.collect.ImmutableMap;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

/**
 * Creates vertex formats with the layout used by the rendering helpers.
 */
public final class VertexBuilder
{
    public static final VertexFormat[] cache = new VertexFormat[16];

    private VertexBuilder()
    {
    }

    public static VertexFormat getFormat(boolean color, boolean tex, boolean lightmap, boolean normal)
    {
        int index = (color ? 1 : 0) | (tex ? 1 << 1 : 0) | (lightmap ? 1 << 2 : 0) | (normal ? 1 << 3 : 0);

        if (cache[index] == null)
        {
            ImmutableMap.Builder<String, VertexFormatElement> elements = ImmutableMap.builder();
            elements.put("Position", DefaultVertexFormat.ELEMENT_POSITION);

            if (color)
            {
                elements.put("Color", DefaultVertexFormat.ELEMENT_COLOR);
            }
            else
            {
                addPadding(elements, "Color", 4);
            }

            if (tex)
            {
                elements.put("UV0", DefaultVertexFormat.ELEMENT_UV0);
            }
            else
            {
                addPadding(elements, "UV0", 8);
            }

            if (lightmap)
            {
                elements.put("UV2", DefaultVertexFormat.ELEMENT_UV2);
            }
            else
            {
                addPadding(elements, "UV2", 4);
            }

            if (normal)
            {
                elements.put("Normal", DefaultVertexFormat.ELEMENT_NORMAL);
            }
            else
            {
                addPadding(elements, "Normal", 3);
            }

            elements.put("Padding", DefaultVertexFormat.ELEMENT_PADDING);
            cache[index] = new VertexFormat(elements.build());
        }

        return cache[index];
    }

    private static void addPadding(ImmutableMap.Builder<String, VertexFormatElement> elements, String name, int count)
    {
        for (int i = 0; i < count; i++)
        {
            elements.put(name + i, DefaultVertexFormat.ELEMENT_PADDING);
        }
    }
}
