package com.kgjr.si_moto_backend_exp.api_end_points.impl;

import com.kgjr.si_moto_backend_exp.actual_logics.VideoService;
import com.kgjr.si_moto_backend_exp.api_end_points.VideoApis;
import com.kgjr.si_moto_backend_exp.database_tables.Video;
import com.kgjr.si_moto_backend_exp.dto.VideoFeedRequest;
import com.kgjr.si_moto_backend_exp.dto.VideoUploadRequest;
import com.kgjr.si_moto_backend_exp.dto.response.VideoFeedResponse;
import com.kgjr.si_moto_backend_exp.dto.response.VideoResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class VideoApisImpl implements VideoApis {

    private final VideoService videoService;
    @Override
    public ResponseEntity<VideoResponse> uploadVideo(MultipartFile file, VideoUploadRequest request) {
        VideoResponse videoResponse = videoService.processAndSaveVideo(file,request);
        return ResponseEntity.ok(videoResponse);
    }

    @Override
    public ResponseEntity<VideoFeedResponse> getVideoFeed(VideoFeedRequest request) {
        VideoFeedResponse response = videoService.getVideoFeed(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ResourceRegion> streamVideo(String filename, HttpHeaders headers) throws IOException {
        ResourceRegion region = videoService.streamVideo(filename, headers);
        return ResponseEntity
                .status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaType.parseMediaType("video/mp4"))
                .body(region);
    }

}
