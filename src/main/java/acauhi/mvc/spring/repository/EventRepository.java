package acauhi.mvc.spring.repository;

import acauhi.mvc.spring.entity.Event;
import acauhi.mvc.spring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

  // Métodos de busca por status e tempo
  List<Event> findByActiveTrue();

  List<Event> findByActiveTrueAndStartDateTimeAfter(LocalDateTime dateTime);

  // Métodos de busca por organizador
  List<Event> findByOrganizerAndActiveTrue(User organizer);

  @Query("SELECT e FROM Event e " +
      "WHERE e.organizer.id = :organizerId AND e.active = true")
  List<Event> findEventsByOrganizerId(@Param("organizerId") UUID organizerId);

  // Métodos de contagem
  @Query("SELECT COUNT(r) FROM Registration r " +
      "WHERE r.event.id = :eventId AND r.status = 'INSCRITO'")
  Long countRegistrationsByEventId(@Param("eventId") UUID eventId);

  @Query("SELECT COUNT(e) FROM Event e " +
      "WHERE e.organizer.id = :organizerId AND e.active = true")
  Long countEventsByOrganizerId(@Param("organizerId") UUID organizerId);
}