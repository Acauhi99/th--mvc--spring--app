package acauhi.mvc.spring.controller;

import acauhi.mvc.spring.entity.User;
import acauhi.mvc.spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping
  public String listUsers(Model model) {
    model.addAttribute("users", userService.findAll());
    return "users/list";
  }

  @GetMapping("/create")
  public String createUserForm(Model model) {
    model.addAttribute("user", new User());
    model.addAttribute("userTypes", User.UserType.values());
    return "users/form";
  }

  @PostMapping("/create")
  public String createUser(@ModelAttribute User user) {
    userService.save(user);
    return "redirect:/users";
  }

  @GetMapping("/edit/{id}")
  public String editUserForm(@PathVariable UUID id, Model model, Authentication authentication) {
    User user = userService.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid user Id: " + id));

    // Verifica se é ADMIN ou se o usuário está editando o próprio perfil
    if (!hasAdminRole(authentication) && !isCurrentUser(authentication, user)) {
      return "redirect:/access-denied";
    }

    model.addAttribute("user", user);
    model.addAttribute("userTypes", User.UserType.values());
    return "users/form";
  }

  @PostMapping("/edit/{id}")
  public String editUser(@PathVariable UUID id, @ModelAttribute User user, Authentication authentication) {
    User existingUser = userService.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid user Id: " + id));

    // Verifica se é ADMIN ou se o usuário está editando o próprio perfil
    if (!hasAdminRole(authentication) && !isCurrentUser(authentication, existingUser)) {
      return "redirect:/access-denied";
    }

    user.setId(id);
    userService.save(user);
    return "redirect:/users";
  }

  @GetMapping("/delete/{id}")
  public String deleteUser(@PathVariable UUID id, Authentication authentication) {
    // Apenas ADMIN pode deletar usuários
    if (!hasAdminRole(authentication)) {
      return "redirect:/access-denied";
    }

    userService.deleteById(id);
    return "redirect:/users";
  }

  @GetMapping("/profile")
  public String viewProfile(Model model, Authentication authentication) {
    User user = userService.findByEmail(authentication.getName())
        .orElseThrow(() -> new IllegalStateException("User not found"));
    model.addAttribute("user", user);
    return "users/profile";
  }

  private boolean hasAdminRole(Authentication authentication) {
    return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
  }

  private boolean isCurrentUser(Authentication authentication, User user) {
    return user.getEmail().equals(authentication.getName());
  }
}
