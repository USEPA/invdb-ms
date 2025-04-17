package gov.epa.ghg.invdb.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import lombok.extern.log4j.Log4j2;

@Configuration
@EnableWebSecurity
@Log4j2
public class SecurityConfig {

        @Autowired
        private RsaKeyConfigProperties rsaKeyConfigProperties;

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedHeaders(List.of("Authorization", "Accept",
                                "Cache-Control", "Content-Type"));
                configuration.setAllowedOriginPatterns(List.of("http://localhost:[*]",
                                "https://uat.ccdsupport.com",
                                "https://ghg-test.saic.com", "https://ghg-test-r8.corp.saic.com",
                                "https://ghg-jenkins.saic.com", "https://ghg-jenkins-r8.corp.saic.com"));
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH",
                                "DELETE"));
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(86400L);
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http)
                        throws Exception {
                http
                                .cors(Customizer.withDefaults())
                                /*
                                 * sets the session creation policy to STATELESS, meaning that no session will
                                 * be created or used for authentication.
                                 * Never disable CSRF protection while leaving session management enabled! Doing
                                 * so will open you up to a Cross-Site Request Forgery attack.
                                 */
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/api/user/load", "/api/login").permitAll()
                                                .anyRequest().authenticated())
                                .oauth2ResourceServer((oauth2) -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())));

                return http.build();
        }

        @Bean
        public JwtDecoder jwtDecoder() {
                return NimbusJwtDecoder.withPublicKey(rsaKeyConfigProperties.rsaPublicKey()).build();
        }

        @Bean
        JwtEncoder jwtEncoder() {
                JWK jwk = new RSAKey.Builder(rsaKeyConfigProperties.rsaPublicKey())
                                .privateKey(rsaKeyConfigProperties.rsaPrivateKey())
                                .build();
                JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
                return new NimbusJwtEncoder(jwks);
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }
}
