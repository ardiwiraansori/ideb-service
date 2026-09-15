package com.skyworx.ideb.delegate;

import com.skyworx.ideb.service.WebSocketNotificationService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogFailureDelegate implements JavaDelegate {

    private static final Logger log =
            LoggerFactory.getLogger(LogFailureDelegate.class);

    private final WebSocketNotificationService notificationService;

    public LogFailureDelegate(
            WebSocketNotificationService notificationService
    ) {
        this.notificationService = notificationService;
    }

    @Override
    public void execute(DelegateExecution execution) {

        String nik =
                (String) execution.getVariable("nik");

        String failureStage =
                (String) execution.getVariable("failureStage");

        String failureMessage =
                (String) execution.getVariable("failureMessage");

        execution.setVariable(
                "processStatus",
                "FAILED"
        );

        notificationService.sendFailed(
                execution.getProcessInstanceId(),
                failureStage,
                failureMessage
        );

        log.error(
                "Proses IDEB gagal. stage={}, message={}, processInstanceId={}, nik={}",
                failureStage,
                failureMessage,
                execution.getProcessInstanceId(),
                nik
        );
    }
}