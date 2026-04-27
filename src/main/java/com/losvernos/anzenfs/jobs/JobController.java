package com.losvernos.anzenfs.jobs;

import java.sql.Array;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.losvernos.anzenfs.files.FileService;

@RestController
@RequestMapping("/api/files/jobs")
public class JobController {
  @Autowired
  private FileService fileService;

  @Autowired
  private JobService jobService;

  @PostMapping
  public ResponseEntity<?> createJob(@RequestBody ManifestRequest request) {
    String jobId = UUID.randomUUID().toString();

    jobService.createJob(jobId, request.parentUuid(), request.manifest());
    return ResponseEntity.ok(Map.of("jobId", jobId));
  }

  @PostMapping("/api/files/jobs/{jobId}/upload")
  public ResponseEntity<?> uploadFile(
      @PathVariable String jobId,
      @RequestParam("files") MultipartFile[] files,
      @RequestParam("parentUuid") String parentUuid) {

    fileService.processFolderUpload(jobId, parentUuid, files);
    return ResponseEntity.accepted().build();
  }

}
