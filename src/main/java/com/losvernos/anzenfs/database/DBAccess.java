package com.losvernos.anzenfs.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBAccess {

    private static final String DB_NAME = "ansen.db";
    private static final String APP_NAME = "ansenfs";

    public static Connection getConnection() {
        String xdgDataHome = System.getenv("XDG_DATA_HOME");
        if (xdgDataHome == null || xdgDataHome.isEmpty()) {
            xdgDataHome = System.getProperty("user.home") + File.separator + ".local" + File.separator + "share";
        }

        File appDataDir = new File(xdgDataHome, APP_NAME);
        File dbFile = new File(appDataDir, DB_NAME);

        try {
            var conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }
}