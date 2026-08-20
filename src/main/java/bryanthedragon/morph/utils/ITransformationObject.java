package bryanthedragon.morph.utils;

import bryanthedragon.morph.client.gui.framework.elements.input.GuiTransformations;

public interface ITransformationObject
{
    void addTranslation(double x, double y, double z, GuiTransformations.TransformOrientation orientation);
}
