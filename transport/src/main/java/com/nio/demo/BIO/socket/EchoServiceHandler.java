package com.nio.demo.BIO.socket;

import java.io.*;
import java.net.Socket;

public class EchoServiceHandler implements Runnable {

    private Socket socket = null;

    public EchoServiceHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        BufferedReader bufferedInputReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            bufferedInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            while (true) {
                String line = bufferedInputReader.readLine();
                if (line == null) {
                    break;
                }
                bufferedWriter.write(line + "\n");
                bufferedWriter.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedInputReader != null) {
                try {
                    bufferedInputReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (this.socket != null) {
                try {
                    this.socket.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }
}
