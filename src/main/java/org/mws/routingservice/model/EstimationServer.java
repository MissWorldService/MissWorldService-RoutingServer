package org.mws.routingservice.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "estimation_servers")
@Data
public class EstimationServer {


    @Column(name = "server_id")
    @Id
    @SequenceGenerator(name="server_sequence", sequenceName="server_id_sequence", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="server_sequence")
    Long serverId;

    @Column
    String ip;

    @Column(name = "active_flag")
    Boolean activeFlag;


}
