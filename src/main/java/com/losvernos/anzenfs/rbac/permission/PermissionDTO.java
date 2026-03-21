package com.losvernos.anzenfs.rbac.permission;

import com.losvernos.anzenfs.DTO;

public class PermissionDTO implements DTO {

  private long ID;

  private String name;

  @Override
  public long getID() {
    return this.ID;
  }

  @Override
  public void setID(long ID) {
    this.ID = ID;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
