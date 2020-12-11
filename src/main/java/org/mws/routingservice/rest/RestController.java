package org.mws.routingservice.rest;

import lombok.val;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.mws.routingservice.StatusChecker;
import org.mws.routingservice.dto.*;
import org.mws.routingservice.model.EstimationServer;
import org.mws.routingservice.model.User;
import org.mws.routingservice.security.JwtEstimationServerService;
import org.mws.routingservice.security.jwt.JwtTokenProvider;
import org.mws.routingservice.security.jwt.Server.JwtEstimationServer;
import org.mws.routingservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
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
    public ResponseEntity evaluate(@RequestBody EvaluationRequestDto requestDto) throws IOException, URISyntaxException, JSONException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        List<EstimationServer> estimationServers = jwtEstimationServerService.getAllActiveServers();
        Map<Object, Object> response = new HashMap<>();
        if(estimationServers.size()>=0) {
            EstimationServer estimationServer = new EstimationServer();//estimationServers.get(0);
            //jwtEstimationServerService.updateStatus(estimationServer.getServerId(),false);
            sendPost(httpClient, estimationServer, ":80/upload-testset-from-url/", requestDto.getDatasetUrl());
            sendPost(httpClient, estimationServer, ":80/upload-model-from-url/", requestDto.getModelUrl());
            ResponseEntity<Map> evaluationResponse = sendGet(requestDto.getType());
            //jwtEstimationServerService.updateStatus(estimationServer.getServerId(),true);
            //StatusChecker statusChecker = new StatusChecker();
            //statusChecker.start();
            //HttpEntity entity = evaluationResponse.getEntity();
            //String data = EntityUtils.toString(entity);
            response.put("Data", evaluationResponse.getBody().get("metrics"));
            response.put("Result", "Success");
        }
        else{
            response.put("Result", "Fault");
            StatusChecker statusChecker = new StatusChecker();
            statusChecker.start();
        }
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map> sendGet(String type) throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("http://" +/* estimationServer.getIp()*/  "52.15.232.97" + "/evaluate/");
        RestTemplate restTemplate = new RestTemplate();
        EvaluateRequestDto data = new EvaluateRequestDto();
        data.setType(type);
        System.out.println(data);
        val response =  restTemplate.exchange(uriBuilder.build(),
                HttpMethod.POST,
                new org.springframework.http.HttpEntity<EvaluateRequestDto>(data, new HttpHeaders()),
                Map.class);
        return response;
    }

    private void sendPost(CloseableHttpClient httpClient, EstimationServer estimationServer, String portAndEndpoint, String url) throws IOException, JSONException, URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder("http://" +/* estimationServer.getIp()*/  "52.15.232.97" + portAndEndpoint);

        RestTemplate restTemplate = new RestTemplate();
        PostToServerRequestDto data = new PostToServerRequestDto();
        data.setUrl(url);
        val response =  restTemplate.exchange(uriBuilder.build(),
                HttpMethod.POST,
                new org.springframework.http.HttpEntity<PostToServerRequestDto>(data, new HttpHeaders()),
                Map.class);
        //CloseableHttpResponse testSetResponse = httpClient.execute(testSetRequest);

    }

    @PostMapping("register_server")
    public ResponseEntity registerServer(HttpServletRequest request){
        JwtEstimationServer estimationServer = jwtEstimationServerService.createServer(request.getRemoteAddr());
        Map<Object, Object> response = new HashMap<>();
        response.put("Result","Success");
        response.put("ip",request.getRemoteAddr());
        response.put("Token", estimationServer.getToken());
        return ResponseEntity.ok(response);
    }

}
