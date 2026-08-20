package bryanthedragon.morph.loader;

import bryanthedragon.morph.loader.state.MorphLoaderState;

public final class MorphLoader
{
    private static final MorphLoader INSTANCE = new MorphLoader();

    private volatile MorphLoaderState loaderState = MorphLoaderState.CONSTRUCTION;

    private MorphLoader()
    {
        
    }

    public static MorphLoader instance()
    {
        return INSTANCE;
    }

    public MorphLoaderState getLoaderState()
    {
        return this.loaderState;
    }

    public void setLoaderState(MorphLoaderState loaderState)
    {
        if (loaderState.ordinal() < this.loaderState.ordinal())
        {
            throw new IllegalArgumentException("Loader state cannot move backwards");
        }

        this.loaderState = loaderState;
    }
}
