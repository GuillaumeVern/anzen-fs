package com.losvernos.anzenfs.rbac.role_permission;

import com.losvernos.anzenfs.DTO;

public class RolePermissionDTO implements DTO {

  private long ID;

  private long roleID;

  private long permissionID;

  @Override
  public long getID() {
    return this.ID;
  }

  @Override
  public void setID(long ID) {
    this.ID = ID;
  }

  public long getPermissionID() {
    return this.permissionID;
  }

  public void setPermissionID(long permissionID) {
    this.permissionID = permissionID;
  }

  public long getRoleID() {
    return this.roleID;
  }

  public void setRoleID(long roleID) {
    this.roleID = roleID;
  }
}
