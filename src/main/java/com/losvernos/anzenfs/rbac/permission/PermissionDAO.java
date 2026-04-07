package com.losvernos.anzenfs.rbac.permission;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.losvernos.anzenfs.DAO;
import com.losvernos.anzenfs.rbac.database.DBManager;

@Service
public class PermissionDAO implements DAO<Permission> {

  public List<Permission> getAll() {
    var conn = DBManager.getInstance().getConnection();
    List<Permission> permissionsList = new ArrayList<Permission>();

    try {
      var stmt = conn.prepareStatement("""
            SELECT * FROM permissions;
          """);
      var resultSet = stmt.executeQuery();
      permissionsList = mapResultSetToPermissions(resultSet);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return permissionsList;
  }

  public Optional<Permission> get(long ID) {
    var conn = DBManager.getInstance().getConnection();
    Optional<Permission> result = Optional.empty();

    try {
      var stmt = conn.prepareStatement("""
            SELECT * FROM permissions WHERE permissions.permission_id = ?;
          """);

      stmt.setInt(1, (int) ID);
      var resultSet = stmt.executeQuery();
      var permissionList = mapResultSetToPermissions(resultSet);
      try {
        result = Optional.of(permissionList.getFirst());
      } catch (NoSuchElementException e) {
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return result;
  }

  public void save(Permission elementToSave) {
    var conn = DBManager.getInstance().getConnection();

    try {
      var stmt = conn.prepareStatement("""
          INSERT INTO permissions (permission_name)
          VALUES (?);""");

      stmt.setString(1, elementToSave.getName());

      stmt.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void update(Permission elementToUpdate, String[] params) {
    System.out.println("update permission not implemented");
  }

  public void delete(Permission elementToDelete) {
    System.out.println("delete permission not implemented");
  }

  private List<Permission> mapResultSetToPermissions(ResultSet resultSet) throws SQLException {
    var userDTOList = new ArrayList<Permission>();

    while (resultSet.next()) {
      var permission = new Permission();
      permission.setID(resultSet.getLong("permission_id"));
      permission.setName(resultSet.getString("permission_name"));
      userDTOList.add(permission);
    }

    return userDTOList;
  }
}
