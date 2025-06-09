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

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/registrations")
@RequiredArgsConstructor
public class RegistrationController {

  private final RegistrationService registrationService;
  private final EventService eventService;
  private final UserService userService;

  @GetMapping
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public String listRegistrations(Model model) {
    List<Registration> registrations = registrationService.findAll();
    // Mudança aqui: buscar todos os eventos do sistema em vez de apenas os ativos
    List<Event> events = eventService.findAll();
    
    // Calcular estatísticas
    long confirmedCount = registrations.stream()
        .filter(r -> r.getStatus() == Registration.RegistrationStatus.CONFIRMADO)
        .count();
    
    long pendingCount = registrations.stream()
        .filter(r -> r.getStatus() == Registration.RegistrationStatus.INSCRITO)
        .count();
        
    long cancelledCount = registrations.stream()
        .filter(r -> r.getStatus() == Registration.RegistrationStatus.CANCELADO)
        .count();
    
    model.addAttribute("registrations", registrations);
    model.addAttribute("events", events);
    model.addAttribute("confirmedCount", confirmedCount);
    model.addAttribute("pendingCount", pendingCount);
    model.addAttribute("cancelledCount", cancelledCount);
    
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
    
    List<Registration> registrations = registrationService.findByEvent(event);
    
    // Calcular estatísticas
    long confirmedCount = registrations.stream()
        .filter(r -> r.getStatus() == Registration.RegistrationStatus.CONFIRMADO)
        .count();
    
    long pendingCount = registrations.stream()
        .filter(r -> r.getStatus() == Registration.RegistrationStatus.INSCRITO)
        .count();
    
    model.addAttribute("event", event);
    model.addAttribute("registrations", registrations);
    model.addAttribute("confirmedCount", confirmedCount);
    model.addAttribute("pendingCount", pendingCount);
    
    return "pages/registrations/by-event";
  }

  @PostMapping("/confirm/{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ORGANIZADOR')")
  public String confirmRegistration(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
    Registration registration = registrationService.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid registration Id: " + id));
    
    registration.setStatus(Registration.RegistrationStatus.CONFIRMADO);
    registrationService.save(registration);
    
    redirectAttributes.addFlashAttribute("successMessage", "Registration confirmed successfully!");
    
    // Verifica se veio da lista geral ou da página do evento
    String referer = redirectAttributes.getFlashAttributes().get("referer") != null ? 
        (String) redirectAttributes.getFlashAttributes().get("referer") : 
        "/registrations";
    
    if (referer.contains("/registrations/event/")) {
      return "redirect:/registrations/event/" + registration.getEvent().getId();
    }
    return "redirect:/registrations";
  }

  @PostMapping("/cancel/{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ORGANIZADOR')")
  public String cancelRegistration(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
    Registration registration = registrationService.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid registration Id: " + id));
    
    registration.setStatus(Registration.RegistrationStatus.CANCELADO);
    registrationService.save(registration);
    
    redirectAttributes.addFlashAttribute("successMessage", "Registration cancelled successfully!");
    
    // Verifica se veio da lista geral ou da página do evento
    String referer = redirectAttributes.getFlashAttributes().get("referer") != null ? 
        (String) redirectAttributes.getFlashAttributes().get("referer") : 
        "/registrations";
    
    if (referer.contains("/registrations/event/")) {
      return "redirect:/registrations/event/" + registration.getEvent().getId();
    }
    return "redirect:/registrations";
  }

  @PostMapping("/toggle-attendance/{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ORGANIZADOR')")
  public String toggleAttendance(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
    Registration registration = registrationService.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid registration Id: " + id));
    
    registration.setAttended(!registration.getAttended());
    registrationService.save(registration);
    
    String message = registration.getAttended() ? 
        "Participant marked as present!" : 
        "Participant marked as absent!";
    
    redirectAttributes.addFlashAttribute("successMessage", message);
    
    // Verifica se veio da lista geral ou da página do evento
    String referer = redirectAttributes.getFlashAttributes().get("referer") != null ? 
        (String) redirectAttributes.getFlashAttributes().get("referer") : 
        "/registrations";
    
    if (referer.contains("/registrations/event/")) {
      return "redirect:/registrations/event/" + registration.getEvent().getId();
    }
    return "redirect:/registrations";
  }
}