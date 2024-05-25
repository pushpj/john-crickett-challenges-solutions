package com.tartan.marvel.HelperService;

import com.tartan.marvel.HelperService.config.Configuration;
import com.tartan.marvel.HelperService.config.ConfigurationManager;
import com.tartan.marvel.HelperService.config.HttpConfigurationException;
import com.tartan.marvel.HelperService.core.ServerListenerThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        ConfigurationManager.getInstance()
                .loadConfigurationFile(
                        "/Users/pushpjain/Desktop/john-crickett-challenges-solutions/load-balancer/src/main/resources/http.json");
        Configuration configuration = ConfigurationManager.getInstance()
                .getCurrentConfiguration();

        System.out.println("Port: " + configuration.getPort());
        System.out.println("WebRoot: " + configuration.getWebroot());

        ServerListenerThread serverListenerThread = null;
        try {
            serverListenerThread = new ServerListenerThread(configuration.getPort(),
                    configuration.getWebroot());
            serverListenerThread.run();
        } catch (IOException e) {
            throw new HttpConfigurationException("Exception occurred while creating connection :: ", e);
            // todo: handle later
        }
    }
}