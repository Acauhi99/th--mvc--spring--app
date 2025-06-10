package acauhi.mvc.spring.config;

import acauhi.mvc.spring.entity.User;
import acauhi.mvc.spring.service.UserService;
import acauhi.mvc.spring.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("securityActionChecker")
@RequiredArgsConstructor
public class SecurityActionChecker {

  private final UserService userService;
  private final EventService eventService;

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

  public boolean canModifyUser(Authentication authentication, User updatedUser, UUID targetUserId) {
    if (isAdmin(authentication)) {
      return true;
    }

    if (!isSelf(authentication, targetUserId)) {
      return false;
    }

    User existingUser = userService.findById(targetUserId).orElse(null);
    return existingUser != null && existingUser.getUserType() == updatedUser.getUserType();
  }

  public boolean canModifyEvent(Authentication authentication, UUID eventId) {
    if (isAdmin(authentication)) {
      return true;
    }

    return eventService.findById(eventId)
        .map(event -> event.getOrganizer().getEmail().equals(authentication.getName()))
        .orElse(false);
  }

  public boolean canDeleteUser(Authentication authentication, UUID userId) {
    if (isAdmin(authentication)) {
      return true;
    }

    return isSelf(authentication, userId);
  }
}
