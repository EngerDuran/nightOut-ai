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

        log.info("Seeding users and roles with high-fidelity data...");
        roleService.save(new Role("ROLE_USER"));
        roleService.save(new Role("ROLE_ADMIN"));

        // 🎫 Clientes Generales y VIP
        userService.saveUser(new User("Carlos Pérez", "carlos.perez@gmail.com", "Carlos2026!"));
        userService.saveUser(new User("Lucía Ortiz", "lucia.ortiz@gmail.com", "LuciaO99!"));

        // 👑 Administradores del Club
        userService.saveUser(new User("Alejandro Garrido", "alex.garrido@nightout.ai", "Kapital2026!"));
        userService.saveUser(new User("Sara Villanueva", "sara.villanueva@nightout.ai", "SaraRrpp2026!"));

        // Asignación de Roles usando los nuevos emails corporativoso personales
        roleService.addRoleToUser("carlos.perez@gmail.com", "ROLE_USER");
        roleService.addRoleToUser("lucia.ortiz@gmail.com", "ROLE_USER");

        roleService.addRoleToUser("alex.garrido@nightout.ai", "ROLE_ADMIN");
        roleService.addRoleToUser("sara.villanueva@nightout.ai", "ROLE_ADMIN");
        roleService.addRoleToUser("sara.villanueva@nightout.ai", "ROLE_USER"); // Multi-role de prueba

        log.info("User seeding complete. Data ready for a real demo!");
    }
}