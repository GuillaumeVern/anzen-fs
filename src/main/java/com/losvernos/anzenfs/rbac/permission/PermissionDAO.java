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
public class PermissionDAO implements DAO<PermissionDTO> {

  public List<PermissionDTO> getAll() {
    var conn = DBManager.getInstance().getConnection();
    List<PermissionDTO> permissionsList = new ArrayList<PermissionDTO>();

    try {
      var stmt = conn.prepareStatement("""
            SELECT * FROM permissions;
          """);
      var resultSet = stmt.executeQuery();
      permissionsList = mapResultSetToDTO(resultSet);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return permissionsList;
  }

  public Optional<PermissionDTO> get(long ID) {
    var conn = DBManager.getInstance().getConnection();
    Optional<PermissionDTO> result = Optional.empty();

    try {
      var stmt = conn.prepareStatement("""
            SELECT * FROM permissions WHERE permissions.permission_id = ?;
          """);

      stmt.setInt(1, (int) ID);
      var resultSet = stmt.executeQuery();
      var permissionDTOList = mapResultSetToDTO(resultSet);
      try {
        result = Optional.of(permissionDTOList.getFirst());
      } catch (NoSuchElementException e) {
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return result;
  }

  public void save(PermissionDTO elementToSave) {
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

  public void update(PermissionDTO elementToUpdate, String[] params) {
    System.out.println("update permission not implemented");
  }

  public void delete(PermissionDTO elementToDelete) {
    System.out.println("delete permission not implemented");
  }

  private List<PermissionDTO> mapResultSetToDTO(ResultSet resultSet) throws SQLException {
    var userDTOList = new ArrayList<PermissionDTO>();

    while (resultSet.next()) {
      var permissionDTO = new PermissionDTO();
      permissionDTO.setID(resultSet.getLong("permission_id"));
      permissionDTO.setName(resultSet.getString("permission_name"));
      userDTOList.add(permissionDTO);
    }

    return userDTOList;
  }
}
