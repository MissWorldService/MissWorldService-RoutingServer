package org.mws.routingservice.service;

import org.mws.routingservice.model.EstimationServer;

import java.util.List;

public interface EstimationServerService {

    EstimationServer findServerById(Long server_id);

    EstimationServer addServer(String ip);

    public List<EstimationServer> getAllActiveServers();

    EstimationServer updateStatus(Long id, Boolean status);

    public List<EstimationServer> findAllServers();
}
