public class Room {

  private String roomName;
  private String roomURL;

  public Room(String roomName, String roomURL) {
    this.roomName = roomName;
    this.roomURL = roomURL;
  }

  public String getName() {
    return this.roomName;
  }

  public String getUrl() {
    return this.roomURL;
  }
}

