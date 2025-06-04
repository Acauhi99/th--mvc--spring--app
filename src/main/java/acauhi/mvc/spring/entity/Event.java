package acauhi.mvc.spring.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EventType type;

  @Column(nullable = false)
  private String location;

  @Column(nullable = false)
  private LocalDateTime startDateTime;

  @Column(nullable = false)
  private LocalDateTime endDateTime;

  @Column(nullable = false)
  private Integer totalVacancies;

  private String speaker; // Palestrante

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organizer_id", nullable = false)
  private User organizer;

  @Column(nullable = false)
  @Builder.Default
  private Boolean active = true;

  public enum EventType {
    PALESTRA, CURSO, OFICINA, FEIRA, WORKSHOP, SEMINARIO
  }

  // Método auxiliar para calcular vagas ocupadas
  @Transient
  public int getOccupiedVacancies() {
    // Será implementado via query no service
    return 0;
  }

  @Transient
  public int getAvailableVacancies() {
    return totalVacancies - getOccupiedVacancies();
  }
}