package hive.oak.user;

import java.util.UUID;

public class UserJson {
  private UUID id;
  private String username;
  private String name;
  private String lastName;
  private String email;
  private String cpf;
  private String birthDate;
  private String college;

  public UserJson() {
  }

  public UserJson(
      final UUID id,
      final String username,
      final String name,
      final String lastName,
      final String email,
      final String cpf,
      final String birthDate,
      final String college
  ) {
    this.id = id;
    this.username = username;
    this.name = name;
    this.lastName = lastName;
    this.email = email;
    this.cpf = cpf;
    this.birthDate = birthDate;
    this.college = college;
  }

  public UUID getId() {
    return id;
  }

  public void setId(final UUID id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(final String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public String getCpf() {
    return cpf;
  }

  public void setCpf(final String cpf) {
    this.cpf = cpf;
  }

  public String getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(final String birthDate) {
    this.birthDate = birthDate;
  }

  public String getCollege() {
    return college;
  }

  public void setCollege(final String college) {
    this.college = college;
  }
}
