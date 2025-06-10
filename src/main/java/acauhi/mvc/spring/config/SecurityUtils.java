package acauhi.mvc.spring.config;

import acauhi.mvc.spring.entity.User;
import acauhi.mvc.spring.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

  private static UserRepository userRepository;

  public SecurityUtils(UserRepository userRepository) {
    SecurityUtils.userRepository = userRepository;
  }

  public static boolean hasMultipleAdmins() {
    long adminCount = userRepository.countByUserType(User.UserType.ADMIN);
    return adminCount > 1;
  }
}