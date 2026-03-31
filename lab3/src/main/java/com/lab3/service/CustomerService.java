package com.lab3.service;


import com.lab3.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomerService {

    /**
     * Получить всех клиентов с пагинацией и фильтрацией
     */
    Page<Customer> getAllCustomers(String firstName, String lastName, String email, Pageable pageable);

    /**
     * Получить клиента по ID
     */
    Optional<Customer> getCustomerById(Long id);

    /**
     * Создать нового клиента
     */
    Customer createCustomer(Customer customer);

    /**
     * Обновить клиента
     */
    Customer updateCustomer(Long id, Customer customer);

    /**
     * Удалить клиента
     */
    void deleteCustomer(Long id);
}