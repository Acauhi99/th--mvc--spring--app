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

  // Operações de busca gerais
  public List<Event> findAll() {
    return eventRepository.findAll();
  }

  public Optional<Event> findById(UUID id) {
    return eventRepository.findById(id);
  }

  // Operações de busca específicas
  public List<Event> findActiveEvents() {
    return eventRepository.findByActiveTrue();
  }

  public List<Event> findUpcomingEvents() {
    return eventRepository.findByActiveTrueAndStartDateTimeAfter(LocalDateTime.now());
  }

  public List<Event> findEventsByOrganizer(User organizer) {
    return eventRepository.findEventsByOrganizerId(organizer.getId());
  }

  public List<Event> findEventsByOrganizerId(UUID organizerId) {
    return eventRepository.findEventsByOrganizerId(organizerId);
  }

  // Operações de persistência
  public Event save(Event event) {
    return eventRepository.save(event);
  }

  // Soft delete - marca evento como inativo em vez de excluir
  public void deleteById(UUID id) {
    Optional<Event> event = eventRepository.findById(id);
    if (event.isPresent()) {
      Event eventToDelete = event.get();
      eventToDelete.setActive(false);
      eventRepository.save(eventToDelete);
    }
  }

  // Métricas e operações de contagem
  public Long countRegistrationsByEventId(UUID eventId) {
    return eventRepository.countRegistrationsByEventId(eventId);
  }

  public Long countEventsByOrganizerId(UUID organizerId) {
    return eventRepository.countEventsByOrganizerId(organizerId);
  }
}