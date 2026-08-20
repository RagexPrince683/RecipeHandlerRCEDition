# Building NoMoreRecipeConflict

The build uses the repository Gradle wrapper and RetroFuturaGradle. Gradle runs
on JDK 17, while Java compilation uses `--release 8` so the mod remains compatible
with the Java 8 runtime used by Minecraft 1.12.2.

Set `JAVA_HOME` to a JDK 17 installation, then run:

```text
gradlew.bat build
```

On Linux or macOS, use `./gradlew build`. Do not invoke a separately installed
system Gradle; the wrapper selects the Gradle version required by this project.
