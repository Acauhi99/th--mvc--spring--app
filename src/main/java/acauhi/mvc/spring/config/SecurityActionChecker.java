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

  // Verifica se o usuário tem o papel de ADMIN
  public boolean isAdmin(Authentication authentication) {
    return authentication.getAuthorities()
        .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
  }

  // Verifica se o usuário autenticado é o próprio usuário sendo acessado
  public boolean isSelf(Authentication authentication, UUID userId) {
    return userService.findById(userId)
        .map(user -> user.getEmail().equals(authentication.getName()))
        .orElse(false);
  }

  // Verifica se o usuário é ADMIN ou o próprio usuário
  public boolean isAdminOrSelf(Authentication authentication, UUID userId) {
    return isAdmin(authentication) || isSelf(authentication, userId);
  }

  // Verifica permissões para modificar um usuário
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

  // Verifica permissões para modificar um evento
  public boolean canModifyEvent(Authentication authentication, UUID eventId) {
    if (isAdmin(authentication)) {
      return true;
    }

    return eventService.findById(eventId)
        .map(event -> event.getOrganizer().getEmail().equals(authentication.getName()))
        .orElse(false);
  }

  // Verifica permissões para excluir um usuário
  public boolean canDeleteUser(Authentication authentication, UUID userId) {
    if (isAdmin(authentication)) {
      return true;
    }

    return isSelf(authentication, userId);
  }
}
