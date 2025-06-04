package acauhi.mvc.spring.config;

import acauhi.mvc.spring.entity.User;
import acauhi.mvc.spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final UserService userService;

  @Override
  public void run(String... args) throws Exception {
    // Só cria usuários se o banco estiver vazio
    if (userService.findAll().isEmpty()) {
      User admin = User.builder()
          .name("Admin User")
          .email("admin@example.com")
          .password("password")
          .userType(User.UserType.ADMIN)
          .build();
      userService.save(admin);

      User employee = User.builder()
          .name("Employee User")
          .email("employee@example.com")
          .password("password")
          .userType(User.UserType.ORGANIZADOR)
          .build();
      userService.save(employee);

      User client = User.builder()
          .name("Client User")
          .email("client@example.com")
          .password("password")
          .userType(User.UserType.PARTICIPANTE)
          .build();
      userService.save(client);

      System.out.println("Banco de dados inicializado com usuários de teste!");
    }
  }
}