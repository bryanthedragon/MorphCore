package bryanthedragon.morph.capabilities.render;

import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class EntitySelector
{
    public boolean enabled = true;
    public String name = "";
    public String type = "";
    public CompoundTag match;
    public CompoundTag morph;

    public boolean matches(LivingEntity target)
    {
        if (target == null || !this.enabled)
        {
            return false;
        }

        Component nameTag = target.getName();
        target.getType();
        ResourceLocation entityName = EntityType.getKey(null);

        if (entityName == null)
        {
            entityName = new ResourceLocation(target instanceof Player ? "player" : "");
        }

        String n = this.name;
        String t = this.type;

        boolean negativeName = n.startsWith("!");
        boolean negativeType = t.startsWith("!");

        if (negativeName) n = n.substring(1);
        if (negativeType) t = t.substring(1);

        ResourceLocation rt = new ResourceLocation(t);

        @SuppressWarnings("unlikely-arg-type")
        boolean matchesName = this.name.isEmpty() || negativeName != nameTag.equals(n);
        boolean matchesType = this.type.equals("*") || negativeType != entityName.equals(rt);

        if (matchesName && matchesType)
        {
            if (this.match != null && !this.match.isEmpty())
            {
                return this.match(target.serializeNBT());
            }

            return true;
        }

        return false;
    }

    private boolean match(CompoundTag entityTag)
    {
        for (String key : this.match.getAllKeys())
        {
            Tag value = this.match.get(key);
            Tag entityValue = entityTag.get(key);

            if (!value.equals(entityValue))
            {
                return false;
            }
        }

        return true;
    }
}