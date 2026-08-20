## GTNH-ify the 1710 build system
* Replace the obsolete ForgeGradle 1.2 build with the GTNH convention stack and Gradle 8.11.1 wrapper configuration.
* Build Minecraft 1.7.10 against Forge 10.13.4.1614 and stable MCP mappings version 12 while retaining Java 8 bytecode.
* Compile the legacy `assets/recipehandler` Java source tree and package only its language resources and root metadata files.
* Generate `assets.recipehandler.Tags` for the mod version and expand GTNH project properties in `mcmod.info`.

== NoMoreRecipeConflict 0.3 ==
* More custom crafting support ?
* Keep crafting result selected on shift-click

== NoMoreRecipeConflict 0.2 ==
* Added cycle button in crafting gui (enabled by default)
* Changed switch key to disabled by default
* Added config options to choose between key or button
* Added extra checks server side to avoid trouble

== NoMoreRecipeConflict 0.1 ==
* Port to Forge
* Removed all base class edits
* Added SMP support
* Added mcmod.info and pack.mcmeta files
* Changed left/right keys to custom switch key ('add' by default)
* Obfuscated "version-independent"
* Added M.U.D support
