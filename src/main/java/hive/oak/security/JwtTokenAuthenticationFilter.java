package hive.oak.security;

import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {
  private final JwtConfig jwtConfig;

  JwtTokenAuthenticationFilter(JwtConfig jwtConfig) {
    this.jwtConfig = jwtConfig;
  }

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain
  ) throws ServletException, IOException {
    final var header = request.getHeader(jwtConfig.getHeader());

    if (header == null || !header.startsWith(jwtConfig.getPrefix())) {
      filterChain.doFilter(request, response);
      return;
    }

    final var token = header.replace(jwtConfig.getPrefix(), "");
    try {
      final var claims = Jwts.parser()
          .setSigningKey(jwtConfig.getSecret().getBytes())
          .parseClaimsJws(token)
          .getBody();

      final var subject = claims.getSubject();

      if (subject != null) {
        @SuppressWarnings("unchecked") final var authorities =
            (List<String>) claims.get("authorities");

        var auth = new UsernamePasswordAuthenticationToken(
            subject,
            null,
            authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
      }
    } catch (RuntimeException e) {
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }
}
