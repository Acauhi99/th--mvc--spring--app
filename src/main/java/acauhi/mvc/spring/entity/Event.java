package acauhi.mvc.spring.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events", indexes = {
    @Index(name = "idx_event_start_date", columnList = "startDateTime"),
    @Index(name = "idx_event_organizer", columnList = "organizer_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

  // Identificação
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  // Informações básicas do evento
  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EventType type;

  // Logística
  @Column(nullable = false)
  private String location;

  @Column(nullable = false)
  private LocalDateTime startDateTime;

  @Column(nullable = false)
  private LocalDateTime endDateTime;

  @Column(nullable = false)
  private Integer totalVacancies;

  private String speaker;

  // Relacionamentos
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organizer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_event_organizer", foreignKeyDefinition = "FOREIGN KEY (organizer_id) REFERENCES users(id) ON DELETE CASCADE"))
  private User organizer;

  // Status
  @Column(nullable = false)
  @Builder.Default
  private Boolean active = true;

  // Enum para tipos de evento
  public enum EventType {
    PALESTRA, CURSO, OFICINA, FEIRA, WORKSHOP, SEMINARIO
  }

  // Métodos auxiliares para gerenciamento de vagas
  @Transient
  public int getOccupiedVacancies() {
    // Este método será implementado via query no service
    return 0;
  }

  @Transient
  public int getAvailableVacancies() {
    return totalVacancies - getOccupiedVacancies();
  }

  // Métodos para verificação de estado
  @Transient
  public boolean isPastEvent() {
    return startDateTime.isBefore(LocalDateTime.now());
  }

  @Transient
  public boolean isInProgress() {
    LocalDateTime now = LocalDateTime.now();
    return startDateTime.isBefore(now) && endDateTime.isAfter(now);
  }
}