package org.mws.routingservice.rest;

import org.mws.routingservice.dto.AuthenticationRequestDto;
import org.mws.routingservice.dto.RegistrationRequestDto;
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
                throw new UsernameNotFoundException("User with such username not found");
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
    public ResponseEntity register(@RequestBody RegistrationRequestDto requestDto){
        User user = new User();
        user.setUsername(requestDto.getUsername());
        user.setPassword(requestDto.getPassword());
        userService.register(user);
        Map<Object, Object> response = new HashMap<>();
        response.put("username", requestDto.getUsername());
        response.put("success", "Y");
        return ResponseEntity.ok(response);
    }
}
