package hive.oak.user;

import javax.persistence.Embeddable;

@Embeddable
public class Cpf {
  private String value;

  public Cpf() {
  }

  public Cpf(final String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(final String value) {
    this.value = value;
  }
}
