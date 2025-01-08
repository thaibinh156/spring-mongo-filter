package com.infodation.user.MongoDbOAuth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infodation.user.MongoDbOAuth.services.CollectionServiceImpl;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CollectionControllerTest {

    private static final Logger log = LoggerFactory.getLogger(CollectionControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CollectionServiceImpl collectionService;

    private Document request;
    private Document response;

    @BeforeEach
    public void initData () {
        request = new Document("collectionName", "testCollection");
    }

    @Test
    public void createCollection_validRequest_success() throws Exception {
        log.info("Test create collection with valid request");
        // GIVEN
        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(request);
        response = new Document("message", "testCollection is created");
        Mockito.when(collectionService.createCollection(ArgumentMatchers.anyString())).thenReturn(response);
        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.
                        post("/api/collections/create-collection")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept("application/json"))
                .andExpect(status().isOk());

    }

    @Test
    public void createCollection_invalidRequest_fail() throws Exception {
        log.info("Test create collection with invalid request");
        // GIVEN
        request.remove("collectionName");
        request.append("collectionName", "");
        response = new Document("message", "testCollection is created");
        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(request);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.
                        post("/api/collections/create-collection")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("collectionName is required"));
    }

    @Test
    public void createCollection_conflictRequest_fail() throws Exception {
        log.info("Test create collection with conflict request");
        // GIVEN
        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(request);
        Mockito.when(collectionService.isCollectionExisted(ArgumentMatchers.anyString())).thenReturn(true);
        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.
                        post("/api/collections/create-collection")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept("application/json"))
                .andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("testCollection is existed"));
    }

    @Test
    public void insertOne_validRequest_success() throws Exception {
        log.info("Test insert one document with valid request");
        // GIVEN
        Document document = new Document("name", "test");
        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(document);
        response = new Document("message", "testCollection is created");
        Mockito.when(collectionService.insertOne(ArgumentMatchers.any(Document.class), ArgumentMatchers.anyString())).thenReturn(response);
        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.
                        post("/api/collections/insert-one/testCollection")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept("application/json"))
                .andExpect(status().isOk());
    }
}
