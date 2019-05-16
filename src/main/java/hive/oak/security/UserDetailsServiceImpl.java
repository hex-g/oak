package hive.oak.security;

import hive.oak.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  @Autowired
  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    final var user = userRepository.findByUsername(username);

    if (user == null) {
      throw new UsernameNotFoundException("Username: " + username + " not found");
    }

    return new User(
        user.getUsername(),
        user.getPassword(),
        AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_TEST")
    );
  }
}
