package com.losvernos.anzenfs.files;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.losvernos.anzenfs.database.DBManager;

@Service
public class FileRepository {

  public Optional<Integer> findIdByNameAndParent(String name, Integer parentId) {
    var sql = "SELECT file_id FROM files WHERE name = ? AND " +
        (parentId == null ? "parent_id IS NULL" : "parent_id = ?");
    try (var stmt = DBManager.getInstance().getConnection().prepareStatement(sql)) {
      stmt.setString(1, name);
      if (parentId != null)
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

  public Optional<Integer> findIdByUuid(String uuid) {
    if (uuid == null)
      return Optional.empty();
    var sql = "SELECT file_id FROM files WHERE external_id = ?;";
    try (var stmt = DBManager.getInstance().getConnection().prepareStatement(sql)) {
      stmt.setString(1, uuid);
      try (var rs = stmt.executeQuery()) {
        if (rs.next())
          return Optional.of(rs.getInt("file_id"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  public List<FileNode> getChildrenAfter(Integer parentId, String lastFileName, String parentUuid, int limit) {
    var result = new ArrayList<FileNode>();

    var sql = """
        SELECT * FROM files
        WHERE parent_id IS ?
        AND (? IS NULL OR name > ?)
        ORDER BY name ASC
        LIMIT ?;
        """;
    try (var stmt = DBManager.getInstance().getConnection().prepareStatement(sql)) {
      stmt.setInt(1, parentId);
      stmt.setString(2, lastFileName);
      stmt.setString(3, lastFileName);
      stmt.setInt(4, limit);
      try (var rs = stmt.executeQuery()) {
        while (rs.next()) {
          result.add(new FileNode(
              rs.getString("external_id"),
              parentUuid,
              rs.getString("name"),
              rs.getString("type"),
              rs.getString("file_hash"),
              0L));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return result;
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
