package com.tartan.marvel.HelperService.core;

import com.tartan.marvel.HelperService.config.HttpConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

public class HttpConnectionWorkerThread extends Thread {

    private Socket socket;

    public HttpConnectionWorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            int _byte;

            while ((_byte = inputStream.read()) >= 0) {
                System.out.print((char) _byte);
            }

            String html = "<html><head><title>Simple Java HTTP Server</title></head><body><h1>This page was served using my simple HTTP Server developed in Java</h1></body></html>";

            final String CRLF = "\n\r"; // 13, 10
            String response = "HTTP/1.1 200 OK" + CRLF + // Status line
                    "Content-Length: " + html.getBytes().length + CRLF + // Header
                    CRLF + html + CRLF + CRLF;

            outputStream.write(response.getBytes());
        } catch (IOException e) {
            throw new HttpConfigurationException("Error occurred", e);
        } finally {
            if (Objects.nonNull(inputStream)) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new HttpConfigurationException(e);
                }
            }
            if (Objects.nonNull(outputStream))
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new HttpConfigurationException(e);
                }
            if (Objects.nonNull(socket))
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new HttpConfigurationException(e);
                }
        }
    }
}
