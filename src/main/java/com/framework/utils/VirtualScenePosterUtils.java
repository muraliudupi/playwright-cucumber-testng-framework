package com.framework.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.charset.StandardCharsets;

public final class VirtualScenePosterUtils {

    private static final String SCENE_RESOURCE_NAME = "Toren1BD";
    private static final String POSTER_IMAGE_FILENAME = "qr-scan-poster.png";

    private VirtualScenePosterUtils() {
    }

    public static void setVirtualScenePoster(String sourceImagePath) throws IOException {
        Path source = Paths.get(sourceImagePath);
        if (!Files.exists(source)) {
            throw new IllegalStateException("Virtual Scene Poster Failure: source image not found at " + source.toAbsolutePath());
        }

        Path resourcesDir = resolveEmulatorResourcesDir();
        if (!Files.exists(resourcesDir)) {
            throw new IllegalStateException(
                    "Virtual Scene Poster Failure: emulator resources folder not found at " + resourcesDir
                            + " — confirm ANDROID_HOME/ANDROID_SDK_ROOT is set correctly, or that this environment has the "
                            + "Android Emulator package installed.");
        }

        Path imageTarget = resourcesDir.resolve(POSTER_IMAGE_FILENAME);
        Files.copy(source, imageTarget, StandardCopyOption.REPLACE_EXISTING);

        Path postersFile = resourcesDir.resolve(SCENE_RESOURCE_NAME + ".posters");
        String posterDefinition = String.format(
                "poster custom size 1 1 position 0 0 -1.5 rotation 0 0 0 default %s%n", POSTER_IMAGE_FILENAME);
        Files.writeString(postersFile, posterDefinition, StandardCharsets.UTF_8);
    }

    private static Path resolveEmulatorResourcesDir() {
        String sdkRoot = System.getenv("ANDROID_HOME");
        if (sdkRoot == null || sdkRoot.isBlank()) {
            sdkRoot = System.getenv("ANDROID_SDK_ROOT");
        }
        if (sdkRoot == null || sdkRoot.isBlank()) {
            throw new IllegalStateException(
                    "Virtual Scene Poster Failure: neither ANDROID_HOME nor ANDROID_SDK_ROOT is set — "
                            + "can't locate the Android SDK's emulator/resources folder.");
        }
        return Paths.get(sdkRoot, "emulator", "resources");
    }
}