package com.lab2.repository;

import com.lab2.entity.Customer;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>,
        JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByEmail(String email);
}