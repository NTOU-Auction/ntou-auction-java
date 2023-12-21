package ntou.auction.spring.security;

import ntou.auction.spring.core.AppConfig;
import ntou.auction.spring.data.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import java.util.HashMap;
import java.util.Map;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {
    private final AppConfig appConfig;
    private final JWTRequestFilter jwtRequestFilter;

    public SecurityConfiguration(AppConfig appConfig, JWTRequestFilter jwtRequestFilter) {
        this.appConfig = appConfig;
        this.jwtRequestFilter = jwtRequestFilter;
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
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/log-in").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/sign-up").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/product/products").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/product/product/name/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/product/product/classification/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/product/product/{ID}").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/sockjs/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
