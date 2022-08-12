

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @Author Anthony Z.
 * @Date 11/8/2022
 * @Description:
 */
public class Test1 {

    final static String HOST = "";
    final static int PORT = 1500;

    static String[] requests = new String[]{
             "log.txt"
    };
    
    final static String CRLF = "\r\n";
    
    
    public static void main(String[] args) {
        for(int i=0; i<2; i++){
            new Thread(() -> {
                Socket socket = null;
                try{
                    socket = new Socket(HOST, PORT);
                    OutputStream out = socket.getOutputStream();
                    Writer writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                    InputStream in = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

                    for(String request:requests){
                        process(request, writer, reader);
                    }
                    
                    writer.flush();
                    
                }catch (UnknownHostException e){
                    
                    System.out.println("Cannot connected to socket " + HOST
                            + " on port " + 1500);
                }catch (IOException e){
                    System.err.println(e);
                }finally {
                    
                    try{
                        socket.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }

    private static void process(String str, Writer writer, BufferedReader reader) throws IOException {
        writer.write("GET /" + str + " HTTP/1.1" + CRLF);
        writer.write("Host: " + HOST + ":" + PORT + CRLF);
        writer.write("Connection: keep-alive" + CRLF);
        writer.write("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/98.0.4758.82 Safari/537.36" + CRLF);
        writer.write(CRLF);
        writer.flush();

        System.out.println("Write operation finishes");

        for(String line = reader.readLine(); line != null; line = reader.readLine()){
            System.out.println(line);
        }

    }
}
