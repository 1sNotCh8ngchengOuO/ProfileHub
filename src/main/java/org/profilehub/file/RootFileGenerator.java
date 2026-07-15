package org.profilehub.file;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RootFileGenerator {
    private static final Path rootFilePath = Paths.get("C:\\Users\\Admin\\ProfileHub");

    public static boolean isRootFileExist() {
        return Files.exists(rootFilePath);
    }
}
