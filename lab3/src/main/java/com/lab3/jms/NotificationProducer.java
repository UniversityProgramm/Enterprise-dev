package com.lab3.jms;

import com.lab3.dto.WelcomeEmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Component;

import jakarta.jms.Message;

@Slf4j
@Component
public class NotificationProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${app.queue.email}")
    private String emailQueue;

    /**
     * Отправляет сообщение о необходимости отправить приветственное письмо
     * Метод возвращает управление немедленно, реальная отправка происходит асинхронно
     */
    public void sendWelcomeEmail(Long customerId, String email, String firstName) {
        WelcomeEmailMessage message = new WelcomeEmailMessage(customerId, email, firstName);
        log.info(" Отправка сообщения в очередь {}: {}", emailQueue, message);

        jmsTemplate.convertAndSend(emailQueue, message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws jakarta.jms.JMSException {
                message.setStringProperty("messageType", "welcome-email");
                message.setLongProperty("customerId", customerId);
                return message;
            }
        });
    }
}