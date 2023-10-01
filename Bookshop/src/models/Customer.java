package models;

public class Customer {
  private String firstname, lastname, phonenumer, email;

  public Customer(String firstname, String lastname, String phonenumer, String email) {
    this.firstname = firstname;
    this.lastname = lastname;
    this.phonenumer = phonenumer;
    this.email = email;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public String getPhonenumer() {
    return phonenumer;
  }

  public void setPhonenumer(String phonenumer) {
    this.phonenumer = phonenumer;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

}
