package com.lab1.service;

import com.lab1.entity.Customer;
import com.lab1.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Customer createCustomer(Customer customer) {
        // Проверка на существование клиента с таким email
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new RuntimeException("Клиент с таким email уже существует");
        }

        // Установка даты создания
        customer.setCreatedAt(java.time.LocalDateTime.now());

        // Сохранение в БД
        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(Long id, Customer customerDetails) {
        // Поиск клиента
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));

        // Проверка на уникальность email (если изменён)
        if (!customer.getEmail().equals(customerDetails.getEmail()) &&
                customerRepository.existsByEmailAndIdNot(customerDetails.getEmail(), id)) {
            throw new RuntimeException("Клиент с таким email уже существует");
        }

        // Обновление данных
        customer.setFirstName(customerDetails.getFirstName());
        customer.setLastName(customerDetails.getLastName());
        customer.setEmail(customerDetails.getEmail());

        // Сохранение
        return customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}