package org.mws.routingservice.repository;

import org.mws.routingservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository <User, String> {
    User findByUsername(String username);
}
