package acauhi.mvc.spring.service;

import acauhi.mvc.spring.entity.Event;
import acauhi.mvc.spring.entity.User;
import acauhi.mvc.spring.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

  private final EventRepository eventRepository;

  public List<Event> findAll() {
    return eventRepository.findAll();
  }

  public List<Event> findActiveEvents() {
    return eventRepository.findByActiveTrue();
  }

  public List<Event> findEventsByOrganizer(User organizer) {
    return eventRepository.findEventsByOrganizerId(organizer.getId());
  }

  public List<Event> findEventsByOrganizerId(UUID organizerId) {
    return eventRepository.findEventsByOrganizerId(organizerId);
  }

  public List<Event> findUpcomingEvents() {
    return eventRepository.findByActiveTrueAndStartDateTimeAfter(LocalDateTime.now());
  }

  public Optional<Event> findById(UUID id) {
    return eventRepository.findById(id);
  }

  public Event save(Event event) {
    return eventRepository.save(event);
  }

  public void deleteById(UUID id) {
    Optional<Event> event = eventRepository.findById(id);
    if (event.isPresent()) {
      Event eventToDelete = event.get();
      eventToDelete.setActive(false);
      eventRepository.save(eventToDelete);
    }
  }

  public Long countRegistrationsByEventId(UUID eventId) {
    return eventRepository.countRegistrationsByEventId(eventId);
  }

  public Long countEventsByOrganizerId(UUID organizerId) {
    return eventRepository.countEventsByOrganizerId(organizerId);
  }
}