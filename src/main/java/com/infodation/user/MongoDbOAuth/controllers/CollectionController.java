package com.infodation.user.MongoDbOAuth.controllers;

import com.infodation.user.MongoDbOAuth.models.CSVDataMiner;
import com.infodation.user.MongoDbOAuth.models.DataMiner;
import com.infodation.user.MongoDbOAuth.services.CollectionServiceImpl;;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    private final MongoTemplate mongoTemplate;
    private final CollectionServiceImpl collectionService;
    public CollectionController(MongoTemplate mongoTemplate, CollectionServiceImpl collectionService) {
        this.mongoTemplate = mongoTemplate;
        this.collectionService = collectionService;
    }

    @PostMapping
    public ResponseEntity<?> importData(@RequestBody MultipartFile file) {
        DataMiner miner = new CSVDataMiner();
//        String fileName = file.getOriginalFilename();
//        if (fileName.endsWith(".csv"))
//            miner = new CSVDataMiner();
//        else if (fileName.endsWith(".xlsx"))
//            miner = new ExcelDataMiner();
//        else miner = new PDFDataMiner();

        miner.mineData(file);

        if (!mongoTemplate.collectionExists(miner.getCollectionName())) {
            mongoTemplate.createCollection(miner.getCollectionName());
        }


        if (miner.getDocuments()== null)
            System.out.println("not ok");

        try {
            mongoTemplate.insert(miner.getDocuments(),miner.getCollectionName());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return ResponseEntity.status(200).body("Oke");
    }

    @PostMapping("/create-collection")
    public ResponseEntity<Document> createCollection(@RequestBody Document body) {
        Document response;
        HttpStatus status;
        String collectionName = body.get("collectionName").toString();

        if (collectionName == null || collectionName.isEmpty()) {
            response = new Document("message", "collectionName is required");
            status = HttpStatus.BAD_REQUEST;
        } else if (collectionService.isCollectionExisted(collectionName)) {
            response = new Document("message", collectionName + " is existed");
            status = HttpStatus.CONFLICT;
        } else {
            response = collectionService.createCollection(collectionName);
            status = HttpStatus.OK;
        }

        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/insert-one/{collectionName}")
    public ResponseEntity<?> insertOne(@RequestBody Document document,
                                       @PathVariable("collectionName") String collectionName)
    {
        if (!collectionService.isCollectionExisted(collectionName))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(collectionName + " is not found");
        return ResponseEntity.status(200).body(collectionService.insertOne(document,collectionName));
    }

    @GetMapping("/{collectionName}")
    public ResponseEntity<?> getWithFilter(@RequestBody Document document,
                                          @PathVariable("collectionName") String collectionName)
    {
        if (!collectionService.isCollectionExisted(collectionName))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(collectionName + " is not found");
        return ResponseEntity.status(200).body(collectionService.getWithFilter(document,collectionName));
    }

    @PutMapping("/{collectionName}")
    public ResponseEntity<?> updateWithFilter(@RequestBody Document document,
                                              @PathVariable("collectionName") String collectionName)
    {
        if (!collectionService.isCollectionExisted(collectionName))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(collectionName + " is not found");
        return ResponseEntity.status(200).body(collectionService.updateWithFilter(document,collectionName));
    }
}
