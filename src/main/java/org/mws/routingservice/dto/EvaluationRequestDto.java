package org.mws.routingservice.dto;

import lombok.Data;

@Data
public class EvaluationRequestDto {
    private String token;

    private String modelUrl;

    private String datasetUrl;
}
