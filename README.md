# MorphCore

## FAQ

**Q: My screen goes up and down. How to fix?**  
A: Most likely you installed MorePlayerModels mod alongside MorphCore. To fix that, either go to `Mod Options > MorphCore > General` and enable `Disable POV` option, or disable POV option in MPM's config and restart the game.

## Install

Install [Minecraft Forge](http://files.minecraftforge.net/), download the latest stable version of jar file for available Minecraft version. Also install

After that, MorphCore mod should be installed and will appear in Minecraft's mods menu. If MorphCore didn't appear in the mods menu, then something went wrong.

## How to play

To morph into other creatures, you must to acquire a *morph* first. To acquire a *morph*, you have to kill a mob, then a ghost of a mob will appear (that's a *morph*), and when you'll pick it up, you'll acquire a *morph*.

Once you have at least one *morph*, you're able to transform into that *morph*. Open survival morph menu by pressing `X` key (for old users `N` key). In the menu you can select the morphs either with mouse or with arrow keys. Press **Morph** button to morph (or `Return` key). You can also favorite morphs (all the non favorite morphs can be toggled by clicking **Only favorites** toggle or `O` key). You can add a keybind to acquired morphs, which can be used inside menu and in the game.

To demorph, you can press demorph key (which is `.` by default).

You retain your *morphs* even after death (although you can disable retaining of morphs after death in mod options). In creative mode, you can select almost any available *morph* via Creative Morph Menu which can be opened by pressing `B` key in the game.

## About morphs

Every *morph* you equip, or apply, grant you different look, size, and health capacity. Except those properties, every *morph* also has special *abilities*, *attack* and *action*. Only vanilla morphs are have configuration, for modded mobs you'll have to [configure them](https://github.com/mchorse/morphcore/wiki/Configuring-Morphs).

*Abilities* give special properties to *morphs*. **Chicken** *morph* will slowly fall and get no fall damage. **Spider** and **CaveSpider** *morphs* will be able to climb on the wall. **Bat** *morph* will be able to fly and has a night vision.

*Attack* is an effect which is applied onto attacked entity. For example, if you hit some entity while being in **CaveSpider** *morph*, your target will gain **poison** potion effect. If you'll hit an entity while being in **VillagerGolem** *morph*, you'll throw your target high in the air.

*Action* is a special ability that is triggered by "action key" (which is `V` by default). Examples: in **Rabbit** *morph*, you'll jump high; in **Creeper** *morph*, you'll explode like a creeper, and so on.

## License

This project is licensed under MIT. See `LICENSE.md` file.
