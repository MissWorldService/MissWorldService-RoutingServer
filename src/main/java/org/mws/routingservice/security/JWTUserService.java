package org.mws.routingservice.security;

import lombok.extern.slf4j.Slf4j;
import org.mws.routingservice.model.User;
import org.mws.routingservice.security.jwt.User.JwtUser;
import org.mws.routingservice.security.jwt.User.JwtUserFactory;
import org.mws.routingservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JWTUserService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public JWTUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user = userService.findByUsername(username);

        if(user == null){
            throw new UsernameNotFoundException("User with such username not found");
        }

        JwtUser jwtUser = JwtUserFactory.createJWTUser(user);
        return jwtUser;
    }

}
