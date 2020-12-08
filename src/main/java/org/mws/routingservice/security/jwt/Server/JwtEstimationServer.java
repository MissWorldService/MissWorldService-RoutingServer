package org.mws.routingservice.security.jwt.Server;

import lombok.Data;

@Data
public class JwtEstimationServer {
    private Long serverId;

    private String ip;

    private Boolean activeFlag;

    private String token;

    public JwtEstimationServer(Long serverId, String ip, Boolean activeFlag, String token){
        this.serverId = serverId;
        this.ip = ip;
        this.activeFlag = activeFlag;
        this.token = token;
    }
}
