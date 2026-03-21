package com.losvernos.anzenfs.rbac.user_role;

import com.losvernos.anzenfs.DTO;

public class UserRoleDTO implements DTO {

  private long ID;

  private long roleID;

  private long userID;

  @Override
  public long getID() {
    return this.ID;
  }

  @Override
  public void setID(long ID) {
    this.ID = ID;
  }

  public long getUserID() {
    return this.userID;
  }

  public void setUserID(long userID) {
    this.userID = userID;
  }

  public long getRoleID() {
    return this.roleID;
  }

  public void setRoleID(long roleID) {
    this.roleID = roleID;
  }
}
