package bryanthedragon.morph.network.morphlib.common;

import io.netty.buffer.ByteBuf;

import bryanthedragon.morph.MorphLib;
import bryanthedragon.morph.permissions.category.PermissionCategory;

import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.Optional;

public class PacketRequestPermission implements IAnswerRequest<Boolean>
{
    private PermissionCategory request;
    private int callbackID = -1;

    public PacketRequestPermission()
    { }

    public PacketRequestPermission(int callbackID, PermissionCategory permission)
    {
        this.callbackID = callbackID;
        this.request = permission;
    }

    @Nullable
    public PermissionCategory getPermissionRequest()
    {
        return this.request;
    }

    @Override
    public void setCallbackID(int callbackID)
    {
        this.callbackID = callbackID;
    }

    @Override
    public Optional<Integer> getCallbackID()
    {
        return Optional.of(this.callbackID == -1 ? null : this.callbackID);
    }

    @Override
    public PacketBoolean getAnswer(Boolean value) throws NoSuchElementException
    {
        return new PacketBoolean(this.getCallbackID().get(), value);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.callbackID = buf.readInt();
        this.request = MorphLib.permissionFactory.getPermission(buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.callbackID);
        buf.writeInt(MorphLib.permissionFactory.getPermissionID(this.request));
    }
}
