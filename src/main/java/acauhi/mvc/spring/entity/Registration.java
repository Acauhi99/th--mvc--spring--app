package acauhi.mvc.spring.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "registrations", uniqueConstraints = @UniqueConstraint(columnNames = { "event_id", "participant_id" }))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Registration {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id", nullable = false,
              foreignKey = @ForeignKey(name = "fk_registration_event", 
                                      foreignKeyDefinition = "FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE"))
  private Event event;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "participant_id", nullable = false,
              foreignKey = @ForeignKey(name = "fk_registration_participant", 
                                      foreignKeyDefinition = "FOREIGN KEY (participant_id) REFERENCES users(id) ON DELETE CASCADE"))
  private User participant;

  @Column(nullable = false)
  private LocalDateTime registrationDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RegistrationStatus status;

  @Builder.Default
  private Boolean attended = false;

  public enum RegistrationStatus {
    INSCRITO, CANCELADO, CONFIRMADO
  }

  @PrePersist
  protected void onCreate() {
    registrationDate = LocalDateTime.now();
    if (status == null) {
      status = RegistrationStatus.INSCRITO;
    }
  }
}