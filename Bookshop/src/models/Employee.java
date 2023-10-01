package models;

import java.util.Date;

public class Employee extends User {
  private int salary;
  private Date startdate;

  public Employee(String username, String password, String firstname, String lastname, int salary) {
    super(username, password, firstname, lastname);
    this.salary = salary;
    setStartdate(new Date());
    isAdmin = false;
  }

  public int getSalary() {
    return salary;
  }

  public Date getStartdate() {
    return startdate;
  }

  public void setStartdate(Date startdate) {
    this.startdate = startdate;
  }

  public void setSalary(int salary) {
    this.salary = salary;
  }
}
