package org.mws.routingservice.service;

import org.mws.routingservice.model.User;

import java.util.List;

public interface UserService {

    User register(User user);

    List<User> getAllUsers();

    User findByUsername(String username);

    void deleteUser(String username);
}
