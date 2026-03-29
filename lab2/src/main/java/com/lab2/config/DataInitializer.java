package com.lab2.config;

import com.lab2.entity.Role;
import com.lab2.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    @Transactional
    public void init() {
        // Создаём роль ROLE_USER, если её нет
        if (!roleRepository.findByName(Role.ERole.ROLE_USER).isPresent()) {
            Role userRole = new Role(Role.ERole.ROLE_USER);
            roleRepository.save(userRole);
            System.out.println("✓ Роль ROLE_USER создана");
        }

        // Создаём роль ROLE_ADMIN, если её нет
        if (!roleRepository.findByName(Role.ERole.ROLE_ADMIN).isPresent()) {
            Role adminRole = new Role(Role.ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
            System.out.println("✓ Роль ROLE_ADMIN создана");
        }
    }
}