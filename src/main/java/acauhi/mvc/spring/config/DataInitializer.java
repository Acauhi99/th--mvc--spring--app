package acauhi.mvc.spring.config;

import acauhi.mvc.spring.entity.Event;
import acauhi.mvc.spring.entity.Registration;
import acauhi.mvc.spring.entity.User;
import acauhi.mvc.spring.service.UserService;
import acauhi.mvc.spring.repository.EventRepository;
import acauhi.mvc.spring.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userService.findAll().isEmpty()) {
            Random random = new Random();

            // === CRIAÇÃO DE USUÁRIOS ===
            User admin = User.builder()
                    .name("Admin User")
                    .email("admin@example.com")
                    .password("password")
                    .userType(User.UserType.ADMIN)
                    .build();
            admin = userService.save(admin);

            // Organizadores
            User organizador1 = User.builder()
                    .name("João Silva")
                    .email("joao.organizador@example.com")
                    .password("password")
                    .userType(User.UserType.ORGANIZADOR)
                    .build();
            organizador1 = userService.save(organizador1);

            User organizador2 = User.builder()
                    .name("Maria Santos")
                    .email("maria.organizadora@example.com")
                    .password("password")
                    .userType(User.UserType.ORGANIZADOR)
                    .build();
            organizador2 = userService.save(organizador2);

            User organizador3 = User.builder()
                    .name("Pedro Oliveira")
                    .email("pedro.organizador@example.com")
                    .password("password")
                    .userType(User.UserType.ORGANIZADOR)
                    .build();
            organizador3 = userService.save(organizador3);

            // Participantes
            List<User> participantes = new ArrayList<>();
            String[] nomes = {
                "Carlos Silva", "Ana Costa", "Bruno Santos", "Lucia Ferreira", "Roberto Lima",
                "Fernanda Souza", "Marcos Pereira", "Juliana Rocha", "Ricardo Alves", "Patricia Gomes",
                "André Martins", "Camila Ribeiro", "Diego Torres", "Leticia Cardoso", "Fabio Nascimento",
                "Monica Barbosa", "Thiago Mendes", "Vanessa Castro", "Leonardo Dias", "Gabriela Freitas",
                "Rafael Moura", "Isabella Campos", "Gustavo Pinto", "Natalia Ramos", "Vinicius Teixeira",
                "Larissa Vieira", "Henrique Correia", "Amanda Silva", "Daniel Rodrigues", "Carolina Nunes",
                "Eduardo Santos", "Beatriz Lima", "Felipe Costa", "Mariana Alves", "Rodrigo Ferreira",
                "Camila Nascimento", "Lucas Pereira", "Isabelle Rocha", "Matheus Souza", "Larissa Gomes"
            };

            for (int i = 0; i < nomes.length; i++) {
                User participante = User.builder()
                        .name(nomes[i])
                        .email("participante" + (i + 1) + "@example.com")
                        .password("password")
                        .userType(User.UserType.PARTICIPANTE)
                        .build();
                participantes.add(userService.save(participante));
            }

            // === CRIAÇÃO DE EVENTOS DISTRIBUÍDOS AO LONGO DO ANO ===
            List<Event> eventos = new ArrayList<>();

            // Eventos do Organizador 1 - João Silva (Tecnologia) - Distribuídos nos últimos 12 meses
            String[] eventosTech = {
                "Introdução ao Spring Boot", "Microserviços com Docker", "React Native para Iniciantes",
                "Banco de Dados NoSQL", "DevOps e CI/CD", "Segurança em APIs REST",
                "Machine Learning Básico", "Cloud Computing com AWS", "Kubernetes Fundamentals",
                "GraphQL na Prática", "Testes Automatizados", "Arquitetura de Software"
            };

            for (int i = 0; i < eventosTech.length; i++) {
                // Distribuir eventos pelos últimos 12 meses
                LocalDateTime startDate = LocalDateTime.now().minusMonths(11 - i).withDayOfMonth(5 + random.nextInt(20));
                Event evento = Event.builder()
                        .name(eventosTech[i])
                        .description("Evento de tecnologia: " + eventosTech[i])
                        .type(Event.EventType.values()[random.nextInt(Event.EventType.values().length)])
                        .location("Sala " + (101 + i) + " - Centro de Tecnologia")
                        .startDateTime(startDate.withHour(14).withMinute(0))
                        .endDateTime(startDate.withHour(18).withMinute(0))
                        .totalVacancies(20 + random.nextInt(30))
                        .speaker("Especialista Tech " + (i + 1))
                        .organizer(organizador1)
                        .active(true)
                        .build();
                eventos.add(eventRepository.save(evento));
            }

            // Eventos do Organizador 2 - Maria Santos (Negócios) - Distribuídos nos últimos 10 meses
            String[] eventosNegocios = {
                "Gestão de Projetos Ágeis", "Marketing Digital Avançado", "Empreendedorismo Inovador",
                "Liderança e Comunicação", "Finanças Empresariais", "Estratégias de Vendas",
                "Transformação Digital", "Gestão de Pessoas", "Planejamento Estratégico", "Inovação Corporativa"
            };

            for (int i = 0; i < eventosNegocios.length; i++) {
                // Distribuir eventos pelos últimos 10 meses
                LocalDateTime startDate = LocalDateTime.now().minusMonths(9 - i).withDayOfMonth(10 + random.nextInt(15));
                Event evento = Event.builder()
                        .name(eventosNegocios[i])
                        .description("Evento de negócios: " + eventosNegocios[i])
                        .type(Event.EventType.values()[random.nextInt(Event.EventType.values().length)])
                        .location("Auditório " + (i + 1) + " - Centro de Negócios")
                        .startDateTime(startDate.withHour(19).withMinute(0))
                        .endDateTime(startDate.withHour(22).withMinute(0))
                        .totalVacancies(30 + random.nextInt(20))
                        .speaker("Expert Negócios " + (i + 1))
                        .organizer(organizador2)
                        .active(true)
                        .build();
                eventos.add(eventRepository.save(evento));
            }

            // Eventos do Organizador 3 - Pedro Oliveira (Design) - Distribuídos nos últimos 8 meses
            String[] eventosDesign = {
                "UX/UI Design Fundamentals", "Prototipagem Rápida", "Design Thinking Workshop",
                "Ilustração Digital", "Branding e Identidade Visual", "Design Systems",
                "Motion Graphics", "Design Responsivo"
            };

            for (int i = 0; i < eventosDesign.length; i++) {
                // Distribuir eventos pelos últimos 8 meses
                LocalDateTime startDate = LocalDateTime.now().minusMonths(7 - i).withDayOfMonth(15 + random.nextInt(10));
                Event evento = Event.builder()
                        .name(eventosDesign[i])
                        .description("Evento de design: " + eventosDesign[i])
                        .type(Event.EventType.values()[random.nextInt(Event.EventType.values().length)])
                        .location("Laboratório de Design " + (i + 1))
                        .startDateTime(startDate.withHour(15).withMinute(0))
                        .endDateTime(startDate.withHour(19).withMinute(0))
                        .totalVacancies(15 + random.nextInt(15))
                        .speaker("Design Master " + (i + 1))
                        .organizer(organizador3)
                        .active(true)
                        .build();
                eventos.add(eventRepository.save(evento));
            }

            // Eventos futuros
            String[] eventosFuturos = {
                "Inteligência Artificial na Prática", "Blockchain e Criptomoedas", 
                "Sustentabilidade Empresarial", "E-commerce do Futuro", "Metaverso e Web3",
                "Dados e Analytics"
            };

            for (int i = 0; i < eventosFuturos.length; i++) {
                LocalDateTime startDate = LocalDateTime.now().plusWeeks(i + 1);
                Event evento = Event.builder()
                        .name(eventosFuturos[i])
                        .description("Evento futuro: " + eventosFuturos[i])
                        .type(Event.EventType.values()[random.nextInt(Event.EventType.values().length)])
                        .location("Sala Futura " + (i + 1))
                        .startDateTime(startDate.withHour(14).withMinute(0))
                        .endDateTime(startDate.withHour(18).withMinute(0))
                        .totalVacancies(25 + random.nextInt(25))
                        .speaker("Futurista " + (i + 1))
                        .organizer(i % 3 == 0 ? organizador1 : (i % 3 == 1 ? organizador2 : organizador3))
                        .active(true)
                        .build();
                eventos.add(eventRepository.save(evento));
            }

            // === CRIAÇÃO DE INSCRIÇÕES ===
            for (Event evento : eventos) {
                // Determinar quantas inscrições criar
                int maxInscricoes;
                if (evento.getStartDateTime().isBefore(LocalDateTime.now())) {
                    maxInscricoes = (int) (evento.getTotalVacancies() * (0.6 + random.nextDouble() * 0.3));
                } else {
                    maxInscricoes = (int) (evento.getTotalVacancies() * (0.3 + random.nextDouble() * 0.4));
                }

                // Criar inscrições aleatórias
                List<User> participantesDisponiveis = new ArrayList<>(participantes);
                for (int i = 0; i < maxInscricoes && !participantesDisponiveis.isEmpty(); i++) {
                    // Selecionar participante aleatório
                    int participanteIndex = random.nextInt(participantesDisponiveis.size());
                    User participante = participantesDisponiveis.remove(participanteIndex);

                    // Determinar data de inscrição
                    LocalDateTime dataInscricao;
                    if (evento.getStartDateTime().isBefore(LocalDateTime.now())) {
                        // Para eventos passados, criar inscrições distribuídas ao longo do tempo
                        int diasAntes = random.nextInt(30) + 1;
                        dataInscricao = evento.getStartDateTime().minusDays(diasAntes);
                    } else {
                        // Para eventos futuros
                        dataInscricao = LocalDateTime.now().minusDays(random.nextInt(7));
                    }

                    // Determinar status da inscrição
                    Registration.RegistrationStatus status;
                    if (evento.getStartDateTime().isBefore(LocalDateTime.now())) {
                        status = random.nextDouble() < 0.8 ? 
                                Registration.RegistrationStatus.CONFIRMADO : 
                                Registration.RegistrationStatus.CANCELADO;
                    } else {
                        status = random.nextDouble() < 0.85 ? 
                                Registration.RegistrationStatus.INSCRITO : 
                                Registration.RegistrationStatus.CANCELADO;
                    }

                    // Determinar se compareceu
                    boolean compareceu = false;
                    if (evento.getStartDateTime().isBefore(LocalDateTime.now()) && 
                        status == Registration.RegistrationStatus.CONFIRMADO) {
                        compareceu = random.nextDouble() < 0.85;
                    }

                    Registration registration = Registration.builder()
                            .event(evento)
                            .participant(participante)
                            .registrationDate(dataInscricao)
                            .status(status)
                            .attended(compareceu)
                            .build();
                    registrationRepository.save(registration);
                }
            }

            // === LOGS DE CRIAÇÃO ===
            System.out.println("=== DADOS INICIALIZADOS COM SUCESSO ===");
            System.out.println("✓ Usuários criados: " + (4 + participantes.size()));
            System.out.println("  - 1 Admin");
            System.out.println("  - 3 Organizadores");
            System.out.println("  - " + participantes.size() + " Participantes");
            System.out.println("✓ Eventos criados: " + eventos.size());
            System.out.println("  - " + eventosTech.length + " eventos de Tecnologia (João) - últimos 12 meses");
            System.out.println("  - " + eventosNegocios.length + " eventos de Negócios (Maria) - últimos 10 meses");
            System.out.println("  - " + eventosDesign.length + " eventos de Design (Pedro) - últimos 8 meses");
            System.out.println("  - " + eventosFuturos.length + " eventos futuros");
            
            long totalRegistrations = registrationRepository.count();
            System.out.println("✓ Inscrições criadas: " + totalRegistrations);
            System.out.println("=== BANCO DE DADOS PRONTO COM DADOS DISTRIBUÍDOS AO LONGO DO ANO ===");
        }
    }
}