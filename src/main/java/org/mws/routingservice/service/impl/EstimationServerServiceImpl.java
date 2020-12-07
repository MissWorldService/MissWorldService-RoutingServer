package org.mws.routingservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.mws.routingservice.model.EstimationServer;
import org.mws.routingservice.repository.EstimationServerRepository;
import org.mws.routingservice.service.EstimationServerService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EstimationServerServiceImpl implements EstimationServerService {

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
        EstimationServer estimationServer = new EstimationServer();
        estimationServer.setIp(ip);
        estimationServer.setActiveFlag(true);
        estimationServerRepository.save(estimationServer);
        return estimationServer;
    }
}
