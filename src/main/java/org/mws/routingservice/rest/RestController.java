package org.mws.routingservice.rest;

import org.mws.routingservice.dto.AuthenticationRequestDto;
import org.mws.routingservice.model.User;
import org.mws.routingservice.security.jwt.JwtTokenProvider;
import org.mws.routingservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api/")
public class RestController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;




    @Autowired
    public RestController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService) throws Exception {
        this.authenticationManager =  authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("login")
    public ResponseEntity login(@RequestBody AuthenticationRequestDto requestDto){
        try{
            String username = requestDto.getUsername();
            String password = requestDto.getPassword();
            User user = userService.findByUsername(username);
            if (user == null){
                throw new UsernameNotFoundException("User with such username not found");
            }
            boolean passwordMatches = bCryptPasswordEncoder.matches(password,user.getPassword());
            if (!passwordMatches){
                return ResponseEntity.badRequest().body("Password is incorrect");
            }
            String token = jwtTokenProvider.createToken(username);
            Map<Object, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException e){
            throw new BadCredentialsException("Invalid username or password");
        }
    }
    @PostMapping("register")
    public void createUser(){
        User user = new User();
        user.setUsername("user");
        user.setPassword("text");
        userService.register(user);
    }
}
