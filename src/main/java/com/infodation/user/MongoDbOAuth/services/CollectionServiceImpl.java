package com.infodation.user.MongoDbOAuth.services;

import com.infodation.user.MongoDbOAuth.utils.DynamicFilterParser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CollectionServiceImpl {

    private final MongoTemplate mongoTemplate;
    private final static Logger log = LoggerFactory.getLogger(CollectionServiceImpl.class);

    public CollectionServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public MongoCollection<?> createCollection (String collectionName) {
        return mongoTemplate.createCollection(collectionName);
    }

    public Document insertOne (Document document,  String collectionName) {
        return mongoTemplate.insert(document, collectionName);
    }

    public boolean isCollectionExisted (String collectionName) {
        return mongoTemplate.collectionExists(collectionName);
    }

    public List<Document> getWithFilter(Document document, String collectionName) {
        Map<String, Object> filter = (Map<String, Object>) document.get("filter");
        Criteria criteria = DynamicFilterParser.parseFilter(filter);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Document.class, collectionName);
    }

    public List<Document> updateWithFilter(Document document, String collectionName) {
        // Extract the filter and set parts from the document
        Map<String, Object> filter = (Map<String, Object>) document.get("filter");
        Criteria criteria = DynamicFilterParser.parseFilter(filter);
        Query query = new Query(criteria);

        Map<String, Object> set = (Map<String, Object>) document.get("set");

        Update update = new Update();
        set.forEach((key, value) -> update.set(key, value));

        UpdateResult result = mongoTemplate.updateMulti(query, update, collectionName);

        return mongoTemplate.find(query, Document.class, collectionName);
    }


}
