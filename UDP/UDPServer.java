/*
 * UDPServer.java
 *
 * Version 2.1
 * Vorlesung Rechnernetze HAW Hamburg
 * Autor: M. Hübner (nach Kurose/Ross)
 * Zweck: UDP-Server Beispielcode:
 *        Warte auf UDP-Pakete. Nach Empfang eines Pakets als String auspacken,
 *        in Großbuchstaben konvertieren und zurücksenden
 *        Bei Empfang von SHUTDOWN beenden.
 */
import java.io.*;
import java.net.*;

public class UDPServer {
  public final int SERVER_PORT = 9876;
  public final int BUFFER_SIZE = 1024; 
  public final String CHARSET = "IBM-850";
  
  // UDP-Socketklasse  
  private DatagramSocket serverSocket;   

  private InetAddress receivedIPAddress; // IP-Adresse des Clients
  private int receivedPort; // Port auf dem Client
  private boolean serviceRequested = true; // Anzeige, ob der Server-Dienst weiterhin benoetigt wird 

  public void startService() {
    String capitalizedSentence;

    try {
      /* UDP-Socket erzeugen (kein Verbindungsaufbau!)
       * Socket wird an den ServerPort gebunden */
      serverSocket = new DatagramSocket(SERVER_PORT);
      System.err.println("UDP Server: Waiting for connection - listening UDP port " + SERVER_PORT);

      while (serviceRequested) {

        /* String vom Client empfangen und in Großbuchstaben umwandeln */
        capitalizedSentence = readFromClient().toUpperCase();

        /* Modifizierten String an Client senden */
        writeToClient(capitalizedSentence);
		  	
        /* Test, ob Server beendet werden soll */
        if (capitalizedSentence.startsWith("SHUTDOWN")) {
          serviceRequested = false;
        }
      }

      /* Socket schließen (freigeben) */
      serverSocket.close();
      System.err.println("Server shut down!");
    } catch (SocketException e) {
      System.err.println("Connection aborted by client!");
    } catch (IOException e) {
      System.err.println("Connection aborted by client!");
    }

    System.err.println("UDP Server stopped!");
  }

  private String readFromClient() throws IOException {
    /* Liefere den nächsten String vom Server */
    String receiveString = "";

    /* Paket für den Empfang erzeugen */
    byte[] receiveData = new byte[BUFFER_SIZE];
    DatagramPacket receivePacket = new DatagramPacket(receiveData, BUFFER_SIZE);

    /* Warte auf Empfang eines Pakets auf dem eigenen Server-Port */
    serverSocket.receive(receivePacket);

    /* Paket erhalten --> auspacken und analysieren */
    receiveString = new String(receivePacket.getData(), 0,
                               receivePacket.getLength(), CHARSET);
    receivedIPAddress = receivePacket.getAddress();
    receivedPort = receivePacket.getPort();

    System.err.println("UDP Server got from Client (Source IP-address: " + receivedIPAddress +
                        " Source port: " + receivedPort + "): " + receiveString);

    return receiveString;
  }

  private void writeToClient(String sendString) throws IOException {
    /* Sende den String als UDP-Paket zum Client */

    /* String in Byte-Array umwandeln */
    byte[] sendData = sendString.getBytes(CHARSET);

    /* Antwort-Paket erzeugen */
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                                                   receivedIPAddress,
                                                   receivedPort);
    /* Senden des Pakets */
    serverSocket.send(sendPacket);

    System.err.println("UDP Server has sent the message: " + sendString);
  }

  public static void main(String[] args) {
    UDPServer myServer = new UDPServer();
    myServer.startService();
  }
}
