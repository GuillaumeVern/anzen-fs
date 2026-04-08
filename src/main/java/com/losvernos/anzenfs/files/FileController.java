package com.losvernos.anzenfs.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.losvernos.anzenfs.tasks.UploadTaskSummary;

@RestController
@RequestMapping("/api/files")
public class FileController {

  @Autowired
  private FileService fileService;

  private final Path stagingDir = new File(FileUtils.getDataDir(), "staging").toPath();

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UploadTaskSummary> upload(
      @RequestParam(required = false) Integer parentId,
      @RequestPart("files") MultipartFile[] files) throws IOException {

    String taskId = java.util.UUID.randomUUID().toString();
    long totalBytes = java.util.Arrays.stream(files)
        .mapToLong(MultipartFile::getSize)
        .sum();

    Files.createDirectories(stagingDir);

    // 2. Synchronously "Rescue" the files from Tomcat's /tmp to our Staging dir
    // This is fast because on Linux, transferTo() often uses a 'reflink' or 'move'
    for (int i = 0; i < files.length; i++) {
      Path dest = stagingDir.resolve("file_" + i);
      files[i].transferTo(dest);
    }

    Thread.ofVirtual().start(() -> {
      fileService.processFolderUpload(taskId, parentId, stagingDir, files);
      FileUtils.deleteDirectory(stagingDir);
    });

    UploadTaskSummary summary = new UploadTaskSummary(
        taskId,
        files.length,
        0,
        totalBytes,
        0L,
        "PROCESSING");

    return ResponseEntity.status(HttpStatus.ACCEPTED).body(summary);
  }
}
