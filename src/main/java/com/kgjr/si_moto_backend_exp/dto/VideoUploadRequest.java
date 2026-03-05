package com.kgjr.si_moto_backend_exp.dto;

import lombok.Data;
import java.util.List;

@Data
public class VideoUploadRequest {
    private String companyId;
    private double latitude;
    private double longitude;
    private String country;
    private String state;
    private List<String> tags;
}