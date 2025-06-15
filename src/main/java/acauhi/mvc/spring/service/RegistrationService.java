package acauhi.mvc.spring.service;

import acauhi.mvc.spring.entity.Registration;
import acauhi.mvc.spring.entity.Event;
import acauhi.mvc.spring.entity.User;
import acauhi.mvc.spring.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationService {

  private final RegistrationRepository registrationRepository;

  // Operações básicas de busca
  public List<Registration> findAll() {
    return registrationRepository.findAll();
  }

  public Optional<Registration> findById(UUID id) {
    return registrationRepository.findById(id);
  }

  // Operações de busca por relacionamentos
  public List<Registration> findByParticipant(User participant) {
    return registrationRepository.findByParticipant(participant);
  }

  public List<Registration> findByEvent(Event event) {
    return registrationRepository.findByEvent(event);
  }

  public List<Registration> findByEventAndStatus(Event event, Registration.RegistrationStatus status) {
    return registrationRepository.findByEventAndStatus(event, status);
  }

  public Optional<Registration> findByEventAndParticipant(Event event, User participant) {
    return registrationRepository.findByEventAndParticipant(event, participant);
  }

  // Operações de contagem
  public Long countByEventAndStatus(Event event, Registration.RegistrationStatus status) {
    return registrationRepository.countByEventAndStatus(event, status);
  }

  // Operações de persistência
  public Registration save(Registration registration) {
    return registrationRepository.save(registration);
  }

  public void deleteById(UUID id) {
    registrationRepository.deleteById(id);
  }

  // Operações de relatórios e métricas
  public Long countTotalRegistrationsByOrganizerId(UUID organizerId) {
    return registrationRepository.countTotalRegistrationsByOrganizerId(organizerId);
  }

  public List<Object[]> findRegistrationsCountByEventForOrganizer(UUID organizerId) {
    return registrationRepository.findRegistrationsCountByEventForOrganizer(organizerId);
  }

  public List<Object[]> findRegistrationsByMonthForOrganizer(UUID organizerId, LocalDateTime sixMonthsAgo) {
    return registrationRepository.findRegistrationsByMonthForOrganizer(organizerId, sixMonthsAgo);
  }
}