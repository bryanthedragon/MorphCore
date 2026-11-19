package org.bryanthedragon.morph.client.gui.framework.elements.list;

import java.util.List;
import java.util.function.Consumer;

import org.bryanthedragon.morph.client.gui.framework.GuiBase;
import org.bryanthedragon.morph.client.gui.utils.Icon;
import org.bryanthedragon.morph.client.gui.utils.Icons;
import org.bryanthedragon.morph.utils.files.entries.AbstractEntry;
import org.bryanthedragon.morph.utils.files.entries.FileEntry;
import org.bryanthedragon.morph.utils.files.entries.FolderEntry;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * Folder entry list GUI
 * 
 * This GUI list element allows to navigate through the file tree 
 * entries. 
 */
public class GuiFolderEntryListElement extends GuiListElement<AbstractEntry>
{
    public Consumer<FileEntry> fileCallback;
    public ResourceLocation rl;
    public FolderEntry parent;

    public GuiFolderEntryListElement(Minecraft mc, Consumer<FileEntry> fileCallback)
    {
        super(mc, null);

        this.callback = (list) ->
        {
            AbstractEntry entry = list.get(0);

            if (entry instanceof FileEntry)
            {
                if (this.fileCallback != null)
                {
                    this.fileCallback.accept(((FileEntry) entry));
                }
            }
            else if (entry.isFolder())
            {
                this.setFolder((FolderEntry) entry);
            }
        };
        this.fileCallback = fileCallback;
        this.scroll.scrollItemSize = 16;
        this.scroll.scrollSpeed = 16;
    }

    /**
     * Set current folder
     */
    public void setFolder(FolderEntry folder)
    {
        /* Quick jump to children folder that has only one folder */
        if (folder.getEntries().size() <= 2 && !folder.isTop())
        {
            for (AbstractEntry subEntry : folder.getEntries())
            {
                if (subEntry.isFolder())
                {
                    FolderEntry subFolder = (FolderEntry) subEntry;

                    if (!subFolder.isTop())
                    {
                        this.setFolder(subFolder);

                        return;
                    }
                }
            }
        }

        this.setDirectFolder(folder);
    }

    public void setDirectFolder(FolderEntry folder)
    {
        List<AbstractEntry> entries = folder.getEntries();
        List<AbstractEntry> current = this.getCurrent();

        this.parent = folder;
        this.setList(entries);
        this.setCurrent(current.isEmpty() ? null : current.get(0));

        if (this.current.isEmpty())
        {
            this.setCurrent(this.rl);
        }
    }

    public ResourceLocation getCurrentResource()
    {
        List<AbstractEntry> entry = this.getCurrent();

        if (!entry.isEmpty() && entry.get(0) instanceof FileEntry)
        {
            return ((FileEntry) entry.get(0)).resource;
        }

        return null;
    }

    public void setCurrent(ResourceLocation rl)
    {
        this.setIndex(-1);

        if (rl == null)
        {
            return;
        }

        for (int i = 0, c = this.list.size(); i < c; i++)
        {
            AbstractEntry entry = this.list.get(i);

            if (entry instanceof FileEntry && ((FileEntry) entry).resource.equals(rl))
            {
                this.setIndex(i);
                break;
            }
        }
    }

    @Override
    protected void drawElementPart(AbstractEntry element, int i, int x, int y, boolean hover, boolean selected)
    {
        GuiGraphics graphics = GuiBase.getCurrent().graphics;
        Icon icon = element instanceof FolderEntry ? Icons.FOLDER : Icons.SERVER;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1, 1, 1, hover ? 0.8F : 0.6F);

        graphics.blit(icon.location, x + 2, y, icon.x, icon.y, icon.w, icon.h, icon.textureW, icon.textureH);

        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
        graphics.drawString(this.font, element.title, x + 20, y + 4, hover ? 16777120 : 0xffffff, true);
    }
}