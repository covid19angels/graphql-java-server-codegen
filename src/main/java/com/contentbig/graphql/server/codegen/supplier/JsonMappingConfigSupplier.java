package com.contentbig.graphql.server.codegen.supplier;

import com.contentbig.graphql.server.codegen.model.MappingConfig;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Retrieve a MappingConfig fro json configuration file.
 *
 * @author valinha
 */
public class JsonMappingConfigSupplier implements MappingConfigSupplier {

    private String jsonConfigFile;

    /**
     * Instantiates a new Json configuration file supplier.
     *
     * @param jsonConfigFile the json config file
     */
    public JsonMappingConfigSupplier(String jsonConfigFile) {
        this.jsonConfigFile = jsonConfigFile;
    }

    @Override
    public MappingConfig get() {
        if (jsonConfigFile != null && !jsonConfigFile.isEmpty()) {
            try {
                return new GsonBuilder().create().fromJson(new FileReader(new File(jsonConfigFile)), MappingConfig.class);
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return null;
    }
}
