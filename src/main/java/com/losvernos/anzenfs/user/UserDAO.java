package com.losvernos.anzenfs.user;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.losvernos.anzenfs.DAO;
import com.losvernos.anzenfs.database.DBManager;

public class UserDAO implements DAO<UserDTO> {

  public List<UserDTO> getAll() {
    var conn = DBManager.getInstance().getConnection();
    List<UserDTO> usersList = new ArrayList<UserDTO>();

    try {
      var stmt = conn.prepareStatement("""
            SELECT * FROM users;
          """);
      var resultSet = stmt.executeQuery();
      usersList = mapResultSetToDTO(resultSet);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return usersList;
  }

  public Optional<UserDTO> get(long ID) {
    var conn = DBManager.getInstance().getConnection();
    Optional<UserDTO> result = Optional.empty();

    try {
      var stmt = conn.prepareStatement("""
            SELECT * FROM users WHERE users.user_id = ?;
          """);

      stmt.setInt(1, (int) ID);
      var resultSet = stmt.executeQuery();
      var userDTOList = mapResultSetToDTO(resultSet);
      try {
        result = Optional.of(userDTOList.getFirst());
      } catch (NoSuchElementException e) {
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return result;
  }

  public void save(UserDTO elementToSave) {
    var conn = DBManager.getInstance().getConnection();

    try {
      var stmt = conn.prepareStatement("""
          INSERT INTO users (username, password)
          VALUES (?, ?);""");

      stmt.setString(1, elementToSave.getUsername());
      stmt.setString(2, elementToSave.getPassword());

      stmt.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void update(UserDTO elementToUpdate, String[] params) {
    System.out.println("update user not implemented");
  }

  public void delete(UserDTO elementToDelete) {
    System.out.println("delete user not implemented");
  }

  private List<UserDTO> mapResultSetToDTO(ResultSet resultSet) throws SQLException {
    var userDTOList = new ArrayList<UserDTO>();

    while (resultSet.next()) {
      var userDTO = new UserDTO();
      userDTO.setID(resultSet.getLong("user_id"));
      userDTO.setUsername(resultSet.getString("username"));
      userDTO.setPassword(resultSet.getString("password"));
      userDTOList.add(userDTO);
    }

    return userDTOList;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    String newLine = System.getProperty("line.separator");

    result.append(this.getClass().getName());
    result.append(" Object {");
    result.append(newLine);

    // determine fields declared in this class only (no fields of superclass)
    Field[] fields = this.getClass().getDeclaredFields();

    // print field names paired with their values
    for (Field field : fields) {
      result.append("  ");
      field.setAccessible(true);
      try {
        result.append(field.getName());
        result.append(": ");
        // requires access to private field:
        result.append(field.get(this));
      } catch (IllegalAccessException ex) {
        System.out.println(ex);
      }
      result.append(newLine + newLine);
    }
    result.append("}");

    return result.toString();
  }
}
