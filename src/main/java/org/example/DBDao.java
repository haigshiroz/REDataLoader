package org.example;

import java.util.List;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


public class DBDao {    
    public void createResidence(List<Document> newResidences) {
        // Establish connection
        // try (MongoClient mongoClient = MongoClients.create("mongodb+srv://shirozianh:lReVvB53gWWFTOyx@realestatedata.ncrbvt4.mongodb.net/")) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/")) {
            // Get the database
            MongoDatabase sampleTrainingDB = mongoClient.getDatabase("RealEstateDB");
            
            // Get the specific collection of residencies
            MongoCollection<Document> residenciesCollection = sampleTrainingDB.getCollection("residencies");


            // Convert purchase_price to int
            for (Document doc : newResidences) {
                Object priceObj = doc.get("purchase_price");
                if (priceObj instanceof String) {
                    try {
                        int price = Integer.parseInt((String) priceObj);
                        doc.put("purchase_price", price);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid purchase_price: " + priceObj);
                    }
                }
            }


            // Add new residence, 
            residenciesCollection.insertMany(newResidences);
        } catch (Exception e) {
            System.err.println("Error in createResidence Dao: " + e.getMessage());
        }
    }
}