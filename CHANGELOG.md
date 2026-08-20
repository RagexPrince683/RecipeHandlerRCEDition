## Fix Forge 1.7.10 bulk-crafting input compatibility
* Replace the unavailable `GuiScreenEvent.MouseInputEvent.Pre` hook with end-of-client-tick mouse polling.
* Preserve alternate-recipe bulk shift-click crafting while limiting requests to one per held click on the crafting result slot.

## Fix alternate-recipe shift-click bulk crafting
* Intercept shift-left-clicks only on a selected conflicting crafting result and send a dedicated bulk-craft request.
* Validate the open window, result slot, matching-recipe position, and live crafting matrix on the server.
* Reuse vanilla container transfers and `SlotCrafting` pickup handling for every craft while preserving the selected alternate result between iterations.
* Stop safely when the recipe no longer matches, inventory transfer fails, the crafting matrix makes no progress, or the container changes.

## Fix GTNH build migration on the 1710 branch
* Replace the obsolete ForgeGradle 1.2 build with the GTNH convention stack and Gradle 8.11.1 wrapper configuration.
* Build Minecraft 1.7.10 against Forge 10.13.4.1614 and stable MCP mappings version 12 while retaining Java 8 bytecode.
* Move Java sources, language files, and mod metadata into the standard Gradle source and resource directories required by GTNH structure validation.
* Remove the legacy root-directory `sourceSets` workaround while keeping GTNH structure validation enabled.
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
