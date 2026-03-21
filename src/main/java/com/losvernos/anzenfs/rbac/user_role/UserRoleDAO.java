package com.losvernos.anzenfs.rbac.user_role;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.losvernos.anzenfs.DAO;
import com.losvernos.anzenfs.rbac.database.DBManager;

@Service
public class UserRoleDAO implements DAO<UserRoleDTO> {

  public List<UserRoleDTO> getAll() {
    var conn = DBManager.getInstance().getConnection();
    List<UserRoleDTO> user_rolesList = new ArrayList<UserRoleDTO>();

    try {
      var stmt = conn.prepareStatement("""
            SELECT * FROM user_roles;
          """);
      var resultSet = stmt.executeQuery();
      user_rolesList = mapResultSetToDTO(resultSet);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return user_rolesList;
  }

  public List<UserRoleDTO> get(long userID) {
    var conn = DBManager.getInstance().getConnection();
    List<UserRoleDTO> result = new ArrayList();

    try {
      var stmt = conn.prepareStatement("""
            SELECT * FROM user_roles WHERE user_roles.user_id = ?;
          """);

      stmt.setInt(1, (int) userID);
      var resultSet = stmt.executeQuery();
      result = mapResultSetToDTO(resultSet);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return result;
  }

  public void save(UserRoleDTO elementToSave) {
    var conn = DBManager.getInstance().getConnection();

    try {
      var stmt = conn.prepareStatement("""
          INSERT INTO user_roles (user_id, role_id)
          VALUES (?, ?);""");

      stmt.setInt(1, (int) elementToSave.getUserID());
      stmt.setInt(2, (int) elementToSave.getRoleID());

      stmt.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void update(UserRoleDTO elementToUpdate, String[] params) {
    System.out.println("update user_role not implemented");
  }

  public void delete(UserRoleDTO elementToDelete) {
    System.out.println("delete user_role not implemented");
  }

  private List<UserRoleDTO> mapResultSetToDTO(ResultSet resultSet) throws SQLException {
    var userRoleDTOList = new ArrayList<UserRoleDTO>();

    while (resultSet.next()) {
      var userRoleDTO = new UserRoleDTO();
      userRoleDTO.setID(resultSet.getLong("user_role_id"));
      userRoleDTO.setUserID(resultSet.getLong("user_id"));
      userRoleDTO.setRoleID(resultSet.getLong("role_id"));
      userRoleDTOList.add(userRoleDTO);
    }

    return userRoleDTOList;
  }
}
