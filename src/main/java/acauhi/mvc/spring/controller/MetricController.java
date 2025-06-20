package acauhi.mvc.spring.controller;

import acauhi.mvc.spring.entity.User;
import acauhi.mvc.spring.service.EventService;
import acauhi.mvc.spring.service.RegistrationService;
import acauhi.mvc.spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/metrics")
@RequiredArgsConstructor
public class MetricController {

    private final EventService eventService;
    private final RegistrationService registrationService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ORGANIZADOR')")
    public String viewMetrics(Model model, Authentication authentication) {
        User currentUser = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        UUID organizerId = currentUser.getId();
        
        if (currentUser.getUserType() == User.UserType.ADMIN) {
            List<User> organizadores = userService.findAll().stream()
                    .filter(u -> u.getUserType() == User.UserType.ORGANIZADOR)
                    .toList();
            if (!organizadores.isEmpty()) {
                organizerId = organizadores.get(0).getId();
            }
        }

        Long totalEvents = eventService.countEventsByOrganizerId(organizerId);
        Long totalRegistrations = registrationService.countTotalRegistrationsByOrganizerId(organizerId);

        model.addAttribute("totalEvents", totalEvents);
        model.addAttribute("totalRegistrations", totalRegistrations);

        // Dados para gráfico de inscrições por evento
        List<Object[]> registrationsByEvent = registrationService.findRegistrationsCountByEventForOrganizer(organizerId);

        System.out.println("=== DEBUG METRICS ===");
        System.out.println("Organizer ID: " + organizerId);
        System.out.println("Total Events: " + totalEvents);
        System.out.println("Total Registrations: " + totalRegistrations);
        System.out.println("Registrations by Event size: " + registrationsByEvent.size());

        StringBuilder eventNamesJson = new StringBuilder("[");
        StringBuilder registrationCountsJson = new StringBuilder("[");
        
        for (int i = 0; i < registrationsByEvent.size(); i++) {
            Object[] data = registrationsByEvent.get(i);
            System.out.println("Event data [" + i + "]: " + data[0] + " = " + data[1]);
            
            if (i > 0) {
                eventNamesJson.append(",");
                registrationCountsJson.append(",");
            }
            eventNamesJson.append("\"").append(data[0].toString()).append("\"");
            registrationCountsJson.append(data[1].toString());
        }
        eventNamesJson.append("]");
        registrationCountsJson.append("]");
        
        model.addAttribute("eventNamesJson", eventNamesJson.toString());
        model.addAttribute("registrationCountsJson", registrationCountsJson.toString());

        // Dados para gráfico de inscrições por mês (último ano)
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        List<Object[]> registrationsByMonth = registrationService.findRegistrationsByMonthForOrganizer(
                organizerId, oneYearAgo);

        System.out.println("Registrations by Month size: " + registrationsByMonth.size());
        
        // Criar mapa para facilitar o preenchimento dos dados
        Map<Integer, Long> monthlyData = new HashMap<>();
        for (Object[] data : registrationsByMonth) {
            int month = ((Number) data[0]).intValue();
            long count = ((Number) data[1]).longValue();
            monthlyData.put(month, count);
        }
        
        StringBuilder monthNamesJson = new StringBuilder("[");
        StringBuilder monthlyRegistrationsJson = new StringBuilder("[");
        
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                          "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        
        // Começar do mês atual e voltar 12 meses
        LocalDateTime currentMonth = LocalDateTime.now().withDayOfMonth(1);
        for (int i = 0; i < 12; i++) {
            LocalDateTime monthToShow = currentMonth.minusMonths(11 - i);
            int monthNumber = monthToShow.getMonthValue();
            
            if (i > 0) {
                monthNamesJson.append(",");
                monthlyRegistrationsJson.append(",");
            }
            
            monthNamesJson.append("\"").append(months[monthNumber - 1]).append("\"");
            monthlyRegistrationsJson.append(monthlyData.getOrDefault(monthNumber, 0L));
            
            System.out.println("Month " + months[monthNumber - 1] + ": " + monthlyData.getOrDefault(monthNumber, 0L));
        }
        
        monthNamesJson.append("]");
        monthlyRegistrationsJson.append("]");
        
        model.addAttribute("monthNamesJson", monthNamesJson.toString());
        model.addAttribute("monthlyRegistrationsJson", monthlyRegistrationsJson.toString());

        model.addAttribute("averageRegistrationsPerEvent", 
                totalEvents > 0 ? totalRegistrations / totalEvents : 0);

        System.out.println("Event Names JSON: " + eventNamesJson.toString());
        System.out.println("Registration Counts JSON: " + registrationCountsJson.toString());
        System.out.println("Month Names JSON: " + monthNamesJson.toString());
        System.out.println("Monthly Registrations JSON: " + monthlyRegistrationsJson.toString());
        System.out.println("=== END DEBUG ===");

        return "pages/metrics/view";
    }
}
