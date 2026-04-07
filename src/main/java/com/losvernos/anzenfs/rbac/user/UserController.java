package com.losvernos.anzenfs.rbac.user;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.losvernos.anzenfs.rbac.permission.Permission;
import com.losvernos.anzenfs.rbac.role.Role;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private UserDAO userDAO;

  public UserController(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  @PreAuthorize("hasRole('ADMIN') and hasAuthority('ADMIN_READ')")
  @GetMapping("")
  public List<GetUserRequest> getUsers() {
    var users = userDAO.getAll();
    var usersDTO = users.stream().map(user -> new GetUserRequest(user.getUsername(), user.getPassword())).toList();
    return usersDTO;
  }

  @GetMapping("/{id}")
  public User getUser(@PathVariable long id) {
    return userDAO.get(id).get();
  }

  @PostMapping("/create")
  public void createUser(@RequestBody CreateUserRequest createUserRequest) {
    var adminRole = new Role();
    adminRole.setName("ADMIN");
    adminRole.setPermissions(List.of(
        Permission.builder().name("ADMIN_READ").build(),
        Permission.builder().name("ADMIN_WRITE").build()));

    var user = User.builder()
        .username(createUserRequest.username())
        .password(createUserRequest.password())
        .userRoles(List.of(adminRole))
        .build();
    userDAO.save(user);
  }

}
