package org.bryanthedragon.morph.api.morphs.utils;

import org.bryanthedragon.morph.api.morphs.main.AbstractMorph;

public interface IMorphGenerator
{
    public boolean canGenerate();

    public AbstractMorph genCurrentMorph(float partialTicks);
}
