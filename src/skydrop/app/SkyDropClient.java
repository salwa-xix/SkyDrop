package skydrop.app;

import java.io.*;
import java.net.Socket;

public class SkyDropClient {

    private static final String HOST = "localhost";
    private static final int PORT = 8189;

    // Send a request to the server and return the response
    public static String sendRequest(String request) {

        try (

                // Open a socket connection to the server
                Socket socket = new Socket(HOST, PORT);

                // Send text requests to the server
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // Read the server response
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())))
        {

            // Send the request using the shared protocol format
            out.println(request);

            // Return the server response to the GUI
            return in.readLine();

        } catch (IOException e) {

            System.out.println("Client error: " + e.getMessage());

            return null;
        }
    }
}