package com.ironhack.nightoutai.service.impl;

import com.ironhack.nightoutai.model.Role;
import com.ironhack.nightoutai.model.User;
import com.ironhack.nightoutai.repository.RoleRepository;
import com.ironhack.nightoutai.repository.UserRepository;
import com.ironhack.nightoutai.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    /**
     * Saves a new role to the database
     *
     * @param role the role to be saved
     * @return the saved role
     */
    @Override
    public Role save(Role role) {
        log.info("Saving new role {} to the database", role.getName());
        return roleRepository.save(role);
    }

    /**
     * Adds a role to the user with the given username.
     * Validates that both the user and role exist before attempting to add the role.
     *
     * @param username the username of the user to add the role to
     * @param roleName the name of the role to be added
     */
    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to user {}", roleName, username);

        // Retrieve the user and role objects from the repository
        User user = userRepository.findByUsername(username);
        Role role = roleRepository.findByName(roleName);

        // validamos que el rol exista en el db
        if (user == null) {
            log.error("❌ User not found in database: {}", username);
            return;
        }

        // Validamos que el rol exista en el db
        if (role == null) {
            log.error("❌ Role not found in database: {}", roleName);
            return;
        }

        // agregamos el rol al usuario
        user.getRoles().add(role);

        // guardamos el usuario
        userRepository.save(user);
        log.info("✅ Role {} successfully added to user {}", roleName, username);
    }
}
