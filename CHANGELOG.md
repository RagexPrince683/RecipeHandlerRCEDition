# Build system repair

* Replaced the mutable ForgeGradle snapshot build with a single RetroFuturaGradle 1.4.0 setup for Minecraft 1.12.2.
* Pinned Forge 14.23.4.2745 and the snapshot_20180801 mappings in `gradle.properties`.
* Updated the wrapper to Gradle 8.5 and retained Java 8 bytecode output.
* Preserved the legacy Java and language-resource layout through explicit source sets.

== NoMoreRecipeConflict 0.8 ==
* Made switch button internal delay more consistent
* Added shift click behavior (keep shift key down to continue craft)
* Added option to only show switch button when a conflict happens

== NoMoreRecipeConflict 0.7 ==
* Switch key handling improved (support mouse keybind)
* Disabled switch in creative inventory (option to enable)

== NoMoreRecipeConflict 0.5 ==
* Better support in potion effect shift
* Fixed custom crafting table support

== NoMoreRecipeConflict 0.4 ==
* Added config options to offset the cycle button
* Made the switch keybind support Forge conflict handler

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