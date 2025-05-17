package acauhi.mvc.spring.config;

import acauhi.mvc.spring.entity.User;
import acauhi.mvc.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final UserRepository userRepository;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/css/**", "/js/**", "/images/**", "/styles.css").permitAll()
            .requestMatchers("/", "/login", "/register").permitAll()
            .requestMatchers("/users").hasAuthority("ROLE_ADMIN")
            .requestMatchers("/users/create").hasAuthority("ROLE_ADMIN")
            .requestMatchers("/users/edit/**").authenticated()
            .requestMatchers("/users/delete/**").hasAuthority("ROLE_ADMIN")
            .requestMatchers("/users/profile").authenticated()
            .anyRequest().authenticated())
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/")
            .permitAll())
        .logout(logout -> logout
            .logoutSuccessUrl("/")
            .permitAll());

    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return username -> userRepository.findByEmail(username)
        .map(this::mapToUserDetails)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
  }

  private org.springframework.security.core.userdetails.User mapToUserDetails(User user) {
    return new org.springframework.security.core.userdetails.User(
        user.getEmail(),
        user.getPassword(),
        List.of(new SimpleGrantedAuthority("ROLE_" + user.getUserType().name())));
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
