package acauhi.mvc.spring.repository;

import acauhi.mvc.spring.entity.Registration;
import acauhi.mvc.spring.entity.Event;
import acauhi.mvc.spring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, UUID> {

  List<Registration> findByParticipant(User participant);

  List<Registration> findByEvent(Event event);

  List<Registration> findByEventAndStatus(Event event, Registration.RegistrationStatus status);

  Optional<Registration> findByEventAndParticipant(Event event, User participant);

  Long countByEventAndStatus(Event event, Registration.RegistrationStatus status);

  @Query("SELECT COUNT(r) FROM Registration r JOIN r.event e WHERE e.organizer.id = :organizerId AND r.status = 'INSCRITO'")
  Long countTotalRegistrationsByOrganizerId(@Param("organizerId") UUID organizerId);

  @Query("SELECT e.name, COUNT(r) FROM Registration r JOIN r.event e WHERE e.organizer.id = :organizerId AND r.status = 'INSCRITO' GROUP BY e.id, e.name ORDER BY COUNT(r) DESC")
  List<Object[]> findRegistrationsCountByEventForOrganizer(@Param("organizerId") UUID organizerId);

  @Query("SELECT EXTRACT(MONTH FROM r.registrationDate), COUNT(r) FROM Registration r JOIN r.event e WHERE e.organizer.id = :organizerId AND r.registrationDate >= :sixMonthsAgo GROUP BY EXTRACT(MONTH FROM r.registrationDate) ORDER BY EXTRACT(MONTH FROM r.registrationDate)")
  List<Object[]> findRegistrationsByMonthForOrganizer(@Param("organizerId") UUID organizerId,
      @Param("sixMonthsAgo") LocalDateTime sixMonthsAgo);
}