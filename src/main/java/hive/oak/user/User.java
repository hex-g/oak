package hive.oak.user;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
public class User {
  @Id
  @GeneratedValue
  private UUID id;
  @Column(unique = true)
  private String username;
  private String password;
  @Column(unique = true)
  private String email;
  @Embedded
  private Name name;
  @Embedded
  private Cpf cpf;
  private Date birthDate;
  private String college;
  private Integer hiveUser;

  public User() {
  }

  public User(
      final UUID id,
      final String username,
      final String password,
      final String email,
      final Name name,
      final Cpf cpf,
      final Date birthDate,
      final String college
  ) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.email = email;
    this.name = name;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public Name getName() {
    return name;
  }

  public void setName(final Name name) {
    this.name = name;
  }

  public Cpf getCpf() {
    return cpf;
  }

  public void setCpf(final Cpf cpf) {
    this.cpf = cpf;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(final Date birthDate) {
    this.birthDate = birthDate;
  }

  public String getCollege() {
    return college;
  }

  public void setCollege(final String college) {
    this.college = college;
  }

  public Integer getHiveUser() {
    return hiveUser;
  }

  public void setHiveUser(final Integer hiveUser) {
    this.hiveUser = hiveUser;
  }
}
