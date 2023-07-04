package com.group2.server.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class restController {

    @PostMapping("/logout")
    public ResponseEntity<String> logOut() {
        // server response
        return ResponseEntity.ok("Logout Successful");
    }
}
