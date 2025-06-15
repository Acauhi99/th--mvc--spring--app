package acauhi.mvc.spring.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

  // Identificação
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  // Informações pessoais
  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String email;

  // Autenticação
  @Column(nullable = false)
  private String password;

  // Tipo de usuário
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserType userType;

  // Enum para tipos de usuário
  public enum UserType {
    ADMIN, PARTICIPANTE, ORGANIZADOR
  }
}
