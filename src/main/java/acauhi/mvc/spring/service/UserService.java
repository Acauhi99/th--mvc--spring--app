package acauhi.mvc.spring.service;

import acauhi.mvc.spring.entity.User;
import acauhi.mvc.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public List<User> findAll() {
    return userRepository.findAll();
  }

  public Optional<User> findById(UUID id) {
    return userRepository.findById(id);
  }

  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public User save(User user) {
    if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
    }
    return userRepository.save(user);
  }

  public void deleteById(UUID id) {
    userRepository.deleteById(id);
  }
}