package acauhi.mvc.spring.service;

import acauhi.mvc.spring.entity.User;
import acauhi.mvc.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  // Operações de registro e autenticação
  public User registerUser(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  public boolean emailExists(String email) {
    return userRepository.findByEmail(email).isPresent();
  }

  public Optional<User> findUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }
}
