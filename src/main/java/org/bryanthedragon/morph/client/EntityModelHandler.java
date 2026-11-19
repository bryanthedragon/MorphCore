package org.bryanthedragon.morph.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.bryanthedragon.morph.utils.json.JsonUtils;
import org.bryanthedragon.morph.MorphCore;
import org.bryanthedragon.morph.capabilities.render.EntitySelector;
import org.bryanthedragon.morph.capabilities.render.EntitySelectorAdapter;
import org.bryanthedragon.morph.capabilities.render.IModelRenderer;
import org.bryanthedragon.morph.capabilities.render.ModelRenderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    public void onRenderLiving(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event)
    {
        LivingEntity entity = event.getEntity();
        IModelRenderer cap = ModelRenderer.get(entity);

        if (cap != null && currentRendering == null)
        {
            currentRendering = entity;

            if (cap.render(entity, entity.getX(), entity.getY(), entity.getZ(), event.getPartialTick()))
            {
                event.setCanceled(true);
            }

            currentRendering = null;
        }
    }

    @SubscribeEvent
    public void onUpdateEntity(LivingTickEvent event)
    {
        LivingEntity entity = (LivingEntity) event.getEntity();
        IModelRenderer cap = ModelRenderer.get(entity);

        if (cap != null && entity.level().isClientSide)
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