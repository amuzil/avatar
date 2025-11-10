package com.amuzil.av3.entity.renderer.sdf;

import com.amuzil.av3.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

public class SdfManager {
    public HashMap<String, SdfShapeRecord> LOADED_SDFS = new HashMap<>();

    private final String SDF_SHAPE_FOLDER = "./local/av3/sdfs";

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
        SdfConstants.init();
        try {
            File configFile = new File(SDF_SHAPE_FOLDER);
            Files.createDirectories(configFile.toPath());

            File toWrite = new File(SDF_SHAPE_FOLDER + "/" + "sphere" + ".json");

//            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Gson gson2 = SdfGson.create().setPrettyPrinting().create();

            FileWriter writer = new FileWriter(toWrite);
//            gson.toJson(new SdfShapeRecord(SdfConstants.STATIC_SPHERE), writer);
            gson2.toJson(new SdfShapeRecord(SdfConstants.STATIC_SPHERE), writer);
            writer.flush();
        } catch (IOException e) {

            System.out.println("An error occurred writing config json!");
            e.printStackTrace();
        }
    }
}
