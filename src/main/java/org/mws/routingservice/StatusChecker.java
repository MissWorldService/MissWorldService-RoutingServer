package org.mws.routingservice;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.mws.routingservice.model.EstimationServer;
import org.mws.routingservice.security.JwtEstimationServerService;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URISyntaxException;
import java.util.List;

public class StatusChecker extends Thread{

    @Autowired
    private JwtEstimationServerService jwtEstimationServerService;

    @Override
    public void run(){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        List<EstimationServer> estimationServers = jwtEstimationServerService.getAllActiveServers();
        for (EstimationServer estimationServer: estimationServers){
            boolean error = false;
            URIBuilder uriBuilder = null;
            try {
                uriBuilder = new URIBuilder("http://" + estimationServer.getIp() + ":8080/api/a");
            } catch (URISyntaxException e) {
            }
            uriBuilder.setParameter("type","classification");
            HttpGet evaluateRequest = null;
            try {
                evaluateRequest = new HttpGet(uriBuilder.build());
            } catch (URISyntaxException e) {
            }
            try {
                CloseableHttpResponse response = httpClient.execute(evaluateRequest);
            }catch (Exception e){
                jwtEstimationServerService.updateStatus(estimationServer.getServerId(),false);
                error = true;
            }
            if (!error){
                jwtEstimationServerService.updateStatus(estimationServer.getServerId(),true);
            }
        }
    }
}
