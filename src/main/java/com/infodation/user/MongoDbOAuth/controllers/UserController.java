package com.infodation.user.MongoDbOAuth.controllers;

import com.infodation.user.MongoDbOAuth.services.UserServiceImpl;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<Document>> pagingData(@RequestParam(value = "page", defaultValue = "0") int page,
                                                     @RequestParam(value = "size", defaultValue = "10") int size,
                                                     @RequestParam(required = false, name = "sortBY") String sortBy,
                                                     @RequestParam(name = "sort", defaultValue = "asc") String sortD) {
        Pageable pageable;

        if (sortBy == null || sortBy.isEmpty()) {
            pageable = PageRequest.of(page, size);
        } else {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortD) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Sort sort = Sort.by(direction, sortBy);
            pageable = PageRequest.of(page, size, sort);
        }

        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/{username}")
    public ResponseEntity<Document> getUserByUserName(@PathVariable("username") String username) {
        Document user = userService.getUser(username);
        if (user == null)
        {
            Map<String, String> res = new HashMap<>();
            res.put("message", username + " is not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Document(res));
        }

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping
    public ResponseEntity<Document> createUser(@RequestBody Document user) {
        Map<String, String> res = new HashMap<>();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (user.get("pwd") == "" || user.get("pwd") == null) {
            res.put("message", "pwd is null or blank");
            status = HttpStatus.BAD_REQUEST;
        }

        if (user.get("username") == "" || user.get("username") == null) {
            res.put("message", "username is null or blank");
            status = HttpStatus.BAD_REQUEST;
        }

        if (userService.existedUsername(user.get("username").toString())) {
            res.put("message", "username is existing");
            status = HttpStatus.CONFLICT;
        }

        if (!res.isEmpty()) {
            return ResponseEntity.status(status).body(new Document(res));
        }

        Document savedUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PutMapping({"/{username}"})
    public ResponseEntity<?> updateUser(@PathVariable("username") String username,@RequestBody Document user) {
        Map<String, String> res = new HashMap<>();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (user.get("pwd") == "" || user.get("pwd") == null) {
            res.put("message", "pwd is null or blank");
            status = HttpStatus.BAD_REQUEST;
        }

        if (user.get("username") == "" || user.get("username") == null) {
            res.put("message", "username is null or blank");
            status = HttpStatus.BAD_REQUEST;
        }

        if (!res.isEmpty()) {
            return ResponseEntity.status(status).body(new Document(res));
        }

        Document savedUser = userService.updateUser(username,user);
        return ResponseEntity.status(HttpStatus.OK).body(savedUser);
    }

    @GetMapping("/print-commune")
    public void print() {
        List<String> communes = userService.getAllCommune();

        for (String item : communes) {
            System.out.println(item);
        }
    }
}
