package acauhi.mvc.spring.config;

import acauhi.mvc.spring.entity.User;
import acauhi.mvc.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

  private final UserRepository userRepository;

  // Verifica se existe mais de um administrador no sistema
  public boolean hasMultipleAdmins() {
    long adminCount = userRepository.countByUserType(User.UserType.ADMIN);
    return adminCount > 1;
  }
}