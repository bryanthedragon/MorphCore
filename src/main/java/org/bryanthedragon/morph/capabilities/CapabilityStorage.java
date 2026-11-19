package org.bryanthedragon.morph.capabilities;

import java.util.concurrent.Callable;

import javax.annotation.Nullable;

import com.google.common.base.Throwables;

import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;

import net.minecraftforge.common.capabilities.Capability;

public class CapabilityStorage<T>
{
    public static interface IStorage<T> 
    {
        /**
         * Serialize the capability instance to a NBTTag.
         * This allows for a central implementation of saving the data.
         *
         * It is important to note that it is up to the API defining
         * the capability what requirements the 'instance' value must have.
         *
         * Due to the possibility of manipulating internal data, some
         * implementations MAY require that the 'instance' be an instance
         * of the 'default' implementation.
         *
         * Review the API docs for more info.
         *
         * @param capability The Capability being stored.
         * @param instance An instance of that capabilities interface.
         * @param side The side of the object the instance is associated with.
         * @return a NBT holding the data. Null if no data needs to be stored.
         */
        @Nullable
        Tag writeNBT(CapabilityStorage<T> capability, T instance, Direction side);

        /**
         * Read the capability instance from a NBT tag.
         *
         * This allows for a central implementation of saving the data.
         *
         * It is important to note that it is up to the API defining
         * the capability what requirements the 'instance' value must have.
         *
         * Due to the possibility of manipulating internal data, some
         * implementations MAY require that the 'instance' be an instance
         * of the 'default' implementation.
         *
         * Review the API docs for more info.         *
         *
         * @param capability The Capability being stored.
         * @param instance An instance of that capabilities interface.
         * @param side The side of the object the instance is associated with.
         * @param nbt A NBT holding the data. Must not be null, as doesn't make sense to call this function with nothing to read...
         */
        void readNBT(CapabilityStorage<T> capability, T instance, Direction side, Tag nbt);
    }

    /**
     * @return The unique name of this capability, typically this is
     * the fully qualified class name for the target interface.
     */
    public String getName() { return name; }

    /**
     * @return An instance of the default storage handler. You can safely use this store your default implementation in NBT.
     */
    public IStorage<T> getStorage() { return storage; }
    
    /**
     * Quick access to the IStorage's readNBT. 
     * See {@link IStorage#readNBT(Capability, Object, Direction, Tag)}  for documentation.
     */
    public void readNBT(T instance, Direction side, Tag nbt)
    {
    	storage.readNBT(this, instance, side, nbt); 
    }
    
    /**
     * Quick access to the IStorage's writeNBT. 
     * See {@link IStorage#writeNBT(Capability, Object, Direction)} for documentation.
     */
    @Nullable
    public Tag writeNBT(T instance, Direction side)
    {
    	return storage.writeNBT(this, instance, side);
    }

    /**
     * A NEW instance of the default implementation.
     *
     * If it important to note that if you want to use the default storage
     * you may be required to use this exact implementation.
     * Refer to the owning API of the Capability in question.
     *
     * @return A NEW instance of the default implementation.
     */
    @Nullable
    public T getDefaultInstance()
    {
        try
        {
            return this.factory.call();
        }
        catch (Exception e)
        {
            Throwables.throwIfUnchecked(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Use this inside ICapabilityProvider.getCapability to avoid unchecked cast warnings.
     * Example: return SOME_CAPABILITY.cast(instance);
     * Use with caution;
     */
    @SuppressWarnings("unchecked")
    public <R> R cast(T instance)
    {
        return (R)instance;
    }

    // INTERNAL
    private final String name;
    private final IStorage<T> storage;
    private final Callable<? extends T> factory;

    public CapabilityStorage(String name, IStorage<T> storage, Callable<? extends T> factory)
    {
        this.name = name;
        this.storage = storage;
        this.factory = factory;
    }
}
