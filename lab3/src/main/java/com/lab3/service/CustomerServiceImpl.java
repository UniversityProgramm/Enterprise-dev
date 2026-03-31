package com.lab3.service;

import com.lab3.entity.Customer;
import com.lab3.repository.CustomerRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Cacheable(value = "allCustomers", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #firstName + '-' + #lastName + '-' + #email + '-' + #pageable.sort")
    public Page<Customer> getAllCustomers(String firstName, String lastName, String email, Pageable pageable) {
        Specification<Customer> spec = (root, query, cb) -> cb.conjunction();

        if (firstName != null && !firstName.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"));
        }

        if (lastName != null && !lastName.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%"));
        }

        if (email != null && !email.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
        }

        return customerRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "customers", key = "#id")
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    @CacheEvict(value = {"customers", "allCustomers"}, allEntries = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Customer createCustomer(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new RuntimeException("Клиент с таким email уже существует");
        }
        customer.setCreatedAt(java.time.LocalDateTime.now());
        return customerRepository.save(customer);
    }

    @Override
    @CacheEvict(value = {"customers", "allCustomers"}, allEntries = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Customer updateCustomer(Long id, Customer customerDetails) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));

        if (!customer.getEmail().equals(customerDetails.getEmail()) &&
                customerRepository.existsByEmailAndIdNot(customerDetails.getEmail(), id)) {
            throw new RuntimeException("Клиент с таким email уже существует");
        }

        customer.setFirstName(customerDetails.getFirstName());
        customer.setLastName(customerDetails.getLastName());
        customer.setEmail(customerDetails.getEmail());

        return customerRepository.save(customer);
    }

    @Override
    @CacheEvict(value = {"customers", "allCustomers"}, allEntries = true)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}