package com.framework.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class VirtualScenePosterUtils {

    private VirtualScenePosterUtils() {
    }

    public static void setVirtualScenePoster(String sourceImagePath) throws IOException {
        Path source = Paths.get(sourceImagePath);
        if (!Files.exists(source)) {
            throw new IllegalStateException("Virtual Scene Poster Failure: source image not found at " + source.toAbsolutePath());
        }

        Path posterPath = resolvePosterPath();
        if (!Files.exists(posterPath.getParent())) {
            throw new IllegalStateException(
                    "Virtual Scene Poster Failure: emulator resources folder not found at " + posterPath.getParent()
                            + " — confirm ANDROID_HOME/ANDROID_SDK_ROOT is set correctly, or that this environment has the "
                            + "Android Emulator package installed.");
        }

        Files.copy(source, posterPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private static Path resolvePosterPath() {
        String sdkRoot = System.getenv("ANDROID_HOME");
        if (sdkRoot == null || sdkRoot.isBlank()) {
            sdkRoot = System.getenv("ANDROID_SDK_ROOT");
        }
        if (sdkRoot == null || sdkRoot.isBlank()) {
            throw new IllegalStateException(
                    "Virtual Scene Poster Failure: neither ANDROID_HOME nor ANDROID_SDK_ROOT is set — "
                            + "can't locate the Android SDK's emulator/resources folder.");
        }
        return Paths.get(sdkRoot, "emulator", "resources", "poster.png");
    }
}