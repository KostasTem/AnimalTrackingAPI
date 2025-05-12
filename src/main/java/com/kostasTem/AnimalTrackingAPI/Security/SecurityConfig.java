package com.kostasTem.AnimalTrackingAPI.Security;

import com.kostasTem.AnimalTrackingAPI.Services.UserService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final RsaKeyProperties jwtConfigProperties;
    private final UserService userService;

    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.ignoringRequestMatchers("/api/**"));
        http.exceptionHandling(
                (exceptions) ->
                        exceptions
                                .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                                .accessDeniedHandler(new BearerTokenAccessDeniedHandler()));
        http.cors(AbstractHttpConfigurer::disable);
        http
                .authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers("/api/user/**").permitAll()
                                .requestMatchers("/api/device/**").permitAll()
                                .requestMatchers("/api/test/**").permitAll()
                                .requestMatchers("/api/posts/**").permitAll()
                                .requestMatchers("/api/report/**").permitAll()
                                .requestMatchers("/api/animals/**").permitAll()
                                .anyRequest().authenticated()
                );
        http.sessionManagement((ses) -> ses.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.oauth2ResourceServer((oAuth2ResourceServerConfigurer) -> oAuth2ResourceServerConfigurer.jwt(Customizer.withDefaults()));
        return http.build();
    }
    @Bean
    public JwtEncoder jwtEncoder() {
        var jwk = new RSAKey.Builder(this.jwtConfigProperties.publicKey()).privateKey(jwtConfigProperties.privateKey()).build();
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    // Used by JwtAuthenticationProvider to decode and validate JWT tokens
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(jwtConfigProperties.publicKey()).build();
    }

    // Extract authorities from the roles claim
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        var jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}

}