package com.lab3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WelcomeEmailMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long customerId;
    private String email;
    private String firstName;
    private LocalDateTime createdAt = LocalDateTime.now();

    public WelcomeEmailMessage(Long customerId, String email, String firstName) {
        this.customerId = customerId;
        this.email = email;
        this.firstName = firstName;
        this.createdAt = LocalDateTime.now();
    }
}