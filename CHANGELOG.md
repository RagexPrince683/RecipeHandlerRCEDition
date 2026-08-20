## Fix 1710 build system
* Replace the Gradle-4.5-incompatible original ForgeGradle 1.2 plugin with the maintained Anatawa12 ForgeGradle 1.2 fork.
* Compile the legacy `assets/recipehandler` source tree for Java 8 and package its language resources without including Java source files.
* Use HTTPS build repositories and expand valid version strings in `mcmod.info`.

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
