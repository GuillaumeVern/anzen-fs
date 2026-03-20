package com.losvernos.anzenfs.user;

import java.lang.String;

import com.losvernos.anzenfs.DTO;

public class UserDTO implements DTO {

  private long ID;

  private String username;

  private String password;

  @Override
  public long getID() {
    return this.ID;
  }

  @Override
  public void setID(long ID) {
    this.ID = ID;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
