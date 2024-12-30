package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String configFilePath = "configuration_file.json"; // Replace with the actual path

        try {
            // Initialize the GPSIMUDataBase
            GPSIMUDataBase gpsimuDb = GPSIMUDataBase.getInstance();
            gpsimuDb.initialize(configFilePath);

            // Access the GPSIMU instance
            GPSIMU gpsimu = gpsimuDb.getGPSIMU();

            // Print all poses
            System.out.println("Loaded Poses:");
            gpsimu.getPoseList().forEach(System.out::println);

            // Check GPSIMU status
            System.out.println("GPSIMU Status: " + gpsimu.getStatus());
        } catch (RuntimeException e) {
            System.err.println("Error initializing GPSIMUDataBase: " + e.getMessage());
        }
    }
}
