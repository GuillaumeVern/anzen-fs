package com.losvernos.anzenfs.files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
  @Autowired
  private FileRepository fileRepository;
  private final Path storageRoot = new File(FileUtils.getDataDir(), "data").toPath();

  public void processFolderUpload(String taskId, Integer rootParentId, Path stagingDir, MultipartFile[] files) {
    for (int i = 0; i < files.length; i++) {
      try {
        Path file = stagingDir.resolve("file_" + i);
        String relativePath = files[i].getOriginalFilename();
        if (null == relativePath)
          continue;

        Path incomingPath = Path.of(relativePath);
        if (incomingPath.isAbsolute()) {
          incomingPath = incomingPath.getRoot().relativize(incomingPath);
        }

        Path targetLocation = storageRoot.resolve(incomingPath).normalize();

        if (!targetLocation.startsWith(storageRoot)) {
          throw new SecurityException("Escape attempt detected: " + relativePath);
        }

        Integer folderId = resolveFolderHierarchy(rootParentId, incomingPath);

        String fileName = Path.of(relativePath).getFileName().toString();
        saveToDisk(file, targetLocation);

        String hash = generateHeuristicHash(incomingPath);
        fileRepository.insertFile(folderId, fileName, "FILE", hash);

        // TODO: implement progress streaming with UploadTaskSummary
      } catch (Exception e) {
        System.err.println(e);
      }
    }
  }

  private Integer resolveFolderHierarchy(Integer rootId, Path path) {
    path = path.getParent();
    if (null == path)
      return rootId;

    Integer currentParentId = rootId;

    for (Path part : path) {
      String folderName = part.toString();

      Optional<Integer> existing = fileRepository.findIdByNameAndParent(folderName, currentParentId);

      if (existing.isPresent()) {
        currentParentId = existing.get();
      } else {
        currentParentId = fileRepository.createFolder(currentParentId, folderName);
      }
    }

    return currentParentId;
  }

  private void saveToDisk(Path file, Path targetLocation) throws IOException {
    Files.createDirectories(targetLocation.getParent());
    Files.move(file, targetLocation);
  }

  private String generateHeuristicHash(Path file) throws IOException {
    try {
      String name = file.toString();
      Path fullPath = storageRoot.resolve(file);
      long size = Files.size(fullPath);
      long timestamp = Files.getLastModifiedTime(fullPath).toMillis();

      String rawInput = String.format("%s:%d:%d", name, size, timestamp);

      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] encodedHash = digest.digest(rawInput.getBytes(StandardCharsets.UTF_8));

      return HexFormat.of().formatHex(encodedHash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 algorithm not found", e);
    }
  }
}
