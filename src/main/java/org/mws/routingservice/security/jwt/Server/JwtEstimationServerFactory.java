package org.mws.routingservice.security.jwt.Server;

import org.mws.routingservice.model.EstimationServer;

public class JwtEstimationServerFactory {

    public JwtEstimationServerFactory(){

    }

    public static JwtEstimationServer createJWTServer(EstimationServer server){
        return new JwtEstimationServer(server.getServerId(),server.getIp(),server.getActiveFlag(), server.getToken());
    }
}
