package acauhi.mvc.spring.controller;

import acauhi.mvc.spring.entity.Registration;
import acauhi.mvc.spring.entity.Event;
import acauhi.mvc.spring.entity.User;
import acauhi.mvc.spring.service.RegistrationService;
import acauhi.mvc.spring.service.EventService;
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
@RequestMapping("/registrations")
@RequiredArgsConstructor
public class RegistrationController {

  private final RegistrationService registrationService;
  private final EventService eventService;
  private final UserService userService;

  @GetMapping
  @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ORGANIZADOR')")
  public String listRegistrations(Model model) {
    model.addAttribute("registrations", registrationService.findAll());
    return "pages/registrations/list";
  }

  @GetMapping("/create")
  @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ORGANIZADOR')")
  public String createRegistrationForm(Model model) {
    model.addAttribute("registration", new Registration());
    model.addAttribute("events", eventService.findAll());
    model.addAttribute("users", userService.findAll());
    model.addAttribute("statuses", Registration.RegistrationStatus.values());
    return "pages/registrations/form";
  }

  @PostMapping("/create")
  @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ORGANIZADOR')")
  public String createRegistration(@ModelAttribute Registration registration, RedirectAttributes redirectAttributes) {
    registrationService.save(registration);
    redirectAttributes.addFlashAttribute("successMessage", "Registration created successfully");
    return "redirect:/registrations";
  }

  @GetMapping("/edit/{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ORGANIZADOR')")
  public String editRegistrationForm(@PathVariable UUID id, Model model) {
    Registration registration = registrationService.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid registration Id: " + id));

    model.addAttribute("registration", registration);
    model.addAttribute("events", eventService.findAll());
    model.addAttribute("users", userService.findAll());
    model.addAttribute("statuses", Registration.RegistrationStatus.values());
    return "pages/registrations/form";
  }

  @PostMapping("/edit/{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ORGANIZADOR')")
  public String editRegistration(@PathVariable UUID id, @ModelAttribute Registration registration, RedirectAttributes redirectAttributes) {
    registration.setId(id);
    registrationService.save(registration);
    redirectAttributes.addFlashAttribute("successMessage", "Registration updated successfully");
    return "redirect:/registrations";
  }

  @GetMapping("/delete/{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ORGANIZADOR')")
  public String deleteRegistration(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
    registrationService.deleteById(id);
    redirectAttributes.addFlashAttribute("successMessage", "Registration deleted successfully");
    return "redirect:/registrations";
  }

  @GetMapping("/my-registrations")
  @PreAuthorize("hasAuthority('ROLE_PARTICIPANTE')")
  public String myRegistrations(Model model, Authentication authentication) {
    User user = userService.findByEmail(authentication.getName())
        .orElseThrow(() -> new IllegalStateException("User not found"));
    model.addAttribute("registrations", registrationService.findByParticipant(user));
    return "pages/registrations/my-registrations";
  }

  @GetMapping("/event/{eventId}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ORGANIZADOR')")
  public String registrationsByEvent(@PathVariable UUID eventId, Model model) {
    Event event = eventService.findById(eventId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid event Id: " + eventId));
    model.addAttribute("event", event);
    model.addAttribute("registrations", registrationService.findByEvent(event));
    return "pages/registrations/by-event";
  }
}