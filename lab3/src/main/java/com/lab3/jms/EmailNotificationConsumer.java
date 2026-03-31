package com.lab3.jms;

import com.lab3.dto.WelcomeEmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailNotificationConsumer {

    @JmsListener(destination = "${app.queue.email}", containerFactory = "queueListenerFactory")
    public void receiveWelcomeEmail(WelcomeEmailMessage message) {
        log.info("Получено сообщение для отправки уведомления: {}", message);

        try {
            // Имитация долгой операции
            log.info("Имитация отправки уведомления... (2 секунды)");
            Thread.sleep(2000);

            // Имитация отправки
            log.info("Уведомление отправлено на адрес: {}", message.getEmail());

            log.info("Уведомление успешно обработано для клиента ID: {}", message.getCustomerId());

        } catch (InterruptedException e) {
            log.error("Ошибка при обработке уведомления: {}", e.getMessage());
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to process notification", e);
        } catch (Exception e) {
            log.error("Ошибка при обработке уведомления: {}", e.getMessage());
            throw new RuntimeException("Failed to process notification", e);
        }
    }
}