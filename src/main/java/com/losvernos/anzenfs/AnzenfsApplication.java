package com.losvernos.anzenfs;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.losvernos.anzenfs.user.UserDAO;
import com.losvernos.anzenfs.user.UserDTO;

@ImportRuntimeHints(AnzenfsApplication.WebResourcesHints.class)
@RestController
@SpringBootApplication
public class AnzenfsApplication {

  private UserDAO userDAO = new UserDAO();

  static class WebResourcesHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
      // Force l'inclusion de tout le dossier static dans le binaire natif
      hints.resources().registerPattern("static/browser/**");
    }
  }

  @GetMapping("/users/create")
  public void createUser() {
    var user = new UserDTO();
    user.setUsername("admin");
    user.setPassword("admin");
    userDAO.save(user);
  }

  @GetMapping("/users")
  public String getUsers() {
    return userDAO.getAll().toString();
  }

  @GetMapping("/users/{id}")
  public String getUserByID(@PathVariable long ID) {
    return userDAO.get(ID).toString();
  }

  public static void main(String[] args) {
    SpringApplication.run(AnzenfsApplication.class, args);
  }

}
