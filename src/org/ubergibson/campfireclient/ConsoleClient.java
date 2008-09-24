package org.ubergibson.campfireclient;

import gnu.getopt.Getopt;
import java.io.*;

public class ConsoleClient extends CampfireClient implements MessageHandler {

  private static String ccVersion = "0.1dev";

  public ConsoleClient(String user, String pass, String sub, boolean ssl) throws Exception {
    super(user, pass, sub, ssl);
    super.setMessageHandler(this);
  }

  public void handleMessage(Message newMsg) {
    try {
      int ASCII_ESCAPE = 27;
      System.out.printf("%c[34;47m%s%c[0m: %s\n", ASCII_ESCAPE, newMsg.getFrom().getName(), ASCII_ESCAPE, newMsg.getMessage());
      //System.out.println(newMsg.getFrom().getName()+": "+newMsg.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
    }
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
    boolean useSSL = false;

    // parse command line parameters
    Getopt g = new Getopt("ConsoleClient", args, "u:p:s:hS");
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
        case 'S':
          useSSL = true;
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

    //clear screen
    System.out.printf("%c[2J", 27);
    System.out.printf("%c[31;40mConsoleClient %s%c[0m\n", 27, ccVersion, 27);

    if(useSSL) {
      System.out.println("> Using SSL.");
    } else {
      System.out.println("> Not using SSL.");
    }

    System.out.println("> Connecting...");
    ConsoleClient cc = new ConsoleClient(user, pass, subdomain, useSSL);

    System.out.println("> Now connected to \""+subdomain+".campfirenow.com\"");
    showCommands();
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String curLine = "";
    while((curLine = br.readLine()) != null) {
      if(curLine.startsWith("/quit")) {
        break;
      } else if(curLine.startsWith("/join")) {
        System.out.println("> Joining room...");
        cc.joinRoom(curLine.substring(6));
        System.out.println("> Now chatting in \""+curLine.substring(6)+"\"");
      } else if(curLine.startsWith("/")) {
        showCommands();
      } else {
        cc.sendMessage(curLine);
      }
    }

    cc.leaveCurrentRoom();
    cc.logOut();
  } 

  private static void showCommands() {
    System.out.println("Available commands: /join <room>, /quit");
  }
}
