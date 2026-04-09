package com.losvernos.anzenfs.files;

import java.sql.SQLException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.losvernos.anzenfs.database.DBManager;

@Service
public class FileRepository {

  public Optional<Integer> findIdByNameAndParent(String name, Integer parentId) {
    var conn = DBManager.getInstance().getConnection();
    var sql = "SELECT file_id FROM files WHERE name = ? AND parent_id = ?;";

    try (var stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, name);
      stmt.setInt(2, parentId);
      try (var resultSet = stmt.executeQuery()) {
        if (resultSet.next()) {
          return Optional.of(resultSet.getInt("file_id"));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  public void insertFile(Integer parentId, String name, String type, String hash) {
    var conn = DBManager.getInstance().getConnection();
    String sql = "INSERT INTO files (parent_id, name, type, file_hash, external_id) VALUES (?, ?, ?, ?, ?);";

    try (var stmt = conn.prepareStatement(sql)) {
      String externalId = java.util.UUID.randomUUID().toString();

      if (parentId == null)
        stmt.setNull(1, java.sql.Types.INTEGER);
      else
        stmt.setInt(1, parentId);

      stmt.setString(2, name);
      stmt.setString(3, type);
      stmt.setString(4, hash);
      stmt.setString(5, externalId);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public Integer createFolder(Integer parentId, String name) {
    var conn = DBManager.getInstance().getConnection();
    String sql = "INSERT INTO files (parent_id, name, type, external_id) VALUES (?, ?, 'FOLDER', ?)";

    try (var stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
      String externalId = java.util.UUID.randomUUID().toString();

      if (parentId == null) {
        stmt.setNull(1, java.sql.Types.INTEGER);
      } else {
        stmt.setInt(1, parentId);
      }

      stmt.setString(2, name);
      stmt.setString(3, externalId);
      stmt.executeUpdate();

      try (var rs = stmt.getGeneratedKeys()) {
        if (rs.next())
          return rs.getInt(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
}
