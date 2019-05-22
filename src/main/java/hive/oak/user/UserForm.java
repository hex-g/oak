package hive.oak.user;

import java.util.UUID;

public class UserForm {
  private UUID id;
  private String username;
  private String password;
  private String name;
  private String lastName;
  private String email;
  private String cpf;
  private String birthDate;
  private String college;

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

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
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
