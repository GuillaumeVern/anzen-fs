package com.losvernos.anzenfs.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
  private static final String APP_NAME = "anzenfs";

  public static File getDataDir() {
    String xdgDataHome = System.getenv("XDG_DATA_HOME");
    if (xdgDataHome == null || xdgDataHome.isEmpty()) {
      xdgDataHome = System.getProperty("user.home") + File.separator + ".local" + File.separator + "share";
    }

    File appDataDir = new File(xdgDataHome, APP_NAME);
    if (!appDataDir.exists()) {
      appDataDir.mkdirs();
    }

    return appDataDir;
  }

  public static void deleteDirectory(Path path) {
    try (var stream = Files.walk(path)) {
      stream.sorted(java.util.Comparator.reverseOrder()) // Delete files, then subfolders, then root
          .map(Path::toFile)
          .forEach(java.io.File::delete);
    } catch (IOException e) {
      // Log it, but don't crash the whole app if a temp file is stuck
      System.err.println("Could not clean up staging directory: " + e.getMessage());
    }
  }
}
