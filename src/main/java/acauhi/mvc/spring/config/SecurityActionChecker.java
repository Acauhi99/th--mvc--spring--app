package acauhi.mvc.spring.config;

import acauhi.mvc.spring.entity.User;
import acauhi.mvc.spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("securityActionChecker")
@RequiredArgsConstructor
public class SecurityActionChecker {
  private final UserService userService;

  public boolean isAdmin(Authentication authentication) {
    return authentication.getAuthorities()
        .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
  }

  public boolean isSelf(Authentication authentication, UUID userId) {
    return userService.findById(userId)
        .map(user -> user.getEmail().equals(authentication.getName()))
        .orElse(false);
  }

  public boolean isAdminOrSelf(Authentication authentication, UUID userId) {
    return isAdmin(authentication) || isSelf(authentication, userId);
  }

  public boolean canModifyUser(Authentication authentication, User user, UUID targetUserId) {
    if (isAdmin(authentication)) {
      return true;
    }

    // Não-admins só podem modificar seus próprios perfis
    if (!isSelf(authentication, targetUserId)) {
      return false;
    }

    return userService.findById(targetUserId)
        .map(existingUser -> existingUser.getUserType() == user.getUserType())
        .orElse(false);
  }
}
