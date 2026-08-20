package bryanthedragon.morph.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import bryanthedragon.morph.utils.JsonUtils;
import bryanthedragon.morph.MorphCore;
import bryanthedragon.morph.capabilities.render.EntitySelector;
import bryanthedragon.morph.capabilities.render.EntitySelectorAdapter;
import bryanthedragon.morph.capabilities.render.IModelRenderer;
import bryanthedragon.morph.capabilities.render.ModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity model handler. This handler is responsible for rendering 
 * models on player.
 */
@OnlyIn(Dist.CLIENT)
public class EntityModelHandler
{
    /**
     * Entity selectors
     */
    public static final List<EntitySelector> selectors = new ArrayList<EntitySelector>();

    /**
     * Entity selector GSON 
     */
    public Gson entitySelector;

    public Entity currentRendering;

    public EntityModelHandler()
    {
        /* Create GSON builder */
        GsonBuilder gson = new GsonBuilder();

        gson.registerTypeAdapter(EntitySelector.class, new EntitySelectorAdapter());
        gson.setPrettyPrinting();

        this.entitySelector = gson.create();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderLiving(RenderLivingEvent.Pre<LivingEntity> event)
    {
        LivingEntity entity = event.getEntity();
        IModelRenderer cap = ModelRenderer.get(entity);

        if (cap != null && currentRendering == null)
        {
            currentRendering = entity;

            if (cap.render(entity, event.getX(), event.getY(), event.getZ(), Minecraft.getInstance().getRenderPartialTicks()))
            {
                event.setCanceled(true);
            }

            currentRendering = null;
        }
    }

    @SubscribeEvent
    public void onUpdateEntity(LivingUpdateEvent event)
    {
        LivingEntity entity = event.getEntityLiving();
        IModelRenderer cap = ModelRenderer.get(entity);

        if (cap != null && entity.world.isRemote)
        {
            cap.update(entity);
        }
    }

    public void loadSelectors()
    {
        File selectorsFile = MorphCore.proxy.selectors;

        if (selectorsFile.exists())
        {
            try
            {
                Type token = (new TypeToken<List<EntitySelector>>() {}).getType();
                List<EntitySelector> selectors = this.entitySelector.fromJson(FileUtils.readFileToString(selectorsFile, StandardCharsets.UTF_8), token);

                EntityModelHandler.selectors.clear();
                EntityModelHandler.selectors.addAll(selectors);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void saveSelectors()
    {
        try
        {
            JsonElement element = this.entitySelector.toJsonTree(selectors);

            FileUtils.writeStringToFile(MorphCore.proxy.selectors, JsonUtils.jsonToPretty(element), StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}