package rinhacampusiv.api.v2.infra.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Autowired
    private SecurityFilter securityFilter;

    @Autowired
    private CustomAuthEntryPoint authEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // ← preflight sempre passa
                        .requestMatchers(HttpMethod.POST, "/auth/register", "/auth/login", "/auth/refresh", "/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/me").permitAll()
                        // Torneios — leitura pública, escrita restrita ao ADMIN
                        .requestMatchers(HttpMethod.GET, "/tournaments/me/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/tournaments/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Autorizar /webhook para a notificação do pagamento do mercadopago
                        .requestMatchers(HttpMethod.POST, "/webhook").permitAll()
                        // Autorizar /ws/** para os cliente que vai se inscrever no websocket
                        .requestMatchers("/ws/**").permitAll()
                        // Autorizar rotas de recuperação de senha
                        .requestMatchers(HttpMethod.POST, "/auth/password-reset/request", "/auth/password-reset/confirm").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/auth/password-reset/validate").permitAll()
                        // Autorizar rotas de validação de conta
                        .requestMatchers(HttpMethod.GET,  "/auth/activate/validate").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/activate", "/auth/activate/resend").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)  // 401
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Acesso negado\"}");
                        })
                ).build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){ //Mostrar para o Spring qual algoritmo de criptografia de senha estamos utilizando
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000")); // NÃO "*"
        config.setAllowedMethods(List.of("GET","POST","PUT", "PATCH", "DELETE","OPTIONS"));
        config.setAllowCredentials(true); // muito importante
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }



}
