package skydrop.app;

import java.io.*;
import java.net.Socket;

public class SkyDropClient {

    private static final String HOST = "localhost";
    private static final int PORT = 8189;

    public static String sendRequest(String request) {

        try (
                Socket socket = new Socket(HOST, PORT);

                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(),
                        true
                );

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                )
        ) {

            out.println(request);

            return in.readLine();

        } catch (IOException e) {

            System.out.println("Client error: " + e.getMessage());
            return null;
        }
    }
}