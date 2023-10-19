package ntou.auction.spring.security;

import ntou.auction.spring.core.AppConfig;
import ntou.auction.spring.data.Role;
import ntou.auction.spring.data.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {
    private final AppConfig appConfig;

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfiguration(AppConfig appConfig, UserDetailsServiceImpl userDetailsService) {
        this.appConfig = appConfig;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // the following value can be changed to meet your need
        // pbkdf2
        String secret = "";
        // byte
        int pbkdf2SaltLength = 16;
        int pbkdf2Iterations = 310000;
        Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm secretKeyFactoryAlgorithm =
                Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256;
        // argon2
        int argon2SaltLength = 16;
        int hashLength = 32;
        int parallelism = 1;
        int memory = 1 << 14;
        int argon2Iterations = 2;
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("BCrypt", new BCryptPasswordEncoder());
        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder(secret,
                pbkdf2SaltLength, pbkdf2Iterations, secretKeyFactoryAlgorithm));
        encoders.put("pbkdf2@SpringSecurity_v5_8",
                Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoders.put("argon2", new Argon2PasswordEncoder(argon2SaltLength,
                hashLength, parallelism, memory, argon2Iterations));
        encoders.put("argon2@SpringSecurity_v5_8",
                Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        return new DelegatingPasswordEncoder(appConfig.getIdForEncode(),
                encoders);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET,"/users/**").authenticated()
                        .requestMatchers(HttpMethod.GET).permitAll()
                        .requestMatchers(HttpMethod.POST,"/users").permitAll()
                        .requestMatchers(HttpMethod.DELETE,"/users/**").hasRole(String.valueOf(Role.ADMIN))
                        .anyRequest().authenticated())
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .formLogin(withDefaults());
        return http.build();
    }

}
