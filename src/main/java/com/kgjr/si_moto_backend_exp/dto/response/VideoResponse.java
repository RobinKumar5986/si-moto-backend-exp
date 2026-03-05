package com.kgjr.si_moto_backend_exp.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class VideoResponse {
    private Long id;
    private String videoUrl;
    private String companyId;
    private double latitude;
    private double longitude;
    private String country;
    private String state;
    private List<String> tags;
    private LocalDateTime createdAt;
}