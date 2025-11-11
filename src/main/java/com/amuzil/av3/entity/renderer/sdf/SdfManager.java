package com.amuzil.av3.entity.renderer.sdf;

import com.amuzil.av3.entity.renderer.PointData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Scanner;

public class SdfManager {
    public static SdfManager INSTANCE = new SdfManager();

    public HashMap<String, SdfShapeRecord> SDFS = new HashMap<>();

    private final String SDF_SHAPE_FOLDER = "./local/av3/sdfs";

    public void registerSdf(String name, SignedDistanceFunction function, boolean animated) {
        // create shape record.
        SdfShapeRecord record = new SdfShapeRecord(function);

        // todo: bake the field.
        record.sdfField = new PointData[][][] {};

        SDFS.put(name, record);
    }

    public void readFolder() {
        try {
            File sdfFolder = new File(SDF_SHAPE_FOLDER);
            File[] files = sdfFolder.listFiles();
            Gson gson = SdfGson.create().setPrettyPrinting().create();
            for (File fileEntry : files) {
                if (fileEntry.isFile()) {
                    StringBuilder sb = new StringBuilder();
                    Scanner myReader = new Scanner(fileEntry);
                    while (myReader.hasNextLine()) {
                        String data = myReader.nextLine();
                        sb.append(data);
                    }
                    myReader.close();
                    myReader = null;
                    String jsonString = sb.toString();
                    SdfShapeRecord loadedSdf = gson.fromJson(jsonString, SdfShapeRecord.class);
                    SDFS.put(fileEntry.getName(), loadedSdf);
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
            File configFile = new File(SDF_SHAPE_FOLDER);
            Files.createDirectories(configFile.toPath());

            for (final String name : SDFS.keySet()) {
                File toWrite = new File(SDF_SHAPE_FOLDER + "/" + name + ".json");

                Gson gson = SdfGson.create().setPrettyPrinting().create();

                FileWriter writer = new FileWriter(toWrite);
                gson.toJson(SDFS.get(name), writer);
                writer.flush();

            }
        } catch (IOException e) {

            System.out.println("An error occurred writing config json!");
            e.printStackTrace();
        }
    }
}
