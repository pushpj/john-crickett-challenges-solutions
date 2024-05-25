package com.tartan.marvel.HelperService.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.tartan.marvel.HelperService.util.Json;

import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class ConfigurationManager {
    private static ConfigurationManager myConfigurationManager;
    private static Configuration myCurrentConfiguration;

    private ConfigurationManager() {
    }

    public static ConfigurationManager getInstance() {
        if (Objects.isNull(myConfigurationManager))
            myConfigurationManager = new ConfigurationManager();
        return myConfigurationManager;
    }

    /**
     * used to load a Configuration File by the path provided
     * @param filePath
     */
    public void loadConfigurationFile(String filePath) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            FileReader fileReader = new FileReader(filePath);
            int i;
            while ((i = fileReader.read()) != -1) {
                stringBuffer.append((char) i);
            }
        } catch (IOException e) {
            throw new HttpConfigurationException("Configuration file not found at filePath!");
        }

        try {
            JsonNode configuration = Json.parse(stringBuffer.toString());
            myCurrentConfiguration = Json.fromJson(configuration, Configuration.class);
        } catch (IOException e) {
            throw new HttpConfigurationException("Error parsing the Configuration file!");
        }
    }

    /**
     * returns the current loaded configuration
     */
    public Configuration getCurrentConfiguration() {
        if (Objects.nonNull(myCurrentConfiguration))
            return myCurrentConfiguration;
        throw new HttpConfigurationException("No current configuration set!");
    }
}
