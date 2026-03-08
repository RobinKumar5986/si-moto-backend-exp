package com.kgjr.si_moto_backend_exp.api_end_points;

import com.kgjr.si_moto_backend_exp.database_tables.Video;
import com.kgjr.si_moto_backend_exp.dto.VideoFeedRequest;
import com.kgjr.si_moto_backend_exp.dto.VideoUploadRequest;
import com.kgjr.si_moto_backend_exp.dto.response.VideoFeedResponse;
import com.kgjr.si_moto_backend_exp.dto.response.VideoResponse;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    @PostMapping(
            value = "/feed",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<VideoFeedResponse> getVideoFeed(
            @RequestBody VideoFeedRequest request
    );

    @GetMapping("/stream/{filename}")
    ResponseEntity<ResourceRegion> streamVideo(
            @PathVariable String filename,
            @RequestHeader HttpHeaders headers
    ) throws IOException;
}
