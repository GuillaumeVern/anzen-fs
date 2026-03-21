package com.losvernos.anzenfs.rbac.role_permission;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.losvernos.anzenfs.DAO;
import com.losvernos.anzenfs.rbac.database.DBManager;

@Service
public class RolePermissionDAO implements DAO<RolePermissionDTO> {

  public List<RolePermissionDTO> getAll() {
    var conn = DBManager.getInstance().getConnection();
    List<RolePermissionDTO> user_rolesList = new ArrayList<RolePermissionDTO>();

    try {
      var stmt = conn.prepareStatement("""
            SELECT * FROM role_permissions;
          """);
      var resultSet = stmt.executeQuery();
      user_rolesList = mapResultSetToDTO(resultSet);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return user_rolesList;
  }

  public List<RolePermissionDTO> get(long roleID) {
    var conn = DBManager.getInstance().getConnection();
    List<RolePermissionDTO> result = new ArrayList();

    try {
      var stmt = conn.prepareStatement("""
            SELECT * FROM role_permissions WHERE role_permissions.role_id = ?;
          """);

      stmt.setInt(1, (int) roleID);
      var resultSet = stmt.executeQuery();
      result = mapResultSetToDTO(resultSet);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return result;
  }

  public void save(RolePermissionDTO elementToSave) {
    var conn = DBManager.getInstance().getConnection();

    try {
      var stmt = conn.prepareStatement("""
          INSERT INTO role_permissions (role_id, permission_id)
          VALUES (?, ?);""");

      stmt.setInt(1, (int) elementToSave.getRoleID());
      stmt.setInt(2, (int) elementToSave.getPermissionID());

      stmt.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void update(RolePermissionDTO elementToUpdate, String[] params) {
    System.out.println("update role_permission not implemented");
  }

  public void delete(RolePermissionDTO elementToDelete) {
    System.out.println("delete role_permission not implemented");
  }

  private List<RolePermissionDTO> mapResultSetToDTO(ResultSet resultSet) throws SQLException {
    var rolePermissionDTOList = new ArrayList<RolePermissionDTO>();

    while (resultSet.next()) {
      var rolePermissionDTO = new RolePermissionDTO();
      rolePermissionDTO.setID(resultSet.getLong("user_role_id"));
      rolePermissionDTO.setRoleID(resultSet.getLong("role_id"));
      rolePermissionDTO.setPermissionID(resultSet.getLong("permission_id"));
      rolePermissionDTOList.add(rolePermissionDTO);
    }

    return rolePermissionDTOList;
  }
}
