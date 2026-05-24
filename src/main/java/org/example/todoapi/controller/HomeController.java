package org.example.todoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class HomeController {
    @Operation(summary = "Just a greeting message")
    @GetMapping
    public String index() {
        return "Welcome to ToDo app!";
    }
}