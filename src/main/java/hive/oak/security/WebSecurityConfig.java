package hive.oak.security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  private final UserDetailsService userDetailsService;
  private final BCryptPasswordEncoder passwordEncoder;
  private final JwtConfig jwtConfig;

  public WebSecurityConfig(
      final UserDetailsServiceImpl userDetailsService,
      final BCryptPasswordEncoder passwordEncoder,
      final JwtConfig jwtConfig
  ) {
    this.userDetailsService = userDetailsService;
    this.passwordEncoder = passwordEncoder;
    this.jwtConfig = jwtConfig;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
  }

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    final var jwtUsernamePasswordAuthenticationFilter =
        new JwtUsernamePasswordAuthenticationFilter(jwtConfig);
    jwtUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager());

    http
        .cors()
        .and()
        .csrf()
        .disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .exceptionHandling()
        .authenticationEntryPoint((req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
        .and()
        .addFilterBefore(
            jwtUsernamePasswordAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class
        )
        .addFilterAfter(
            new JwtTokenAuthenticationFilter(jwtConfig),
            UsernamePasswordAuthenticationFilter.class
        )
        .authorizeRequests()
        .anyRequest().permitAll();
  }
}
