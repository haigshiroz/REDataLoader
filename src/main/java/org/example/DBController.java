package org.example;

import io.javalin.Javalin;
import io.javalin.http.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

import org.bson.Document;

public class DBController {
    private static final Logger log = LoggerFactory.getLogger(DBController.class);
    private final DBDao dbDao = new DBDao();


    // Maps all the helpers to the routes
    public void registerRoutes(Javalin app) {
        app.post("/residencies", this::createResidence);
    }

    // Helper for creating a residence entry in the database
    private void createResidence(Context ctx) {
        try {
            Document[] residenceArray = ctx.bodyAsClass(Document[].class);
            List<Document> residence = List.of(residenceArray);
            dbDao.createResidence(residence);
            ctx.status(201).result("Residence created");
        } catch (Exception e) {
            ctx.status(500).result("Database error");
            log.debug("Database error", e);
        }
    }
}