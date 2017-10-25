package proxy;

import java.net.*;
import java.io.*;

public class ProxyD {
    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket = null;
        boolean listening = true;
        //default port
        int port = 5026;
        try{
            if (args[0].equals("-port") {
                port = Integer.parseInt(args[1]);
            }
        } catch (Exception e) {
        }
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Started on: " + port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + args[0]);
            System.exit(-1);
        }

        while (listening) {
            new ProxyThread(serverSocket.accept()).start();
        }
        serverSocket.close();
    }

}