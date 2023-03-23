package com.octopus.authservice.security.config;

import com.octopus.authservice.mapper.UserMapper;
import com.octopus.authservice.security.UserDetailsServiceImpl;
import com.octopus.authservice.service.UserService;
import com.octopus.authutils.jwt.JWTConfig;
import com.octopus.authutils.jwt.JWTFilter;
import com.octopus.authutils.jwt.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String[] SWAGGER_AUTH_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };
    private final UserDetailsServiceImpl userDetailService;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedMethods("*")
                        .allowedOrigins("*").allowedHeaders("*");
            }
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors()
        .and()
            .csrf()
            .disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .httpBasic()
            .disable()
            .authorizeRequests()
            .antMatchers("/api/auth/**").permitAll()
            .mvcMatchers(HttpMethod.GET, "/").permitAll()
            .antMatchers(SWAGGER_AUTH_WHITELIST).permitAll()
            .anyRequest().authenticated()
        .and()
             .exceptionHandling()
             .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        .and()
             .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    public JWTFilter jwtFilter() {
        return new JWTFilter(jwtUtils());
    }

    @Bean
    public JWTUtils jwtUtils() {
        return new JWTUtils(jwtConfig());
    }

    @Bean
    public JWTConfig jwtConfig() {
        return new JWTConfig();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }
}
