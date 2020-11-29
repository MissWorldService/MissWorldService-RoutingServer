package org.mws.routingservice.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @Column(name = "login")
    String username;

    @Column
    String password;
}
