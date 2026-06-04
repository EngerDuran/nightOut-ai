package com.ironhack.nightoutai.demo;

import com.ironhack.nightoutai.service.RoleService;
import com.ironhack.nightoutai.service.UserService;
import com.ironhack.nightoutai.model.Role;
import com.ironhack.nightoutai.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(1) // Se ejecuta primero
@RequiredArgsConstructor
@Slf4j
public class UserDataLoader implements CommandLineRunner {

    private final UserService userService;
    private final RoleService roleService;

    @Override
    @Transactional
    public void run(String... args) {
        // Validación simple para no duplicar si ya existen roles
        if (userService.getUsers().size() > 0) {
            log.info("Users already exist — skipping UserDataLoader");
            return;
        }

        log.info("Seeding users and roles...");
        roleService.save(new Role("ROLE_USER"));
        roleService.save(new Role("ROLE_ADMIN"));

        userService.saveUser(new User("John Doe", "john", "1234"));
        userService.saveUser(new User("James Smith", "james", "1234"));
        userService.saveUser(new User("Jane Carry", "jane", "1234"));
        userService.saveUser(new User("Chris Anderson", "chris", "1234"));

        roleService.addRoleToUser("john", "ROLE_USER");
        roleService.addRoleToUser("james", "ROLE_ADMIN");
        roleService.addRoleToUser("jane", "ROLE_USER");
        roleService.addRoleToUser("chris", "ROLE_ADMIN");
        roleService.addRoleToUser("chris", "ROLE_USER");
        log.info("User seeding complete.");
    }
}