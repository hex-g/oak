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

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final Cpf cpf = (Cpf) o;

    return value.equals(cpf.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }
}
