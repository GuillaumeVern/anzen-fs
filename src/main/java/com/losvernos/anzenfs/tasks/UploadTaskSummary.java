package com.losvernos.anzenfs.tasks;

public record UploadTaskSummary(
    String taskId,
    int totalFiles,
    int processedFiles,
    long totalBytes,
    long processedBytes,
    String status) {

  public double getPercentComplete() {
    if (totalBytes == 0)
      return 0;
    return (double) processedBytes / totalBytes * 100;
  }
}
