package com.example.aifaceauthentication.config;

import com.example.aifaceauthentication.model.Role;
import com.example.aifaceauthentication.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner loadData(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByName(Role.RoleName.ROLE_USER).isEmpty()) {
                Role userRole = new Role();
                userRole.setName(Role.RoleName.ROLE_USER);
                roleRepository.save(userRole);
            }
            if (roleRepository.findByName(Role.RoleName.ROLE_ADMIN).isEmpty()) {
                Role adminRole = new Role();
                adminRole.setName(Role.RoleName.ROLE_ADMIN);
                roleRepository.save(adminRole);
            }
        };
    }
}
