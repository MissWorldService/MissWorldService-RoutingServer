package org.mws.routingservice.security;

import org.mws.routingservice.model.EstimationServer;
import org.mws.routingservice.repository.EstimationServerRepository;
import org.mws.routingservice.security.jwt.JwtTokenProvider;
import org.mws.routingservice.security.jwt.Server.JwtEstimationServer;
import org.mws.routingservice.security.jwt.Server.JwtEstimationServerFactory;
import org.mws.routingservice.service.EstimationServerService;
import org.mws.routingservice.service.impl.EstimationServerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JwtEstimationServerService extends EstimationServerServiceImpl {

    private final EstimationServerService estimationServerService;


    @Autowired
    JwtTokenProvider tokenProvider;

    public JwtEstimationServerService(EstimationServerRepository estimationServerRepository, EstimationServerService estimationServerService) {
        super(estimationServerRepository);
        this.estimationServerService = estimationServerService;
    }

    public JwtEstimationServer createServer(String ip){
        EstimationServer estimationServer = estimationServerService.addServer(ip);
        JwtEstimationServer jwtEstimationServer = JwtEstimationServerFactory.createJWTServer(estimationServer);
        return jwtEstimationServer;
    }
}
