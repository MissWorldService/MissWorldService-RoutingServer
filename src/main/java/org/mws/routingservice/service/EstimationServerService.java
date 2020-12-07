package org.mws.routingservice.service;

import org.mws.routingservice.model.EstimationServer;

public interface EstimationServerService {

    EstimationServer findServerById(Long server_id);

    EstimationServer addServer(String ip);
}
