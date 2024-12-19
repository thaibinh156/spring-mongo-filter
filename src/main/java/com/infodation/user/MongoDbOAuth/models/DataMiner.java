package com.infodation.user.MongoDbOAuth.models;

import com.infodation.user.MongoDbOAuth.utils.ConvertValueUtil;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.*;

public abstract class DataMiner {
    protected final Logger log = LoggerFactory.getLogger(DataMiner.class);
    private String collectionName;
    private String[][] keys;
    private List<Document> documents;

    public final void mineData(MultipartFile file) {
        String[] nameArray = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
        setDocuments(new ArrayList<>());
        setCollectionName(nameArray[0]);
        log.info("Collection name is {}", getCollectionName());
        openFile(file);
    }

    protected abstract void openFile(MultipartFile file);
    protected abstract void readKey(String[] head, String[] row);

    void readRow(String[] row) {
        if (keys == null || keys[0].length != row.length) {
            throw new IllegalArgumentException("Keys and row length mismatch.");
        }

        Map<String, Object> map = new HashMap<>();

        try {
            for (int i = 0; i < row.length; i++) {
                if (!row[i].isEmpty())
                    map.put(keys[0][i], ConvertValueUtil.parseValue(keys[1][i], row[i]));
            }
        } catch (ParseException e) {
            log.error("An error during parse Process {}", e.getMessage());
        }

        documents.add(new Document(map));
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String[][] getKeys() {
        return keys;
    }

    public void setKeys(String[][] keys) {
        this.keys = keys;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}
