package org.example;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.Javalin;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;
import java.nio.charset.StandardCharsets;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    private static final CSVFormat CSV_FORMAT = CSVFormat.Builder.create(CSVFormat.RFC4180)
        .setHeader()
        .setSkipHeaderRecord(true)
        .setAllowDuplicateHeaderNames(false)
        .build();

    static final private String PATH_TO_FILE = ".\\src\\data\\nsw_property_data.csv";
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7070);
        DBController dbController = new DBController();
        dbController.registerRoutes(app);


        // TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("Hello and welcome!");

        // Path of CSV file to read
        final Path csvFilePath = Paths.get(PATH_TO_FILE);

        List<String> allData = new ArrayList<>();
        try (CSVParser parser = CSVParser.parse(csvFilePath, StandardCharsets.UTF_8, CSV_FORMAT)){
            // Iterate over input CSV records
            int count = 0;
            for (final CSVRecord record : parser)
            {
                // Get all of the header names and associated values from the record
                final Map<String, String> recordValues = record.toMap();

                // Write the updated values to the output CSV
                // Convert to JSON
                String jsonPayload = new ObjectMapper().writeValueAsString(recordValues);

                allData.add(jsonPayload);

                count++;

                // Every 2000 records send a request
                if (count >= 2000) {
                    // Create an HTTP create Request
                    HttpRequest request = HttpRequest.newBuilder()
                        .version(Version.HTTP_1_1)
                        .uri(URI.create("http://localhost:7070/residencies"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(allData.toString()))
                        .build();

                    // Send the request
                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        client.send(request, HttpResponse.BodyHandlers.ofString());
                    } catch (IOException | InterruptedException e) {
                        System.out.println("Error sending request: " + e.getMessage());
                    }

                    count = 0;
                    allData.clear();
                }
            }

            // Stragglers
            if (count > 0) {
                // Create an HTTP create Request
                HttpRequest request = HttpRequest.newBuilder()
                .version(Version.HTTP_1_1)
                .uri(URI.create("http://localhost:7070/residencies"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(allData.toString()))
                .build();

                // Send the request
                try {
                    HttpClient client = HttpClient.newHttpClient();
                    client.send(request, HttpResponse.BodyHandlers.ofString());
                } catch (IOException | InterruptedException e) {
                    System.out.println("Error sending request: " + e.getMessage());
                }
            }

            System.out.println("Total records: " + count);
        } catch (IOException e) {
            System.out.println("File open failed " + e.getMessage());
        }
    }
}