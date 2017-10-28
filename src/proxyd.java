
import java.net.*;
import java.io.*;

public class proxyd {
    public static void main(String[] args) throws IOException{
        int localport = 5026;
        int remoteport = 8080;
        String host = "localhost";
        try{

             host = args[0];
             remoteport = Integer.parseInt(args[1]);
             localport = Integer.parseInt(args[2]);
            System.out.println("Starting proxy for " + host + ":" + remoteport
            + " on port " + localport);
    
            runProxyServer(host, remoteport, localport);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void runProxyServer(String host, int localport, int remoteport) throws IOException{
        ServerSocket lServerSocket = new ServerSocket(localport);
        final byte[] request = new byte[4096];
        byte[] reply = new byte[4096];
        while (true){
            System.out.println("working while");
            Socket client = null, server = null;
             try{
                 client = lServerSocket.accept();
                 final InputStream streamfrclient = client.getInputStream();
                 final OutputStream streamtoclient = client.getOutputStream();
                 try{
                     server = new Socket(host, remoteport);
                 }
                 catch (Exception e){
                    System.out.println("notworking");
                    PrintWriter out = new PrintWriter(streamtoclient);
                    out.print("Proxy server cannot connect to " + host + ":"
                    + remoteport + ":\n" + e + "\n");
                    out.flush();
                    client.close();
                    
                    continue;
                 }

                 final InputStream streamfrserver = server.getInputStream();
                 final OutputStream streamtoserver = server.getOutputStream();

                 Thread serverThread = new Thread(){
                     public void run(){
                         int bytesread;
                         try {
                             while ((bytesread = streamfrclient.read(request)) != -1) {
                                 streamtoserver.write(request, 0, bytesread);
                                 System.out.println(bytesread + "to server" + new String(request, "UTF-8"));
                                 streamtoserver.close();
                                 System.out.println("working");
                             }
                         } catch (Exception e) {
                            System.out.println("notworking");
                                                  }
                         try {
                             streamtoserver.close();
                         } catch (Exception e) {
                            System.out.println("notworking");
                                                  }
                     }
                 };
                 serverThread.start();

                 int bytesread;
                 try {  
                     while((bytesread = streamfrserver.read(reply)) != -1){
                        try {
                            serverThread.sleep(1);
                            System.out.println(bytesread + "client"+ new String(request, "UTF-8"));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                         streamtoclient.write(reply, 0, bytesread);
                         streamtoclient.flush();
                     }
                 } catch (IOException e) {
                 }
                 streamtoclient.close();
             }
             catch(Exception e){
                System.err.println(e);                
             }
             finally{
                 try {
                     if (server != null) 
                         server.close();
                     if (client != null)
                         client.close();
                 } catch (Exception e) {
                 }
             }
        }
    }
}
