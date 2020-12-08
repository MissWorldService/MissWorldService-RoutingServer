package org.mws.routingservice.rest;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.mws.routingservice.dto.AuthenticationRequestDto;
import org.mws.routingservice.dto.EvaluationRequestDto;
import org.mws.routingservice.dto.RegistrationRequestDto;
import org.mws.routingservice.dto.ServerRegistrationRequestDto;
import org.mws.routingservice.model.EstimationServer;
import org.mws.routingservice.model.User;
import org.mws.routingservice.security.JwtEstimationServerService;
import org.mws.routingservice.security.jwt.JwtTokenProvider;
import org.mws.routingservice.security.jwt.Server.JwtEstimationServer;
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api/")
public class RestController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    @Autowired
    private JwtEstimationServerService jwtEstimationServerService;

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

    @PostMapping("evaluate")
    public ResponseEntity evaluate(@RequestBody EvaluationRequestDto requestDto) throws IOException, URISyntaxException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        List<EstimationServer> estimationServers = jwtEstimationServerService.getAllActiveServers();
        EstimationServer estimationServer = estimationServers.get(0);

        sendPost(httpClient, estimationServer, ":5000/upload-testset-from-url/", requestDto.getDatasetUrl());
        sendPost(httpClient, estimationServer, ":5000/upload-model-from-url/", requestDto.getModelUrl());
        CloseableHttpResponse evaluationResponse = sendGet(httpClient, estimationServer);

        Map<Object, Object> response = new HashMap<>();
        response.put("Result","Success");
        return ResponseEntity.ok(response);
    }

    private CloseableHttpResponse sendGet(CloseableHttpClient httpClient, EstimationServer estimationServer) throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("http:" + estimationServer.getIp() + ":5000/evaluate/");
        uriBuilder.setParameter("type","classification");
        HttpGet evaluateRequest = new HttpGet(uriBuilder.build());
        CloseableHttpResponse response = httpClient.execute(evaluateRequest);
        return response;
    }

    private void sendPost(CloseableHttpClient httpClient, EstimationServer estimationServer, String portAndEndpoint, String url) throws IOException {
        HttpPost testSetRequest = new HttpPost("http:" + estimationServer.getIp() + portAndEndpoint);
        List<NameValuePair> testSetParameters = new ArrayList<>();
        testSetParameters.add(new BasicNameValuePair("url", url));
        testSetRequest.setEntity(new UrlEncodedFormEntity(testSetParameters));
        CloseableHttpResponse testSetResponse = httpClient.execute(testSetRequest);
    }

    @PostMapping("register_server")
    public ResponseEntity registerServer(@RequestBody ServerRegistrationRequestDto requestDto){
        JwtEstimationServer estimationServer = jwtEstimationServerService.createServer(requestDto.getIp());
        Map<Object, Object> response = new HashMap<>();
        response.put("Result","Success");
        response.put("Token", estimationServer.getToken());
        return ResponseEntity.ok(response);
    }
}
