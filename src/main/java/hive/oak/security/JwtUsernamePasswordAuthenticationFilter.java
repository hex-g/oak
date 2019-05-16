package hive.oak.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

public class JwtUsernamePasswordAuthenticationFilter
    extends AbstractAuthenticationProcessingFilter {
  private final JwtConfig jwtConfig;

  JwtUsernamePasswordAuthenticationFilter(final JwtConfig jwtConfig) {
    super(new AntPathRequestMatcher(
        jwtConfig.getUri(),
        "POST"
    ));

    this.jwtConfig = jwtConfig;
  }

  @Override
  public Authentication attemptAuthentication(
      final HttpServletRequest request, final HttpServletResponse response
  ) throws AuthenticationException {
    try {
      final var credentials =
          new ObjectMapper().readValue(request.getInputStream(), UserCredentialsMapping.class);

      return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
          credentials.username,
          credentials.password,
          Collections.emptyList()
      ));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void successfulAuthentication(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain chain,
      final Authentication authResult
  ) {
    final long now = System.currentTimeMillis();
    final var token = Jwts.builder()
        .setSubject(authResult.getName())
        .claim("authorities", authResult.getAuthorities().stream().map(
            GrantedAuthority::getAuthority).collect(Collectors.toList())
        )
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + jwtConfig.getExpiration() * 1000))
        .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes())
        .compact();
    response.addHeader(jwtConfig.getHeader(), jwtConfig.getPrefix() + token);
  }
}
