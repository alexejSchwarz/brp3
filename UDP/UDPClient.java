/*
 * UDPClient.java
 *
 * Version 2.1
 * Vorlesung Rechnernetze HAW Hamburg
 * Autor: M. Huebner (nach Kurose/Ross)
 * Zweck: UDP-Client Beispielcode:
 *        UDP-Socket erzeugen, einen vom Benutzer eingegebenen
 *        String in ein UDP-Paket einpacken und an den UDP-Server senden,
 *        den String in Grossbuchstaben empfangen und ausgeben
 *        Nach QUIT beenden, bei SHUTDOWN den Serverthread beenden
 */
import java.io.*;

import java.net.*;

import java.util.Scanner;


public class UDPClient {
  public final int SERVER_PORT = 9876;
  public final String HOSTNAME = "localhost";
  public final int BUFFER_SIZE = 1024; 
  public final String CHARSET = "IBM-850"; // "UTF-8"
  
  // UDP-Socketklasse  
  private DatagramSocket clientSocket; 

  private boolean serviceRequested = true;
  public InetAddress SERVER_IP_ADDRESS;

  /* Client starten. Ende, wenn quit eingegeben wurde */
  public void startJob() {
    Scanner inFromUser;

    String sentence;
    String modifiedSentence;

    try {
      /* IP-Adresse des Servers ermitteln --> DNS-Client-Aufruf! */
      SERVER_IP_ADDRESS = InetAddress.getByName(HOSTNAME);

      /* UDP-Socket erzeugen (kein Verbindungsaufbau!)
       * Socket wird an irgendeinen freien (Quell-)Port gebunden, da kein Port angegeben */
      clientSocket = new DatagramSocket();

      /* Konsolenstream (Standardeingabe) initialisieren */
      inFromUser = new Scanner(System.in, CHARSET);

      while (serviceRequested) {
        System.err.println("ENTER UDP-DATA: ");
        /* String vom Benutzer (Konsoleneingabe) holen */
        sentence = inFromUser.nextLine();

        /* Test, ob Client beendet werden soll */
        if (sentence.startsWith("quit")) {
          serviceRequested = false;
        } else {

          /* Sende den String als UDP-Paket zum Server */
          writeToServer(sentence);

          /* Modifizierten String vom Server empfangen */
          modifiedSentence = readFromServer();
        }
      }

      /* Socket schliessen (freigeben)*/
      clientSocket.close();
    } catch (IOException e) {
      System.err.println("Connection aborted by server!");
    }

    System.err.println("UDP Client stopped!");
  }

  private void writeToServer(String sendString) throws IOException {
    /* Sende den String als UDP-Paket zum Server */

    /* String in Byte-Array umwandeln */
    byte[] sendData = sendString.getBytes(CHARSET);

    /* Paket erzeugen mit Server-IP und Server-Zielport */
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                                                   SERVER_IP_ADDRESS, SERVER_PORT);
    /* Senden des Pakets */
    clientSocket.send(sendPacket);

    System.err.println("UDP Client has sent the message: " + sendString);
  }

  private String readFromServer() throws IOException {
    /* Liefere den naechsten String vom Server */
    String receiveString = "";

    /* Paket fuer den Empfang erzeugen */
    byte[] receiveData = new byte[BUFFER_SIZE];
    DatagramPacket receivePacket = new DatagramPacket(receiveData, BUFFER_SIZE);

    /* Warte auf Empfang des Antwort-Pakets auf dem eigenen (Quell-)Port, 
     * den der Server aus dem Nachrichten-Paket ermittelt hat */
    clientSocket.receive(receivePacket);

    /* Paket wurde empfangen --> auspacken und Inhalt anzeigen */
    receiveString = new String(receivePacket.getData(), 0,
                               receivePacket.getLength(), CHARSET);

    System.err.println("UDP Client got from Server: " + receiveString);

    return receiveString;
  }

  public static void main(String[] args) {
    UDPClient myClient = new UDPClient();
    myClient.startJob();
  }
}
