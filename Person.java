public class Person {
  private String name;
  private int userId;

  public Person(String n, int id) {
    name = n;
    userId = id;
  }

  public String getName() {
    return name;
  }

  public int getId() {
    return userId;
  }
}
