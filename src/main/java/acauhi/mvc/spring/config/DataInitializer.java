package acauhi.mvc.spring.config;

import acauhi.mvc.spring.entity.Event;
import acauhi.mvc.spring.entity.Registration;
import acauhi.mvc.spring.entity.User;
import acauhi.mvc.spring.service.UserService;
import acauhi.mvc.spring.repository.EventRepository;
import acauhi.mvc.spring.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final UserService userService;
  private final EventRepository eventRepository;
  private final RegistrationRepository registrationRepository;

  @Override
  public void run(String... args) throws Exception {
    if (userService.findAll().isEmpty()) {
      
      User admin = User.builder()
          .name("Admin User")
          .email("admin@example.com")
          .password("password")
          .userType(User.UserType.ADMIN)
          .build();
      admin = userService.save(admin);

      User organizador1 = User.builder()
          .name("João Silva")
          .email("joao.organizador@example.com")
          .password("password")
          .userType(User.UserType.ORGANIZADOR)
          .build();
      organizador1 = userService.save(organizador1);

      User organizador2 = User.builder()
          .name("Maria Santos")
          .email("maria.organizadora@example.com")
          .password("password")
          .userType(User.UserType.ORGANIZADOR)
          .build();
      organizador2 = userService.save(organizador2);

      User participante1 = User.builder()
          .name("Carlos Participante")
          .email("carlos@example.com")
          .password("password")
          .userType(User.UserType.PARTICIPANTE)
          .build();
      participante1 = userService.save(participante1);

      User participante2 = User.builder()
          .name("Ana Participante")
          .email("ana@example.com")
          .password("password")
          .userType(User.UserType.PARTICIPANTE)
          .build();
      participante2 = userService.save(participante2);

      Event evento1 = Event.builder()
          .name("Introdução ao Spring Boot")
          .description("Workshop prático sobre desenvolvimento com Spring Boot")
          .type(Event.EventType.WORKSHOP)
          .location("Sala 101 - Centro de Tecnologia")
          .startDateTime(LocalDateTime.now().plusDays(10).withHour(14).withMinute(0))
          .endDateTime(LocalDateTime.now().plusDays(10).withHour(18).withMinute(0))
          .totalVacancies(30)
          .speaker("Dr. Roberto Tech")
          .organizer(organizador1)
          .active(true)
          .build();
      evento1 = eventRepository.save(evento1);

      Event eventoPassado = Event.builder()
          .name("Seminário de Metodologias Ágeis")
          .description("Seminário sobre Scrum, Kanban e outras metodologias")
          .type(Event.EventType.SEMINARIO)
          .location("Sala de Conferências")
          .startDateTime(LocalDateTime.now().minusDays(5).withHour(14).withMinute(0))
          .endDateTime(LocalDateTime.now().minusDays(5).withHour(17).withMinute(0))
          .totalVacancies(50)
          .speaker("Agile Master")
          .organizer(organizador1)
          .active(true)
          .build();
      eventoPassado = eventRepository.save(eventoPassado);

      Event eventoLimitado = Event.builder()
          .name("Oficina de UX/UI Design")
          .description("Oficina prática de design de interfaces")
          .type(Event.EventType.OFICINA)
          .location("Sala de Design")
          .startDateTime(LocalDateTime.now().plusDays(12).withHour(14).withMinute(0))
          .endDateTime(LocalDateTime.now().plusDays(12).withHour(16).withMinute(0))
          .totalVacancies(5)
          .speaker("Designer Expert")
          .organizer(organizador2)
          .active(true)
          .build();
      eventoLimitado = eventRepository.save(eventoLimitado);

      List<User> participantes = List.of(participante1, participante2);

      for (int i = 0; i < 2; i++) {
        Registration registration = Registration.builder()
            .event(evento1)
            .participant(participantes.get(i))
            .registrationDate(LocalDateTime.now().minusDays(3))
            .status(Registration.RegistrationStatus.INSCRITO)
            .attended(false)
            .build();
        registrationRepository.save(registration);
      }


      for (int i = 0; i < 3; i++) {
        Registration registration = Registration.builder()
            .event(eventoPassado)
            .participant(participantes.get(i))
            .registrationDate(LocalDateTime.now().minusDays(8))
            .status(Registration.RegistrationStatus.CONFIRMADO)
            .attended(i < 2)
            .build();
        registrationRepository.save(registration);
      }

      for (int i = 0; i < 4; i++) {
        Registration registration = Registration.builder()
            .event(eventoLimitado)
            .participant(participantes.get(i))
            .registrationDate(LocalDateTime.now().minusDays(1))
            .status(Registration.RegistrationStatus.INSCRITO)
            .attended(false)
            .build();
        registrationRepository.save(registration);
      }


      System.out.println("=== Banco de dados inicializado com dados de teste ===");
      System.out.println("Usuários criados: 7 (1 admin, 2 organizadores, 4 participantes)");
      System.out.println("Eventos criados: 6 (5 futuros, 1 passado)");
      System.out.println("Registros criados: 15");
      System.out.println("=== Dados de login para teste ===");
      System.out.println("Admin: admin@example.com / password");
      System.out.println("Organizador 1: joao.organizador@example.com / password");
      System.out.println("Organizador 2: maria.organizadora@example.com / password");
      System.out.println("Participante: carlos@example.com / password");
    }
  }
}