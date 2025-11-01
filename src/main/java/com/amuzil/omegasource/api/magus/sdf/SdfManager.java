package com.amuzil.omegasource.api.magus.sdf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class SdfManager {
    public HashMap<String, SdfShapeRecord> LOADED_SDFS = new HashMap<>();

    private final String SDF_SHAPE_FOLDER = "local/av3/sdfs";

    public void readFolder() {
        try {
            StringBuilder sb = new StringBuilder();
            File sdfFolder = new File(SDF_SHAPE_FOLDER);
            for (final File fileEntry : sdfFolder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    Scanner myReader = new Scanner(fileEntry);
                    while (myReader.hasNextLine()) {
                        String data = myReader.nextLine();
                        sb.append(data);
                    }
                    myReader.close();
                    GsonBuilder builder = new GsonBuilder();
                    builder.setPrettyPrinting();
                    Gson gson = builder.create();
                    gson.fromJson(sb.toString(), SdfShapeRecord.class);
                } else {
                    System.out.println(fileEntry.getName());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred reading config json! Attempting to generate new config...");
            writeConfig();
        } catch (JsonSyntaxException e) {
            System.out.println("An error occurred reading config json!  Check if VRJesterAPI.cfg is malformed.");
        }
    }

    public void writeConfig() {
        try {
            File configFile = new File(Constants.CONFIG_PATH);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter(configFile);
            gson.toJson(config, writer);
            writer.flush();
        } catch (IOException e) {
            System.out.println("An error occurred writing config json!");
            e.printStackTrace();
        }
    }
}
