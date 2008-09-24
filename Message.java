public class Message {
  private String message;
  private Person fromPerson;
  private String msgId;

  public Message(String msg, Person p, String id) {
    message = msg;
    fromPerson = p;
    msgId = id;
  }

  public String getMessage() {
    return message;
  }

  public Person getFrom() {
    return fromPerson;
  }

  public String getId() {
    return msgId;
  }
}


