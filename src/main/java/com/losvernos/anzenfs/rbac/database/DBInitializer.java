package com.losvernos.anzenfs.rbac.database;

import java.sql.SQLException;

public class DBInitializer {

  public static void initialize() {
    DBInitializer.createSchema();
  }

  private static void createSchema() {
    var conn = DBManager.getInstance().getConnection();

    try {
      var stmt = conn.prepareStatement("""
          CREATE TABLE IF NOT EXISTS roles (
              role_id INTEGER PRIMARY KEY,
              role_name TEXT NOT NULL
          );""");
      stmt.execute();

      stmt = conn.prepareStatement("""
          CREATE TABLE IF NOT EXISTS permissions (
              permission_id INTEGER PRIMARY KEY,
              permission_name TEXT NOT NULL
          );
          """);
      stmt.execute();

      stmt = conn.prepareStatement("""
          CREATE TABLE IF NOT EXISTS users (
              user_id INTEGER PRIMARY KEY,
              username TEXT NOT NULL UNIQUE,
              password TEXT NOT NULL
          );""");
      stmt.execute();

      stmt = conn.prepareStatement("""
          CREATE TABLE IF NOT EXISTS user_roles (
              user_id INTEGER,
              role_id INTEGER,
              FOREIGN KEY(user_id) REFERENCES users(user_id),
              FOREIGN KEY(role_id) REFERENCES roles(role_id)
          );""");
      stmt.execute();

      stmt = conn.prepareStatement("""
          CREATE TABLE IF NOT EXISTS role_permissions (
              role_id INTEGER,
              permission_id INTEGER,
              FOREIGN KEY(role_id) REFERENCES roles(role_id),
              FOREIGN KEY(permission_id) REFERENCES permissions(permission_id)
          );""");
      stmt.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
