package com.losvernos.anzenfs.rbac.user;

import java.lang.String;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.losvernos.anzenfs.rbac.permission.Permission;
import com.losvernos.anzenfs.rbac.role.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails {

  private long ID;

  private String username;

  private String password;

  private List<Role> userRoles;

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Set<GrantedAuthority> authorities = new HashSet<>();

    if (userRoles == null)
      return authorities;

    for (Role role : userRoles) {
      authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

      if (role.getPermissions() != null) {
        for (Permission permission : role.getPermissions()) {
          authorities.add(new SimpleGrantedAuthority(permission.getName()));
        }
      }
    }

    return authorities;
  }

}
