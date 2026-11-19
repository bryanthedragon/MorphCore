package org.bryanthedragon.morph.utils.keyframes;

public enum KeyframeEasing
{
    IN("in"), OUT("out"), INOUT("inout");

    public final String key;

    private KeyframeEasing(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return "morphlib.easing." + this.key;
    }
}
