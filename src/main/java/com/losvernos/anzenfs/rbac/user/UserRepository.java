package com.losvernos.anzenfs.rbac.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.losvernos.anzenfs.database.DBManager;
import com.losvernos.anzenfs.rbac.permission.Permission;
import com.losvernos.anzenfs.rbac.role.Role;

@Service
public class UserRepository {

  @Autowired
  private PasswordEncoder passwordEncoder;

  public List<User> getAll() {
    var conn = DBManager.getInstance().getConnection();
    List<User> usersList = new ArrayList<User>();

    try {
      var stmt = conn.prepareStatement("""
            SELECT * FROM users;
          """);
      var resultSet = stmt.executeQuery();
      usersList = mapResultSetToUsers(resultSet);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return usersList;
  }

  public Optional<User> get(long ID) {
    var conn = DBManager.getInstance().getConnection();
    Optional<User> result = Optional.empty();

    try {
      var stmt = conn.prepareStatement("""
            SELECT * FROM users WHERE users.user_id = ?;
          """);

      stmt.setInt(1, (int) ID);
      var resultSet = stmt.executeQuery();
      var usersList = mapResultSetToUsers(resultSet);
      try {
        result = Optional.of(usersList.getFirst());
      } catch (NoSuchElementException e) {
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return result;
  }

  public Optional<User> findByUsername(String username) {
    var conn = DBManager.getInstance().getConnection();
    Optional<User> result = Optional.empty();

    try {
      var stmt = conn.prepareStatement("""
            SELECT * FROM users WHERE users.username = ?;
          """);

      stmt.setString(1, username);
      var resultSet = stmt.executeQuery();
      var usersList = mapResultSetToUsers(resultSet);
      try {
        result = Optional.of(usersList.getFirst());
      } catch (NoSuchElementException e) {
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return result;
  }

  public Optional<User> findByUsernameWithRolesAndPermissions(String username) {
    var conn = DBManager.getInstance().getConnection();

    try (var stmt = conn.prepareStatement("""
        SELECT
          u.user_id,
          u.username,
          u.password,
          r.role_id,
          r.role_name,
          p.permission_id,
          p.permission_name
        FROM users u
        LEFT JOIN user_roles ur ON ur.user_id = u.user_id
        LEFT JOIN roles r ON r.role_id = ur.role_id
        LEFT JOIN role_permissions rp ON rp.role_id = r.role_id
        LEFT JOIN permissions p ON p.permission_id = rp.permission_id
        WHERE u.username = ?;
        """)) {

      stmt.setString(1, username);

      try (var rs = stmt.executeQuery()) {
        User user = null;

        Map<Long, Role> rolesById = new LinkedHashMap<>();
        Map<Long, Set<Long>> permissionIdsByRole = new HashMap<>();

        while (rs.next()) {
          if (user == null) {
            user = new User();
            user.setID(rs.getLong("user_id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
          }

          long roleId = rs.getLong("role_id");
          if (!rs.wasNull()) {
            Role role = rolesById.get(roleId);
            if (role == null) {
              role = new Role();
              role.setName(rs.getString("role_name"));
              role.setPermissions(new ArrayList<>());
              rolesById.put(roleId, role);
              permissionIdsByRole.put(roleId, new HashSet<>());
            }

            long permissionId = rs.getLong("permission_id");
            if (!rs.wasNull() && permissionIdsByRole.get(roleId).add(permissionId)) {
              Permission permission = new Permission();
              permission.setName(rs.getString("permission_name"));
              role.getPermissions().add(permission);
            }
          }
        }

        if (user == null) {
          return Optional.empty();
        }

        user.setUserRoles(new ArrayList<>(rolesById.values()));
        return Optional.of(user);
      }

    } catch (SQLException e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

  public void save(User elementToSave) {
    var conn = DBManager.getInstance().getConnection();

    try {
      conn.setAutoCommit(false);

      long userId;
      try (var userStmt = conn.prepareStatement("""
          INSERT INTO users (username, password)
          VALUES (?, ?);
          """, Statement.RETURN_GENERATED_KEYS)) {

        userStmt.setString(1, elementToSave.getUsername());
        userStmt.setString(2, passwordEncoder.encode(elementToSave.getPassword()));
        userStmt.executeUpdate();

        try (var keys = userStmt.getGeneratedKeys()) {
          if (!keys.next()) {
            throw new SQLException("Failed to create user: no generated key returned.");
          }
          userId = keys.getLong(1);
        }
      }

      if (elementToSave.getUserRoles() != null) {
        for (Role role : elementToSave.getUserRoles()) {
          long roleId = getOrCreateRoleId(conn, role.getName());
          linkUserRole(conn, userId, roleId);

          if (role.getPermissions() != null) {
            for (Permission permission : role.getPermissions()) {
              long permissionId = getOrCreatePermissionId(conn, permission.getName());
              linkRolePermission(conn, roleId, permissionId);
            }
          }
        }
      }

      conn.commit();
    } catch (SQLException e) {
      try {
        conn.rollback();
      } catch (SQLException rollbackEx) {
        rollbackEx.printStackTrace();
      }
      e.printStackTrace();
    } finally {
      try {
        conn.setAutoCommit(true);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  private long getOrCreateRoleId(java.sql.Connection conn, String roleName) throws SQLException {
    try (var select = conn.prepareStatement("SELECT role_id FROM roles WHERE role_name = ?")) {
      select.setString(1, roleName);
      try (var rs = select.executeQuery()) {
        if (rs.next())
          return rs.getLong("role_id");
      }
    }

    try (var insert = conn.prepareStatement(
        "INSERT INTO roles (role_name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
      insert.setString(1, roleName);
      insert.executeUpdate();
      try (var keys = insert.getGeneratedKeys()) {
        if (keys.next())
          return keys.getLong(1);
      }
    }

    throw new SQLException("Unable to create role: " + roleName);
  }

  private long getOrCreatePermissionId(java.sql.Connection conn, String permissionName) throws SQLException {
    try (var select = conn.prepareStatement("SELECT permission_id FROM permissions WHERE permission_name = ?")) {
      select.setString(1, permissionName);
      try (var rs = select.executeQuery()) {
        if (rs.next())
          return rs.getLong("permission_id");
      }
    }

    try (var insert = conn.prepareStatement(
        "INSERT INTO permissions (permission_name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
      insert.setString(1, permissionName);
      insert.executeUpdate();
      try (var keys = insert.getGeneratedKeys()) {
        if (keys.next())
          return keys.getLong(1);
      }
    }

    throw new SQLException("Unable to create permission: " + permissionName);
  }

  private void linkUserRole(java.sql.Connection conn, long userId, long roleId) throws SQLException {
    try (var link = conn.prepareStatement("""
        INSERT INTO user_roles (user_id, role_id)
        SELECT ?, ?
        WHERE NOT EXISTS (
          SELECT 1 FROM user_roles WHERE user_id = ? AND role_id = ?
        )
        """)) {
      link.setLong(1, userId);
      link.setLong(2, roleId);
      link.setLong(3, userId);
      link.setLong(4, roleId);
      link.executeUpdate();
    }
  }

  private void linkRolePermission(java.sql.Connection conn, long roleId, long permissionId) throws SQLException {
    try (var link = conn.prepareStatement("""
        INSERT INTO role_permissions (role_id, permission_id)
        SELECT ?, ?
        WHERE NOT EXISTS (
          SELECT 1 FROM role_permissions WHERE role_id = ? AND permission_id = ?
        )
        """)) {
      link.setLong(1, roleId);
      link.setLong(2, permissionId);
      link.setLong(3, roleId);
      link.setLong(4, permissionId);
      link.executeUpdate();
    }
  }

  @EventListener(ApplicationReadyEvent.class)
  public void initAdminAccount() {
    User adminInDatabase = this.findByUsername("admin").orElse(new User());
    if (null == adminInDatabase.getUsername()) {
      User adminAccount = User.builder()
          .username("admin")
          .password("admin")
          .userRoles(
              List.of(
                  Role.builder()
                      .name("ADMIN")
                      .permissions(
                          List.of(
                              Permission.builder()
                                  .name("ADMIN_READ")
                                  .build(),
                              Permission.builder()
                                  .name("ADMIN_READ")
                                  .build()))
                      .build()))
          .build();

      save(adminAccount);
    }
  }

  public void update(User elementToUpdate, String[] params) {
    System.out.println("update user not implemented");
  }

  public void delete(User elementToDelete) {
    System.out.println("delete user not implemented");
  }

  private List<User> mapResultSetToUsers(ResultSet resultSet) throws SQLException {
    var userDTOList = new ArrayList<User>();

    while (resultSet.next()) {
      var userDTO = new User();
      userDTO.setID(resultSet.getLong("user_id"));
      userDTO.setUsername(resultSet.getString("username"));
      userDTO.setPassword(resultSet.getString("password"));
      userDTOList.add(userDTO);
    }

    return userDTOList;
  }
}
