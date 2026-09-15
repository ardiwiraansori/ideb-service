package com.skyworx.ideb.controller;

import com.skyworx.ideb.service.WebSocketNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ws")
public class WebSocketTestController {

    private final WebSocketNotificationService notificationService;

    public WebSocketTestController(
            WebSocketNotificationService notificationService
    ) {
        this.notificationService = notificationService;
    }

    @PostMapping("/test")
    public ResponseEntity<?> sendTestMessage() {

        notificationService.sendTestMessage();

        return ResponseEntity.ok(
                Map.of(
                        "status", "OK",
                        "message", "Pesan WebSocket dikirim"
                )
        );
    }
}