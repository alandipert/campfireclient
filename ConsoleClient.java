import gnu.getopt.Getopt;
import java.io.*;

public class ConsoleClient extends CampfireClient implements MessageHandler {

  private String lastMessage;

  public ConsoleClient(String user, String pass, String sub, boolean ssl) throws Exception {
    super(user, pass, sub, ssl);
    super.setMessageHandler(this);
    lastMessage = "";
  }

  public void handleMessage(Message newMsg) {
    System.out.println("Message: "+newMsg.getMessage());
    if(!newMsg.getMessage().matches(".*Robot.*")) {
      try {
        sendMessage("Somebody said "+newMsg.getMessage());
        lastMessage = newMsg.getMessage();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public String getLastMessage() {
    return lastMessage;
  }

  private static void usage() {
    System.out.println(
        "Usage: java ConsoleClient -u username -p password -s subdomain");
    System.exit(1);
  }

  public static void main(String [] args) throws Exception {

    String user;
    String pass;
    String subdomain;
    user = pass = subdomain = "";

    // parse command line parameters
    Getopt g = new Getopt("ConsoleClient", args, "u:p:s:h");
    int c;
    while ((c = g.getopt()) != -1) {
      switch (c) {
        case 'u':
          user = String.valueOf(g.getOptarg());
          break;
        case 'p':
          pass = String.valueOf(g.getOptarg());
          break;
        case 's':
          subdomain = String.valueOf(g.getOptarg());
          break;
        case 'h':
          // fall through
        default:
        case '?':
          usage();
          break;
      }
    }

    if(user == "" || pass == "" || subdomain == "") {
      System.out.println("Error: missing required arguments.");
      usage();
    }

    ConsoleClient cc = new ConsoleClient(user, pass, subdomain, false);
    cc.joinRoom("Bots");
    cc.sendMessage("sup");

    while(!cc.lastMessage.matches(".*go.*away.*robot.*")) {
      //chill in the room
    }

    cc.leaveCurrentRoom();
    cc.logOut();
  } 
}
