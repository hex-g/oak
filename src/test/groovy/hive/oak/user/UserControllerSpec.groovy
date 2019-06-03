package hive.oak.user

import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import java.text.SimpleDateFormat

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserControllerSpec extends Specification {
    UserRepository userRepository
    BCryptPasswordEncoder passwordEncoder
    RestTemplate restTemplate
    MockMvc mockMvc

    def setup() {
        userRepository = Stub()
        passwordEncoder = Stub()
        restTemplate = Stub()

        final def controller = new UserController(userRepository, passwordEncoder, restTemplate)

        ReflectionTestUtils.setField(controller, "hiveApiUrl", "url")

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    def "Given user with provided ID does not exist when user is retrieved, then 404 is received"() {
        given:
        final def id = UUID.randomUUID()
        userRepository.findById(id) >> Optional.empty()

        expect:
        mockMvc
                .perform(get("/user/" + id))
                .andExpect(status().isNotFound())
    }

    def "Given user with provided ID exists when user is retrieved, then 200 is received and JSON is correct"() {
        given:
        final def id = UUID.randomUUID()
        final def birthDate = new Date()
        final def user = new User(
                id,
                "username",
                "password",
                "email",
                new Name("first", "last"),
                new Cpf("cpf"),
                birthDate,
                "college"
        )
        userRepository.findById(id) >> Optional.of(user)

        final def json = "{" +
                "\"id\":\"" + id + "\"," +
                "\"username\":\"username\"," +
                "\"name\":\"first\"," +
                "\"lastName\":\"last\"," +
                "\"email\":\"email\"," +
                "\"cpf\":\"cpf\"," +
                "\"birthDate\":\"" + new SimpleDateFormat("dd/MM/yyyy").format(birthDate) + "\"," +
                "\"college\":\"college\"" +
                "}"

        expect:
        mockMvc
                .perform(get("/user/" + id))
                .andExpect(status().isOk())
                .andExpect(content().json(json))
    }

    def "Given provided username is already in use when user is saved, then 500 is received"() {
        given:
        final def id = UUID.randomUUID()
        final def birthDate = new Date()
        userRepository.existsByUsername("username") >> true

        final def json = "{" +
                "\"id\":\"" + id + "\"," +
                "\"username\":\"username\"," +
                "\"password\":\"password\"," +
                "\"name\":\"first\"," +
                "\"lastName\":\"last\"," +
                "\"email\":\"email\"," +
                "\"cpf\":\"cpf\"," +
                "\"birthDate\":\"" + new SimpleDateFormat("dd/MM/yyyy").format(birthDate) + "\"," +
                "\"college\":\"college\"" +
                "}"

        expect:
        mockMvc
                .perform(post("/user").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isInternalServerError())
                .andExpect(status().reason("Username already in use"))
    }

    def "Given provided email is already in use when user is saved, then 500 is received"() {
        given:
        final def id = UUID.randomUUID()
        final def birthDate = new Date()
        userRepository.existsByEmail("email") >> true

        final def json = "{" +
                "\"id\":\"" + id + "\"," +
                "\"username\":\"username\"," +
                "\"password\":\"password\"," +
                "\"name\":\"first\"," +
                "\"lastName\":\"last\"," +
                "\"email\":\"email\"," +
                "\"cpf\":\"cpf\"," +
                "\"birthDate\":\"" + new SimpleDateFormat("dd/MM/yyyy").format(birthDate) + "\"," +
                "\"college\":\"college\"" +
                "}"

        expect:
        mockMvc
                .perform(post("/user").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isInternalServerError())
                .andExpect(status().reason("Email already in use"))
    }

    def "Given provided CPF is already in use when user is saved, then 500 is received"() {
        given:
        final def id = UUID.randomUUID()
        final def birthDate = new Date()
        userRepository.existsByCpf(new Cpf("cpf")) >> true

        final def json = "{" +
                "\"id\":\"" + id + "\"," +
                "\"username\":\"username\"," +
                "\"password\":\"password\"," +
                "\"name\":\"first\"," +
                "\"lastName\":\"last\"," +
                "\"email\":\"email\"," +
                "\"cpf\":\"cpf\"," +
                "\"birthDate\":\"" + new SimpleDateFormat("dd/MM/yyyy").format(birthDate) + "\"," +
                "\"college\":\"college\"" +
                "}"

        expect:
        mockMvc
                .perform(post("/user").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isInternalServerError())
                .andExpect(status().reason("CPF already in use"))
    }

    def "Given user data is valid when user is saved, then 200 is received with user ID"() {
        given:
        final def id = UUID.randomUUID()
        final def birthDate = new Date()
        final def json = "{" +
                "\"id\":\"" + id + "\"," +
                "\"username\":\"username\"," +
                "\"password\":\"password\"," +
                "\"name\":\"first\"," +
                "\"lastName\":\"last\"," +
                "\"email\":\"email\"," +
                "\"cpf\":\"cpf\"," +
                "\"birthDate\":\"" + new SimpleDateFormat("dd/MM/yyyy").format(birthDate) + "\"," +
                "\"college\":\"college\"" +
                "}"
        final def hiveUser = new HiveUser()
        hiveUser.setId(UUID.randomUUID())
        final def user = new User(
                id,
                "username",
                "password",
                "email",
                new Name("first", "last"),
                new Cpf("cpf"),
                birthDate,
                "college"
        )
        user.setHiveUser(hiveUser.getId())
        restTemplate.postForObject(_ as String, _ as Object, HiveUser.class) >> hiveUser
        userRepository.save(_ as User) >> user

        userRepository.save(_ as User) >> {args -> assert args[0].getHiveUser().equals(hiveUser.getId())}

        expect:
        mockMvc
                .perform(post("/user").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("\"" + id + "\""))
    }

    def "Given user with provided ID does not exist when user is deleted, then 404 is received"() {
        given:
        final def id = UUID.randomUUID()
        userRepository.findById(id) >> Optional.empty()

        expect:
        mockMvc
                .perform(delete("/user/" + id))
                .andExpect(status().isNotFound())
    }

    def "Given user with provided ID exists when user is deleted, then 200 is received"() {
        given:
        final def id = UUID.randomUUID()
        final def birthDate = new Date()
        final def user = new User(
                id,
                "username",
                "password",
                "email",
                new Name("first", "last"),
                new Cpf("cpf"),
                birthDate,
                "college"
        )
        userRepository.findById(id) >> Optional.of(user)

        expect:
        mockMvc
                .perform(delete("/user/" + id))
                .andExpect(status().isOk())
    }
}
