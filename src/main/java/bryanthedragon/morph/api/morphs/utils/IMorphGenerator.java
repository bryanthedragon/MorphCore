package bryanthedragon.morph.api.morphs.utils;

import bryanthedragon.morph.api.morphs.AbstractMorph;

public interface IMorphGenerator
{
    public boolean canGenerate();

    public AbstractMorph genCurrentMorph(float partialTicks);
}
