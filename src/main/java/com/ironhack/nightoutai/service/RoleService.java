package com.ironhack.nightoutai.service;

import com.ironhack.nightoutai.model.Role;

public interface RoleService {

    /**
     * This method is used to save a Role entity to the database.
     *
     * @param role the Role entity to be saved.
     * @return the saved Role entity.
     */
    Role save(Role role);

    /**
     * This method is used to add a Role to a User.
     *
     * @param username the username of the User to which the Role is to be added.
     * @param roleName the name of the Role to be added.
     */
    void addRoleToUser(String username, String roleName);
}
