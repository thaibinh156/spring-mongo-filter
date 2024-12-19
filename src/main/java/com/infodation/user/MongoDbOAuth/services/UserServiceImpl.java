package com.infodation.user.MongoDbOAuth.services;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl {

    private final MongoTemplate mongoTemplate;
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final SimpleDateFormat dateOfBirthFormatter;
    public UserServiceImpl(MongoTemplate mongoTemplate, SimpleDateFormat dateOfBirthFormatter) {
        this.mongoTemplate = mongoTemplate;
        this.dateOfBirthFormatter = dateOfBirthFormatter;
    }

    public Document getUser(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        query.fields().exclude("pwd");
        return mongoTemplate.findOne(query, Document.class,  "users");
    }

    public Page<Document> getAllUsers(Pageable pageable) {
        Query query = new Query();
        query.with(pageable);
        query.fields().exclude("pwd");
        query.limit(pageable.getPageSize());
        long total = mongoTemplate.count(query, Document.class, "users");
        return new PageImpl<>(mongoTemplate.find(query, Document.class, "users"), pageable, total);
    }

    public Document createUser(Document user) {
        try {
            if (user.containsKey("dateOfBirth")) {
                Date dateOfBirth = dateOfBirthFormatter.parse(user.get("dateOfBirth").toString());
                user.append("dateOfBirth", dateOfBirth);
            }

            Document savedUser = mongoTemplate.save(user, "users");
            savedUser.remove("pwd");
            savedUser.append("profileUrl", "http://localhost:8081/api/users/" + savedUser.get("username"));

            return savedUser;
        } catch (Exception e) {
            logger.error("Error creating user {}", e.getMessage());
            throw new RuntimeException("Error creating user: " + e.getMessage());
        }
    }

    public Document updateUser(String username, Document user) {
        try {
            Query query = new Query(Criteria.where("username").is(username));
            if (existedUsername(username)) {
                Update update = new Update();
                user.forEach(update::set);
                if (user.containsKey("dateOfBirth")) {
                    Date dateOfBirth = dateOfBirthFormatter.parse(user.get("dateOfBirth").toString());
                    user.append("dateOfBirth", dateOfBirth);
                }
                mongoTemplate.updateFirst(query, update, "users");
                query.fields().exclude("pwd");
                return mongoTemplate.findOne(query, Document.class, "users");
            }
        } catch (Exception e) {
            logger.error("Error updating user {}",e.getMessage());
            throw new RuntimeException("Error updating user: " + e.getMessage());
        }
        return null;
    }

    public boolean existedUsername (String username) {
        Query query = new Query(Criteria.where("username").is(username));
        Document existingUser = mongoTemplate.findOne(query, Document.class, "users");
        return existingUser != null;
    }
    
    public List<String> getAllCommune() {
        Query query = new Query();
        query.addCriteria(Criteria.where("address.commune").exists(true));
        query.fields().include("address.commune").exclude("_id");
        return mongoTemplate.find(query, String.class, "users");
    }
}
