package com.skyworx.ideb.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationService(
            SimpMessagingTemplate messagingTemplate
    ) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendTestMessage() {

        Map<String, Object> message = Map.of(
                "status", "SUCCESS",
                "message", "Test WebSocket berhasil"
        );

        messagingTemplate.convertAndSend(
                "/topic/ideb",
                (Object) message
        );
    }

    public void sendSuccess(
            String processInstanceId,
            Long reportId,
            String pdfFileName
    ) {

        Map<String, Object> message = Map.of(
                "status", "SUCCESS",
                "processInstanceId", processInstanceId,
                "reportId", reportId,
                "pdfFileName", pdfFileName,
                "message", "Proses IDEB berhasil"
        );

        messagingTemplate.convertAndSend(
                "/topic/ideb",
                (Object) message
        );
    }

    public void sendFailed(
            String processInstanceId,
            String failureStage,
            String failureMessage
    ) {

        Map<String, Object> message = Map.of(
                "status", "FAILED",
                "processInstanceId", processInstanceId,
                "failureStage",
                failureStage != null ? failureStage : "UNKNOWN",
                "message",
                failureMessage != null
                        ? failureMessage
                        : "Proses IDEB gagal"
        );

        messagingTemplate.convertAndSend(
                "/topic/ideb",
                (Object) message
        );
    }
}