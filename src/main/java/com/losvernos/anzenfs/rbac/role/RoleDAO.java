package com.losvernos.anzenfs.rbac.role;

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
public class RoleDAO implements DAO<Role> {

  public List<Role> getAll() {
    var conn = DBManager.getInstance().getConnection();
    List<Role> rolesList = new ArrayList<Role>();

    try {
      var stmt = conn.prepareStatement("""
            SELECT * FROM roles;
          """);
      var resultSet = stmt.executeQuery();
      rolesList = mapResultSetToRoles(resultSet);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return rolesList;
  }

  public Optional<Role> get(long ID) {
    var conn = DBManager.getInstance().getConnection();
    Optional<Role> result = Optional.empty();

    try {
      var stmt = conn.prepareStatement("""
            SELECT * FROM roles WHERE roles.role_id = ?;
          """);

      stmt.setInt(1, (int) ID);
      var resultSet = stmt.executeQuery();
      var roleDTOList = mapResultSetToRoles(resultSet);
      try {
        result = Optional.of(roleDTOList.getFirst());
      } catch (NoSuchElementException e) {
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return result;
  }

  public void save(Role elementToSave) {
    var conn = DBManager.getInstance().getConnection();

    try {
      var stmt = conn.prepareStatement("""
          INSERT INTO roles (role_name)
          VALUES (?);""");

      stmt.setString(1, elementToSave.getName());

      stmt.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void update(Role elementToUpdate, String[] params) {
    System.out.println("update role not implemented");
  }

  public void delete(Role elementToDelete) {
    System.out.println("delete role not implemented");
  }

  private List<Role> mapResultSetToRoles(ResultSet resultSet) throws SQLException {
    var userDTOList = new ArrayList<Role>();

    while (resultSet.next()) {
      var roleDTO = new Role();
      roleDTO.setID(resultSet.getLong("role_id"));
      roleDTO.setName(resultSet.getString("role_name"));
      userDTOList.add(roleDTO);
    }

    return userDTOList;
  }
}
