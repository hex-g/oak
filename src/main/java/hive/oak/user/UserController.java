package hive.oak.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final RestTemplate restTemplate;
  @Value("${hive.oak.hive-api-url}")
  private String hiveApiUrl;

  @Autowired
  public UserController(
      final UserRepository userRepository,
      final BCryptPasswordEncoder passwordEncoder,
      final RestTemplate restTemplate
  ) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.restTemplate = restTemplate;
  }

  @GetMapping
  public Iterable<User> getAll() {
    return userRepository.findAll();
  }

  @GetMapping("/{id}")
  public UserJson getUser(@PathVariable final UUID id) {
    final var user = userRepository.findById(id);

    if (!user.isPresent()) {
      throw new UserNotFoundException();
    }

    final var u = user.get();

    return new UserJson(
        u.getId(),
        u.getUsername(),
        u.getName().getFirstName(),
        u.getName().getLastName(),
        u.getEmail(),
        u.getCpf().getValue(),
        new SimpleDateFormat("dd/MM/yyyy").format(u.getBirthDate()),
        u.getCollege()
    );
  }

  @PostMapping
  public UUID saveUser(@RequestBody UserForm form) {
    try {
      final var user = new User(
          form.getId(),
          form.getUsername(),
          passwordEncoder.encode(form.getPassword()),
          form.getEmail(),
          new Name(form.getName(), form.getLastName()),
          new Cpf(form.getCpf()),
          new SimpleDateFormat("dd/MM/yyyy").parse(form.getBirthDate()),
          form.getCollege()
      );

      if (userRepository.existsByUsername(user.getUsername())) {
        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Username already in use"
        );
      }
      if (userRepository.existsByEmail(user.getEmail())) {
        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Email already in use"
        );
      }
      if (userRepository.existsByCpf(user.getCpf())) {
        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "CPF already in use"
        );
      }

      System.err.println(hiveApiUrl);

      final var headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      final var map = new LinkedMultiValueMap<String, String>();
      map.add("username", form.getUsername());
      map.add("password", form.getPassword());
      final var request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
      final var hiveUser = restTemplate.postForObject(hiveApiUrl, request, HiveUser.class);

      user.setHiveUser(hiveUser.getId());

      return userRepository.save(user).getId();
    } catch (ParseException e) {
      throw new RuntimeException("Invalid birth date");
    }
  }

  @DeleteMapping("/{id}")
  public void deleteUser(@PathVariable final UUID id) {
    final var user = userRepository.findById(id);

    if (!user.isPresent()) {
      throw new UserNotFoundException();
    }

    final var u = user.get();

    restTemplate.delete(hiveApiUrl + "?id=" + u.getHiveUser());

    userRepository.deleteById(id);
    // userRepository.delete(u);
  }
}
