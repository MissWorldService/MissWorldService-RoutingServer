package org.mws.routingservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.mws.routingservice.model.EstimationServer;
import org.mws.routingservice.repository.EstimationServerRepository;
import org.mws.routingservice.security.jwt.JwtTokenProvider;
import org.mws.routingservice.service.EstimationServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EstimationServerServiceImpl implements EstimationServerService {

    @Autowired
    private JwtTokenProvider tokenProvider;

    private final EstimationServerRepository estimationServerRepository;

    public EstimationServerServiceImpl(EstimationServerRepository estimationServerRepository) {
        this.estimationServerRepository = estimationServerRepository;
    }

    @Override
    public EstimationServer findServerById(Long server_id) {
        EstimationServer estimationServer = estimationServerRepository.findServerByServerId(server_id);
        return estimationServer;
    }

    @Override
    public EstimationServer addServer(String ip) {
        EstimationServer estimationServer = new EstimationServer(ip);
        estimationServer.setActiveFlag(true);
        estimationServer.setToken(tokenProvider.createToken(estimationServer.getServerId(),estimationServer.getIp()));
        estimationServerRepository.save(estimationServer);
        return estimationServer;
    }

    @Override
    public List<EstimationServer> getAllActiveServers(){
        List<EstimationServer> estimationServers;
        estimationServers = estimationServerRepository.findAllByActiveFlag(true);
        return estimationServers;
    }

    @Override
    public EstimationServer updateStatus(Long id, Boolean status) {
        EstimationServer server = findServerById(id);
        server.setActiveFlag(status);
        estimationServerRepository.save(server);
        return null;
    }

    @Override
    public List<EstimationServer> findAllServers() {
        List<EstimationServer> estimationServers;
        estimationServers = estimationServerRepository.findAll();
        return estimationServers;
    }
}
