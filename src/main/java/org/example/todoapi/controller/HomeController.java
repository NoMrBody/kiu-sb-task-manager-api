package org.example.todoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.todoapi.config.AppSettings;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController("/")
public class HomeController {

    private final AppSettings appSettings;

    public HomeController(AppSettings appSettings) {
        this.appSettings = appSettings;
    }

    @Operation(summary = "Just a greeting message")
    @GetMapping
    public String index() {
        return "Welcome to ToDo app!";
    }

    @Operation(summary = "Get API metadata sourced from app.settings.* configuration")
    @GetMapping("/api/info")
    public Map<String, Object> info() {
        return Map.of(
                "title", appSettings.getTitle(),
                "contactEmail", appSettings.getContactEmail(),
                "paginationLimit", appSettings.getPaginationLimit(),
                "registrationEnabled", appSettings.isRegistrationEnabled()
        );
    }
}
