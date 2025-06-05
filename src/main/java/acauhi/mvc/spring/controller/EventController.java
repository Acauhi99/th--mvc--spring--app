package acauhi.mvc.spring.controller;

import acauhi.mvc.spring.entity.Event;
import acauhi.mvc.spring.entity.User;
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
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

  private final EventService eventService;
  private final UserService userService;

  @GetMapping
  public String listEvents(Model model) {
    model.addAttribute("events", eventService.findActiveEvents());
    return "pages/events/list";
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
  public String createEvent(@ModelAttribute Event event, Authentication authentication, RedirectAttributes redirectAttributes) {
    User organizer = userService.findByEmail(authentication.getName())
        .orElseThrow(() -> new IllegalStateException("Organizer not found"));
    
    event.setOrganizer(organizer);
    eventService.save(event);
    redirectAttributes.addFlashAttribute("successMessage", "Event created successfully");
    return "redirect:/events";
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
  public String editEvent(@PathVariable UUID id, @ModelAttribute Event event, Authentication authentication, RedirectAttributes redirectAttributes) {
    Event existingEvent = eventService.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid event Id: " + id));
    
    event.setId(id);
    event.setOrganizer(existingEvent.getOrganizer());
    eventService.save(event);
    redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully");
    return "redirect:/events";
  }

  @GetMapping("/delete/{id}")
  @PreAuthorize("@securityActionChecker.canModifyEvent(authentication, #id)")
  public String deleteEvent(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
    eventService.deleteById(id);
    redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully");
    return "redirect:/events";
  }

  @GetMapping("/my-events")
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ORGANIZADOR')")
  public String myEvents(Model model, Authentication authentication) {
    User organizer = userService.findByEmail(authentication.getName())
        .orElseThrow(() -> new IllegalStateException("User not found"));
    
    model.addAttribute("events", eventService.findEventsByOrganizer(organizer));
    return "pages/events/my-events";
  }

  @GetMapping("/view/{id}")
  public String viewEvent(@PathVariable UUID id, Model model) {
    Event event = eventService.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid event Id: " + id));
    
    Long registrationsCount = eventService.countRegistrationsByEventId(id);
    
    model.addAttribute("event", event);
    model.addAttribute("registrationsCount", registrationsCount);
    model.addAttribute("availableVacancies", event.getTotalVacancies() - registrationsCount.intValue());
    return "pages/events/view";
  }
}