package com.server.notification_service.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {

    @GetMapping("/test")
    @PreAuthorize("hasAuthority('USER')")
    public String test() {
        return "test";
    }
}