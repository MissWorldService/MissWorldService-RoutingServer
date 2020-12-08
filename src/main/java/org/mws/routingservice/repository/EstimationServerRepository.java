package org.mws.routingservice.repository;

import org.mws.routingservice.model.EstimationServer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstimationServerRepository extends JpaRepository<EstimationServer, Long> {
    EstimationServer findServerByServerId (Long server_id);

    List<EstimationServer> findAllByActiveFlag(Boolean flag);
}
