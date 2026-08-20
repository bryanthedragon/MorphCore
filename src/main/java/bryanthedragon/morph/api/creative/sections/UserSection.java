package bryanthedragon.morph.api.creative.sections;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import bryanthedragon.morph.utils.JsonUtils;
import bryanthedragon.morph.MorphCore;
import bryanthedragon.morph.api.MorphManager;
import bryanthedragon.morph.api.creative.categories.AcquiredCategory;
import bryanthedragon.morph.api.creative.categories.MorphCategory;
import bryanthedragon.morph.api.creative.categories.RecentCategory;
import bryanthedragon.morph.api.creative.categories.UserCategory;
import bryanthedragon.morph.api.morphs.main.AbstractMorph;
import bryanthedragon.morph.capabilities.morphing.IMorphing;
import bryanthedragon.morph.capabilities.morphing.Morphing;
import bryanthedragon.morph.client.gui.creative.GuiCreativeMorphsList;
import bryanthedragon.morph.client.gui.creative.GuiMorphSection;
import bryanthedragon.morph.client.gui.creative.GuiUserSection;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * User morph section
 *
 * Here we store acquired morphs, recently edited morphs and custom
 * categories created by the player
 */
public class UserSection extends MorphSection
{
    public AcquiredCategory acquired;
    public RecentCategory recent;

    public boolean loaded = false;
    public List<UserCategory> global = new ArrayList<UserCategory>();

    public UserSection(String title)
    {
        super(title);

        this.acquired = new AcquiredCategory(this, "acquired");
        this.recent = new RecentCategory(this, "recent");
    }

    @Override
    public void add(MorphCategory category)
    {
        super.add(category);

        if (category instanceof UserCategory)
        {
            this.global.add((UserCategory) category);
        }

        this.save();
    }

    @Override
    public void remove(MorphCategory category)
    {
        super.remove(category);

        if (category instanceof UserCategory)
        {
            this.global.remove(category);
        }

        this.save();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void update(Level world)
    {
        super.update(world);

        IMorphing morphing = Morphing.get(Minecraft.getInstance().player);

        this.categories.clear();
        this.categories.add(this.acquired);
        this.categories.add(this.recent);
        this.acquired.setMorphs(morphing == null ? Collections.emptyList() : morphing.getAcquiredMorphs());

        if (!this.loaded)
        {
            this.load();
            this.loaded = true;
        }

        this.categories.addAll(this.global);
    }

    @Override
    public void reset()
    {
        super.reset();

        if (this.loaded)
        {
            this.save();
            this.loaded = false;
        }

        this.categories.clear();
        this.acquired.setMorphs(Collections.emptyList());
        this.recent.clear();
        this.global.clear();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public GuiMorphSection getGUI(Minecraft mc, GuiCreativeMorphsList parent, Consumer<GuiMorphSection> callback)
    {
        return new GuiUserSection(mc, parent, this, callback);
    }

    @SuppressWarnings("deprecation")
    public void load()
    {
        File file = MorphCore.proxy.list;

        if (!file.exists())
        {
            return;
        }

        try
        {
            List<UserCategory> categories = new ArrayList<UserCategory>();
            String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            JsonArray object = new JsonParser().parse(content).getAsJsonArray();
            int i = 0;

            for (JsonElement entry : object)
            {
                JsonObject cat = entry.getAsJsonObject();
                UserCategory category = new UserCategory(this, cat.get("title").getAsString());

                if (cat.has("hidden") && cat.get("hidden").isJsonPrimitive())
                {
                    category.hidden = cat.get("hidden").getAsBoolean();
                }

                if (cat.has("morphs"))
                {
                    for (JsonElement string : cat.get("morphs").getAsJsonArray())
                    {
                        try {
                            AbstractMorph morph = MorphManager.INSTANCE.morphFromNBT(JsonToNBT.getTagFromJson(string.getAsString()));

                            if (morph != null)
                            {
                                category.add(morph);
                                i ++;
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

                categories.add(category);
            }

            System.out.println("Loading " + categories.size() + " categories with " + i + " morphs!");

            this.global = categories;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void save()
    {
        if (!this.loaded)
        {
            return;
        }

        JsonArray array = new JsonArray();
        int i = 0;

        for (UserCategory category : this.global)
        {
            JsonObject cat = new JsonObject();
            JsonArray morphs = new JsonArray();

            cat.addProperty("title", category.getTitle());
            cat.addProperty("hidden", category.hidden);
            cat.add("morphs", morphs);

            for (AbstractMorph morph : category.getMorphs())
            {
                if (morph != null)
                {
                    morphs.add(morph.toNBT().toString());

                    i ++;
                }
            }

            array.add(cat);
        }

        System.out.println("Saving " + array.size() + " categories with " + i + " morphs to list.json!");

        try
        {
            FileUtils.writeStringToFile(MorphCore.proxy.list, JsonUtils.jsonToPretty(array), StandardCharsets.UTF_8);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
