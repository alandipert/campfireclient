import com.gargoylesoftware.htmlunit.WebClient; 
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.*;
import org.w3c.dom.*;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.io.*;

public class CampfireClient implements AlertHandler {

  private String loginUser;
  private String loginPass;
  private String campfireUrl;

  static final String CAMPFIRE_DOMAIN = "campfirenow.com";

  private WebClient webClient;
  private HtmlSubmitInput loginButton;
  private ArrayList<Room> roomList;

  private HtmlPage currentPage;
  private Room currentRoom;
  private MessageHandler messageHandler;
  
  public CampfireClient(String newUser, String newPass, String newUrl, boolean useSSL) throws Exception {

    loginUser = newUser;
    loginPass = newPass;

    messageHandler = null;

    if(useSSL) {
      campfireUrl = "http://"+newUrl+"."+CAMPFIRE_DOMAIN;
    } else {
      campfireUrl = "https://"+newUrl+"."+CAMPFIRE_DOMAIN;
    }

    try {
      webClient = new WebClient();
      webClient.setAlertHandler(this);
      currentPage = (HtmlPage)webClient.getPage(new URL(campfireUrl));
      if(!currentPage.getTitleText().matches(".*Login.*")) {
        System.out.println("Bad Campfire URL.");
        System.exit(1);
      }
    } catch (FailingHttpStatusCodeException httpe) {
        System.out.println("Bad Campfire URL.");
        System.exit(1);
    }
 
    if(!login()) {
      System.out.println("Error logging in");
      System.exit(1);
    }

    makeRoomList();

  }
  
  public void handleAlert(Page page, String message) {
    //check if the alert is a message,
    //parse it into a new Message and notify our handler
    if(messageHandler == null)
      return;

    Person newPerson = new Person("Somebody", 0); 
    Message retMsg = new Message(message, newPerson, "");  
    messageHandler.handleMessage(retMsg);
  }

  public void setMessageHandler(MessageHandler mHandler) {
    messageHandler = mHandler;
  }

  public void leaveCurrentRoom() throws Exception {
    NodeList links = currentPage.getElementsByTagName("a");
    for(int i = 0; i < links.getLength(); i++) {
      if(links.item(i).getTextContent().matches(".*Leave.*")) {
        HtmlAnchor logoutLink = (HtmlAnchor)links.item(i);
        System.out.println(logoutLink);
        logoutLink.click();
        break;
      }
    }
  }

  public void logOut() throws Exception {
    NodeList links = currentPage.getElementsByTagName("a");
    for(int i = 0; i < links.getLength(); i++) {
      if(links.item(i).getTextContent().matches(".*Logout.*")) {
        HtmlAnchor logoutLink = (HtmlAnchor)links.item(i);
        System.out.println(logoutLink);
        logoutLink.click();
        break;
      }
    }
  }

  public void sendMessage(String msg) throws Exception {
    HtmlForm sendForm = (HtmlForm)this.currentPage.getElementById("chat_form");
    HtmlTextArea messageInput = (HtmlTextArea)this.currentPage.getElementById("input");
    HtmlSubmitInput submitMsg = (HtmlSubmitInput)this.currentPage.getElementById("send");
    messageInput.setText(msg);
    currentPage = (HtmlPage)submitMsg.click();
    loadScript("hook.js");
  }

  private boolean login() throws Exception {
    HtmlForm loginForm = (HtmlForm)this.currentPage.getElementById("loginForm");
    HtmlTextInput emailInput = (HtmlTextInput)loginForm.getInputByName("email_address");
    HtmlPasswordInput passwordInput = (HtmlPasswordInput)loginForm.getInputByName("password");

    emailInput.setValueAttribute(loginUser);
    passwordInput.setValueAttribute(loginPass);

    List<HtmlInput> inputs  = loginForm.getHtmlElementsByAttribute("input", "type", "image");
    HtmlImageInput submitButton = (HtmlImageInput)inputs.get(0);

    this.currentPage = (HtmlPage)submitButton.click();

    if(currentPage.getTitleText().matches(".*Chat rooms.*")) {
      return true;
    } else {
      return false;
    }
  }

  public void joinRoom(String roomName) throws Exception {
    Room theRoom = getRoomByName(roomName);
    if(theRoom.getName() == "CampfireClient Error") {
      return;
    }
    currentRoom = theRoom;
    currentPage = (HtmlPage)webClient.getPage(new URL(currentRoom.getUrl()));
    loadScript("hook.js");
  }

  private Room getRoomByName(String roomName) {
    for(int i = 0; i < this.roomList.size(); i++) {
      if(this.roomList.get(i).getName().matches(".*"+roomName+".*")) {
        return this.roomList.get(i);
      }
    }

    return new Room("CampfireClient Error", "CampfireClient Error");
  }

  private void makeRoomList() {
    this.roomList = new ArrayList<Room>();
    HtmlElement roomDiv = (HtmlElement)this.currentPage.getElementById("rooms");
    NodeList links = roomDiv.getElementsByTagName("a");
    for(int i = 0; i < links.getLength(); i++) {
      NamedNodeMap nMap = links.item(i).getAttributes();
      String linkUrl = nMap.getNamedItem("href").getNodeValue();
      if(linkUrl.matches(".*room.*")) {
        Room newRoom = new Room(links.item(i).getTextContent(), linkUrl);
        this.roomList.add(newRoom);
      }
    }
  }

  private void getSubmitButton(HtmlForm loginForm) throws Exception {
    NodeList nList = loginForm.getElementsByTagName("input");
    for(int i = 0; i < nList.getLength(); i++) {
      if(nList.item(i).getUserData("alt") == "Sign in") {
        this.loginButton = (HtmlSubmitInput)nList.item(i);
      }
    }

    if(this.loginButton == null) {
      throw new Exception("Unable to find login button.");
    }
  }

  private void loadScript(String filePath) throws IOException {
    StringBuffer fileData = new StringBuffer(1000);
    BufferedReader reader = new BufferedReader(
    new FileReader(filePath));
    char[] buf = new char[1024];
    int numRead=0;

    while((numRead=reader.read(buf)) != -1){
      String readData = String.valueOf(buf, 0, numRead);
      fileData.append(readData);
      buf = new char[1024];
    }

    reader.close();
    currentPage.executeJavaScript(fileData.toString());
  }
}
