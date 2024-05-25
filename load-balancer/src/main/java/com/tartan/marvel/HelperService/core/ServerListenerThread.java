package com.tartan.marvel.HelperService.core;

import com.tartan.marvel.HelperService.config.HttpConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class ServerListenerThread extends Thread {
    private int port;
    private String webroot;
    private ServerSocket serverSocket;

    public ServerListenerThread(int port, String webroot) throws IOException {
        this.port = port;
        this.webroot = webroot;
        this.serverSocket = new ServerSocket(this.port);
    }

    @Override
    public void run() {
        try {
            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("Connection Accepted: " + socket.getInetAddress());

                HttpConnectionWorkerThread httpConnectionWorkerThread = new HttpConnectionWorkerThread(socket);
                httpConnectionWorkerThread.start();
            }
        } catch (IOException e) {
            throw new HttpConfigurationException("Exception occurred while creating connection :: ", e);
        } finally {
            try {
                if (Objects.nonNull(serverSocket))
                    serverSocket.close();
            } catch (IOException e) {
                throw new HttpConfigurationException("Error occurred", e);
            }
        }
    }
}
