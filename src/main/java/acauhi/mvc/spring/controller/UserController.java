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

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  // Admin dashboard methods
  @GetMapping("/dashboard")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public String dashboard(Model model) {
    List<User> users = userService.findAll();

    Map<User.UserType, Long> userTypeCount = users.stream()
        .collect(Collectors.groupingBy(User::getUserType, Collectors.counting()));

    List<User> recentUsers = users.stream()
        .sorted((u1, u2) -> u2.getId().compareTo(u1.getId()))
        .limit(6)
        .collect(Collectors.toList());

    model.addAttribute("users", users);
    model.addAttribute("recentUsers", recentUsers);
    model.addAttribute("totalUsers", users.size());
    model.addAttribute("userTypeCount", userTypeCount);

    return "pages/users/dashboard";
  }

  // User creation methods
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
    return "redirect:/users/dashboard";
  }

  // User edit methods
  @GetMapping("/edit/{id}")
  @PreAuthorize("@securityActionChecker.isAdminOrSelf(authentication, #id)")
  public String editUserForm(@PathVariable UUID id, Model model) {
    User user = userService.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid user Id: " + id));

    model.addAttribute("user", user);
    model.addAttribute("userTypes", User.UserType.values());
    return "pages/users/edit";
  }

  @PostMapping("/edit/{id}")
  @PreAuthorize("@securityActionChecker.canModifyUser(authentication, #user, #id)")
  public String editUser(
      @PathVariable UUID id,
      @ModelAttribute User user,
      @RequestParam(required = false) String newPassword,
      RedirectAttributes redirectAttributes) {

    try {
      user.setId(id);
      userService.updateUser(user, newPassword);
      redirectAttributes.addFlashAttribute("successMessage", "User updated successfully");
      return "redirect:/users/dashboard";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Error updating user: " + e.getMessage());
      return "redirect:/users/edit/" + id;
    }
  }

  // User deletion method
  @PostMapping("/delete/{id}")
  @PreAuthorize("@securityActionChecker.canDeleteUser(authentication, #id)")
  public String deleteUser(@PathVariable UUID id, RedirectAttributes redirectAttributes,
      Authentication authentication) {
    try {
      User currentUser = userService.findByEmail(authentication.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));

      boolean isSelfDelete = currentUser.getId().equals(id);

      userService.deleteById(id);
      redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");

      if (isSelfDelete) {
        return "redirect:/logout";
      }

      return "redirect:/users/dashboard";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Error deleting user: " + e.getMessage());
      return "redirect:/users/dashboard";
    }
  }

  // User profile methods
  @GetMapping("/profile")
  public String viewProfile(Model model, Authentication authentication) {
    User user = userService.findByEmail(authentication.getName())
        .orElseThrow(() -> new IllegalStateException("User not found"));
    model.addAttribute("user", user);
    return "pages/users/profile";
  }

  @GetMapping("/profile/edit")
  public String editProfile(Model model, Authentication authentication) {
    User user = userService.findByEmail(authentication.getName())
        .orElseThrow(() -> new IllegalStateException("User not found"));
    model.addAttribute("user", user);
    return "pages/users/profile-edit";
  }

  @PostMapping("/profile/edit")
  public String updateProfile(@ModelAttribute User user, Authentication authentication,
      RedirectAttributes redirectAttributes) {
    User currentUser = userService.findByEmail(authentication.getName())
        .orElseThrow(() -> new IllegalStateException("User not found"));

    user.setId(currentUser.getId());
    user.setUserType(currentUser.getUserType());

    userService.save(user);
    redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
    return "redirect:/users/profile";
  }

  @PostMapping("/profile/delete")
  public String deleteProfile(Authentication authentication, RedirectAttributes redirectAttributes) {
    User user = userService.findByEmail(authentication.getName())
        .orElseThrow(() -> new IllegalStateException("User not found"));

    userService.deleteById(user.getId());
    redirectAttributes.addFlashAttribute("successMessage", "Account deleted successfully");
    return "redirect:/logout";
  }
}