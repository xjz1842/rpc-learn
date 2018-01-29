package com.nio.demo.BIO.socket;

import java.io.*;
import java.net.Socket;

public class EchoClient {

    private static String hostName = "127.0.0.1";
    private static int port = 18081;

    public static void main(String[] args) throws Exception {
        Socket socket = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        try {
            socket = new Socket(hostName, port);

            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bufferedWriter.write("Hello,java Blocking IO.\n");
            bufferedWriter.flush();

            String echo = bufferedReader.readLine();
            System.out.println("echo:" + echo);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }

    }
}
