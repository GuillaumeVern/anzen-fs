package com.losvernos.anzenfs.files;

public record FileNode(
    String uuid,
    String parentUuid,
    String name,
    String type,
    String hash,
    Long size) {
}
