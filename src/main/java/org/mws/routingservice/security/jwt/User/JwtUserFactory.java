package org.mws.routingservice.security.jwt.User;

import org.mws.routingservice.model.User;

public final class JwtUserFactory {

    public JwtUserFactory(){

    }

    public static JwtUser createJWTUser(User user){
        return new JwtUser(user.getUsername(), user.getPassword());
    }
}
