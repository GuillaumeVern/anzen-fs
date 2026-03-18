package com.losvernos.anzenfs.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.io.File;

public class DBInitializer {
   public static void initialize() {
        String xdgDataHome = System.getenv("XDG_DATA_HOME");
        if (xdgDataHome == null || xdgDataHome.isEmpty()) {
            xdgDataHome = System.getProperty("user.home") + File.separator + ".local" + File.separator + "share";
        }
        
        File appDir = new File(xdgDataHome, "anzenfs");
        if (!appDir.exists()) appDir.mkdirs();
        
        File dbFile = new File(appDir, "ansen.db");
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                         "id INTEGER PRIMARY KEY, " +
                         "username TEXT NOT NULL, " +
                         "role TEXT NOT NULL)");
                         
            stmt.execute("INSERT INTO users (username, role) VALUES " +
                         "('admin', 'admin'), " +
                         "('user1', 'user'), " +
                         "('user2', 'user')");

            
            System.out.println("Database initialized at: " + dbFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
