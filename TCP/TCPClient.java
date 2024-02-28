/*
 * TCPClient.java
 *
 * Version 3.1
 * Autor: M. Huebner HAW Hamburg (nach Kurose/Ross)
 * Zweck: TCP-Client Beispielcode:
 *        TCP-Verbindung zum Server aufbauen, einen vom Benutzer eingegebenen
 *        String senden, den String in Grossbuchstaben empfangen und ausgeben
 */

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TCPClient {
    /* Protokoll-Codierung des Zeilenendes: CRLF */
    private final String CRLF = "\r\n" ;
        
    /* Portnummer */
    private int serverPort;

    /* Hostname */
    private String hostname;

    /* TCP-Standard-Socketklasse */
    private Socket clientSocket; 

    /* Ausgabestream zum Server */
    private DataOutputStream outToServer;
    
    /* Eingabestream vom Server
       Wenn Binärdaten verarbeitet werden müssen, kann auch DataInputStream verwendet werden */
    private BufferedReader inFromServer; 

    private boolean serviceRequested = true; // Client beenden?

    public TCPClient(String hostname, int serverPort) {
        this.serverPort = serverPort;
        this.hostname = hostname;
    }

    public void startJob() {
        /* Client starten. Ende, wenn quit eingegeben wurde */
        Scanner inFromUser;
        String sentence; // vom User uebergebener String
        String modifiedSentence; // vom Server modifizierter String

        try {
            /* Socket erzeugen --> Verbindungsaufbau mit dem Server */
            clientSocket = new Socket(hostname, serverPort);

            /* Socket-Basisstreams durch spezielle Streams filtern */
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            inFromServer = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));

            /* Konsolenstream (Standardeingabe) initialisieren */
            inFromUser = new Scanner(System.in);

            while (serviceRequested) {
                System.err.println("ENTER TCP-DATA: ");
                /* String vom Benutzer (Konsoleneingabe) holen */
                sentence = inFromUser.nextLine();

                /* String an den Server senden */
                writeToServer(sentence);

                /* Modifizierten String vom Server empfangen */
                modifiedSentence = readFromServer();

                /* Test, ob Client beendet werden soll */
                if (modifiedSentence.startsWith("QUIT")) {
                    serviceRequested = false;
                }
            }

            /* Socket-Streams schliessen --> Verbindungsabbau */
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Connection aborted by server!");
        }
        System.err.println("TCP Client stopped!");
    }

    private void writeToServer(String line) throws IOException {
        /* Sende eine Anfrage-Zeile zum Server 
         * ALLE Anfragen an den Server müssen über diese Methode gesendet werden ("Sub-Layer") */
        outToServer.write((line + CRLF).getBytes());
        System.err.println("TCP Client has sent the message: " + line);
    }

    private String readFromServer() throws IOException {
        /* Lies eine Antwort (reply) vom Server 
         * ALLE Antworten vom Server müssen über diese Methode empfangen werden ("Sub-Layer") */
        String reply = inFromServer.readLine();
        System.err.println("TCP Client got from Server: " + reply);
        return reply;
    }

    public static void main(String[] args) {
        /* Test: Erzeuge Client und starte ihn. */
        TCPClient myClient = new TCPClient("localhost", 60000);  // Loopback (localhost) bei IPv6: "::1"
        myClient.startJob();
    }
}
