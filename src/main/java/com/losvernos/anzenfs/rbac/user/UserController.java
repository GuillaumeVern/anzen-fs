package com.losvernos.anzenfs.rbac.user;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private UserDAO userDAO;

  public UserController(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  @GetMapping("")
  public List<UserDTO> getUsers() {
    var usersDTO = userDAO.getAll();
    return usersDTO;
  }

  @GetMapping("/{id}")
  public UserDTO getUser(@PathVariable long id) {
    return userDAO.get(id).get();
  }

}
