package acauhi.mvc.spring.controller;

import acauhi.mvc.spring.entity.User;
import acauhi.mvc.spring.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @GetMapping("/login")
  public String login() {
    return "pages/login";
  }

  @GetMapping("/access-denied")
  public String accessDenied() {
    return "pages/access-denied";
  }

  @GetMapping("/register")
  public String showRegistrationForm(Model model) {
    if (!model.containsAttribute("user")) {
      User user = new User();
      user.setUserType(User.UserType.CLIENT);
      model.addAttribute("user", user);
    }
    return "pages/register";
  }

  @PostMapping("/register")
  public String registerUser(@Valid @ModelAttribute("user") User user,
      BindingResult result,
      RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
      return "pages/register";
    }

    if (authService.emailExists(user.getEmail())) {
      result.rejectValue("email", "error.user", "An account already exists for this email.");
      return "pages/register";
    }

    user.setUserType(User.UserType.CLIENT);
    authService.registerUser(user);

    redirectAttributes.addFlashAttribute("successMessage", "Registration successful! You can now log in.");
    return "redirect:/login";
  }
}