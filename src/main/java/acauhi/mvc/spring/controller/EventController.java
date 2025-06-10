package acauhi.mvc.spring.controller;

import acauhi.mvc.spring.entity.Event;
import acauhi.mvc.spring.entity.User;
import acauhi.mvc.spring.entity.Registration;
import acauhi.mvc.spring.service.EventService;
import acauhi.mvc.spring.service.UserService;
import acauhi.mvc.spring.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Optional;

@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

  private final EventService eventService;
  private final UserService userService;
  private final RegistrationService registrationService;

  @GetMapping
  public String listEvents(Model model, Authentication authentication) {
    List<Event> events = eventService.findActiveEvents();
    
    Map<UUID, Long> registrationCounts = events.stream()
        .collect(Collectors.toMap(
            Event::getId,
            event -> registrationService.countByEventAndStatus(event, Registration.RegistrationStatus.INSCRITO)
        ));

    if (authentication != null && authentication.getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ROLE_PARTICIPANTE"))) {
      User currentUser = userService.findByEmail(authentication.getName()).orElse(null);
      if (currentUser != null) {
        Map<UUID, Boolean> userRegistrations = events.stream()
            .collect(Collectors.toMap(
                Event::getId,
                event -> registrationService.findByEventAndParticipant(event, currentUser).isPresent()
            ));
        model.addAttribute("userRegistrations", userRegistrations);
      }
    }
    
    model.addAttribute("events", events);
    model.addAttribute("registrationCounts", registrationCounts);
    return "pages/events/list";
  }

  @PostMapping("/register/{eventId}")
  @PreAuthorize("hasAuthority('ROLE_PARTICIPANTE')")
  public String registerForEvent(@PathVariable UUID eventId, Authentication authentication, 
                                RedirectAttributes redirectAttributes) {
    try {
      Event event = eventService.findById(eventId)
          .orElseThrow(() -> new IllegalArgumentException("Event not found"));
      
      User participant = userService.findByEmail(authentication.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));

      Optional<Registration> existingRegistration = registrationService.findByEventAndParticipant(event, participant);
      if (existingRegistration.isPresent()) {
        redirectAttributes.addFlashAttribute("errorMessage", "You are already registered for this event!");
        return "redirect:/events";
      }
      
      if (event.getStartDateTime().isBefore(LocalDateTime.now())) {
        redirectAttributes.addFlashAttribute("errorMessage", "Cannot register for past events!");
        return "redirect:/events";
      }
      
      long currentRegistrations = registrationService.countByEventAndStatus(event, Registration.RegistrationStatus.INSCRITO);
      if (currentRegistrations >= event.getTotalVacancies()) {
        redirectAttributes.addFlashAttribute("errorMessage", "Event is full! No vacancies available.");
        return "redirect:/events";
      }
      
      Registration registration = Registration.builder()
          .event(event)
          .participant(participant)
          .status(Registration.RegistrationStatus.INSCRITO)
          .registrationDate(LocalDateTime.now())
          .attended(false)
          .build();
      
      registrationService.save(registration);
      
      redirectAttributes.addFlashAttribute("successMessage", 
          "Successfully registered for \"" + event.getName() + "\"!");
      
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", 
          "Error registering for event: " + e.getMessage());
    }
    
    return "redirect:/events";
  }

  @GetMapping("/create")
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ORGANIZADOR')")
  public String createEventForm(Model model) {
    model.addAttribute("event", new Event());
    model.addAttribute("eventTypes", Event.EventType.values());
    return "pages/events/form";
  }

  @PostMapping("/create")
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ORGANIZADOR')")
  public String createEvent(@ModelAttribute Event event, Authentication authentication,
      RedirectAttributes redirectAttributes) {
    try {
      User organizer = userService.findByEmail(authentication.getName())
          .orElseThrow(() -> new IllegalStateException("User not found"));

      event.setOrganizer(organizer);
      event.setActive(true);

      if (event.getStartDateTime().isBefore(LocalDateTime.now())) {
        redirectAttributes.addFlashAttribute("errorMessage", "Start date cannot be in the past");
        return "redirect:/events/create";
      }

      if (event.getEndDateTime().isBefore(event.getStartDateTime())) {
        redirectAttributes.addFlashAttribute("errorMessage", "End date must be after start date");
        return "redirect:/events/create";
      }

      eventService.save(event);
      redirectAttributes.addFlashAttribute("successMessage", "Event created successfully");
      return "redirect:/events";

    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Error creating event: " + e.getMessage());
      return "redirect:/events/create";
    }
  }

  @GetMapping("/edit/{id}")
  @PreAuthorize("@securityActionChecker.canModifyEvent(authentication, #id)")
  public String editEventForm(@PathVariable UUID id, Model model) {
    Event event = eventService.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid event Id: " + id));
    model.addAttribute("event", event);
    model.addAttribute("eventTypes", Event.EventType.values());
    return "pages/events/form";
  }

  @PostMapping("/edit/{id}")
  @PreAuthorize("@securityActionChecker.canModifyEvent(authentication, #id)")
  public String editEvent(@PathVariable UUID id, @ModelAttribute Event event, Authentication authentication,
      RedirectAttributes redirectAttributes) {
    try {
      Event existingEvent = eventService.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid event Id: " + id));

      event.setId(id);
      event.setOrganizer(existingEvent.getOrganizer());
      event.setActive(existingEvent.getActive());

      if (event.getEndDateTime().isBefore(event.getStartDateTime())) {
        redirectAttributes.addFlashAttribute("errorMessage", "End date must be after start date");
        return "redirect:/events/edit/" + id;
      }

      eventService.save(event);
      redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully");
      return "redirect:/events";

    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Error updating event: " + e.getMessage());
      return "redirect:/events/edit/" + id;
    }
  }

  @GetMapping("/view/{id}")
  public String viewEvent(@PathVariable UUID id, Model model, Authentication authentication) {
    Event event = eventService.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid event Id: " + id));
    
    Long registrationCount = registrationService.countByEventAndStatus(event, Registration.RegistrationStatus.INSCRITO);
    
    // Verificar se o usuário atual já está registrado (para participantes)
    boolean isRegistered = false;
    if (authentication != null && authentication.getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ROLE_PARTICIPANTE"))) {
      User currentUser = userService.findByEmail(authentication.getName()).orElse(null);
      if (currentUser != null) {
        isRegistered = registrationService.findByEventAndParticipant(event, currentUser).isPresent();
      }
    }
    
    model.addAttribute("event", event);
    model.addAttribute("registrationCount", registrationCount);
    model.addAttribute("isRegistered", isRegistered);
    return "pages/events/view";
  }

  @PostMapping("/delete/{id}")
  @PreAuthorize("@securityActionChecker.canModifyEvent(authentication, #id)")
  public String deleteEvent(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
    try {
      eventService.deleteById(id);
      redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Error deleting event: " + e.getMessage());
    }
    return "redirect:/events";
  }

  @GetMapping("/my-events")
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ORGANIZADOR')")
  public String myEvents(Model model, Authentication authentication) {
    User organizer = userService.findByEmail(authentication.getName())
        .orElseThrow(() -> new IllegalStateException("User not found"));

    List<Event> events;
    if (organizer.getUserType() == User.UserType.ADMIN) {
      events = eventService.findActiveEvents();
    } else {
      events = eventService.findEventsByOrganizer(organizer);
    }

    Map<UUID, Long> registrationCounts = events.stream()
        .collect(Collectors.toMap(
            Event::getId,
            event -> registrationService.countByEventAndStatus(event, Registration.RegistrationStatus.INSCRITO)
        ));

    model.addAttribute("events", events);
    model.addAttribute("registrationCounts", registrationCounts);
    return "pages/events/my-events";
  }
}