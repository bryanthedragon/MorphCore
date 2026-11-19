package org.bryanthedragon.morph.client.gui.utils.resizers;

import org.bryanthedragon.morph.client.gui.utils.Area;

public interface IParentResizer
{
    public void apply(Area area, IResizer resizer, ChildResizer child);
}
