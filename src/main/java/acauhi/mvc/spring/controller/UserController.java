package acauhi.mvc.spring.controller;

import acauhi.mvc.spring.entity.User;
import acauhi.mvc.spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public String listUsers(Model model) {
    model.addAttribute("users", userService.findAll());
    return "pages/users/list";
  }

  @GetMapping("/create")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public String createUserForm(Model model) {
    model.addAttribute("user", new User());
    model.addAttribute("userTypes", User.UserType.values());
    return "pages/users/form";
  }

  @PostMapping("/create")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public String createUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
    userService.save(user);
    redirectAttributes.addFlashAttribute("successMessage", "User created successfully");
    return "redirect:/users";
  }

  @GetMapping("/edit/{id}")
  @PreAuthorize("@securityActionChecker.isAdminOrSelf(authentication, #id)")
  public String editUserForm(@PathVariable UUID id, Model model) {
    User user = userService.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid user Id: " + id));

    model.addAttribute("user", user);
    model.addAttribute("userTypes", User.UserType.values());
    return "pages/users/form";
  }

  @PostMapping("/edit/{id}")
  @PreAuthorize("@securityActionChecker.canModifyUser(authentication, #user, #id)")
  public String editUser(@PathVariable UUID id, @ModelAttribute User user, RedirectAttributes redirectAttributes) {
    user.setId(id);
    userService.save(user);
    redirectAttributes.addFlashAttribute("successMessage", "User updated successfully");
    return "redirect:/users";
  }

  @GetMapping("/delete/{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public String deleteUser(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
    userService.deleteById(id);
    redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
    return "redirect:/users";
  }

  @GetMapping("/profile")
  public String viewProfile(Model model, Authentication authentication) {
    User user = userService.findByEmail(authentication.getName())
        .orElseThrow(() -> new IllegalStateException("User not found"));
    model.addAttribute("user", user);
    return "pages/users/profile";
  }
}