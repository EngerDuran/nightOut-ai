package com.ironhack.nightoutai.security;

import com.ironhack.nightoutai.security.filters.CustomAuthenticationFilter;
import com.ironhack.nightoutai.security.filters.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


@Configuration
@EnableWebSecurity // indicates it is a security config class using spring web security
@RequiredArgsConstructor
public class SecurityConfig {


    private final UserDetailsService userDetailsService;

    private final AuthenticationManagerBuilder authManagerBuilder;


    /**
     * Bean definition for AuthenticationManager
     *
     * @param authenticationConfiguration the instance of AuthenticationConfiguration
     * @return an instance of the AuthenticationManager
     * @throws Exception if there is an issue getting the instance of the AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Bean definition for SecurityFilterChain
     *
     * @param http the instance of HttpSecurity
     * @return an instance of the SecurityFilterChain
     * @throws Exception if there is an issue building the SecurityFilterChain
     */
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authManagerBuilder.getOrBuild());

        customAuthenticationFilter.setFilterProcessesUrl("/api/login");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/api/login").permitAll()// public endpoint
                        .requestMatchers(POST, "/api/tickets").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .requestMatchers(GET, "/api/users").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .requestMatchers(POST, "/api/users").permitAll()//("ROLE_ADMIN")
                        .requestMatchers(POST, "/api/roles").hasAnyAuthority("ROLE_ADMIN")

                        //reglas para simular el Administrador del Club:
                        .requestMatchers(POST, "/api/events").hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers(GET, "/api/events").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .anyRequest().authenticated());

        http.addFilter(customAuthenticationFilter);

        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
