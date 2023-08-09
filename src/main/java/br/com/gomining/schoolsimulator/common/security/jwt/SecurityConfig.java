package br.com.gomining.schoolsimulator.common.security.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.GET;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic()
                .and()
                .authorizeHttpRequests()
                .antMatchers(GET, "/students/**").permitAll()
                .antMatchers(GET, "/activities/**").permitAll()
                .antMatchers(GET, "/grades/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable();
        return http.build();
    }
}

