package acauhi.mvc.spring.service;

import acauhi.mvc.spring.entity.User;
import acauhi.mvc.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    if (user.getId() != null && (user.getPassword() == null || user.getPassword().trim().isEmpty())) {
      User existingUser = findById(user.getId()).orElseThrow();
      user.setPassword(existingUser.getPassword());
    } else if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
    }
    return userRepository.save(user);
  }

  public void deleteById(UUID id) {
    userRepository.deleteById(id);
  }

  public User updateUser(User updatedUser, String newPassword) {
    User existingUser = findById(updatedUser.getId())
        .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + updatedUser.getId()));

    existingUser.setName(updatedUser.getName());
    existingUser.setEmail(updatedUser.getEmail());

    if (hasAdminAuthority()) {
      existingUser.setUserType(updatedUser.getUserType());
    }

    if (newPassword != null && !newPassword.isBlank()) {
      existingUser.setPassword(passwordEncoder.encode(newPassword));
    }

    return userRepository.save(existingUser);
  }

  private boolean hasAdminAuthority() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null &&
        authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
  }
}