package acauhi.mvc.spring.controller;

import acauhi.mvc.spring.entity.User;
import acauhi.mvc.spring.service.UserService;
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

  private final UserService userService;

  @GetMapping("/register")
  public String showRegistrationForm(Model model) {
    if (!model.containsAttribute("user")) {
      User user = new User();
      user.setUserType(User.UserType.CLIENT);
      model.addAttribute("user", user);
    }
    return "register";
  }

  @PostMapping("/register")
  public String registerUser(@Valid @ModelAttribute("user") User user,
      BindingResult result,
      RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
      return "register";
    }

    if (userService.findByEmail(user.getEmail()).isPresent()) {
      result.rejectValue("email", "error.user", "An account already exists for this email.");
      return "register";
    }

    user.setUserType(User.UserType.CLIENT);
    userService.save(user);

    redirectAttributes.addFlashAttribute("successMessage", "Registration successful! You can now log in.");
    return "redirect:/login";
  }
}