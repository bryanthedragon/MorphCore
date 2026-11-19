package org.bryanthedragon.morph.client.gui.framework.elements;

import org.bryanthedragon.morph.client.gui.framework.elements.utils.IViewportStack;

public interface IViewport
{
    public void apply(IViewportStack stack);

    public void unapply(IViewportStack stack);
}