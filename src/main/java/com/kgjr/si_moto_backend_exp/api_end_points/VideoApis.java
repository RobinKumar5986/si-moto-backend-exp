package com.kgjr.si_moto_backend_exp.api_end_points;

import com.kgjr.si_moto_backend_exp.database_tables.Video;
import com.kgjr.si_moto_backend_exp.dto.VideoUploadRequest;
import com.kgjr.si_moto_backend_exp.dto.response.VideoResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/videos")
public interface VideoApis {
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<VideoResponse> uploadVideo(
            @RequestPart("videoFile") MultipartFile file,
            @RequestPart("data") VideoUploadRequest request
    );

}
