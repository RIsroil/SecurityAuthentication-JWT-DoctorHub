package com.example.demo.config;

import com.example.demo.user.Role;
import com.example.demo.user.UserEntity;
import com.example.demo.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "aaa";
        String adminPassword = "aaa";

        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            UserEntity admin = new UserEntity();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("✔ Admin user created: " + adminEmail);
        }
    }
}
