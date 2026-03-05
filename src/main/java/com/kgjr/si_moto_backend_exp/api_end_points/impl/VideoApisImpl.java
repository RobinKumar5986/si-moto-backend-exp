package com.kgjr.si_moto_backend_exp.api_end_points.impl;

import com.kgjr.si_moto_backend_exp.actual_logics.VideoService;
import com.kgjr.si_moto_backend_exp.api_end_points.VideoApis;
import com.kgjr.si_moto_backend_exp.database_tables.Video;
import com.kgjr.si_moto_backend_exp.dto.VideoUploadRequest;
import com.kgjr.si_moto_backend_exp.dto.response.VideoResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class VideoApisImpl implements VideoApis {

    private final VideoService videoService;
    @Override
    public ResponseEntity<VideoResponse> uploadVideo(MultipartFile file, VideoUploadRequest request) {
        VideoResponse videoResponse = videoService.processAndSaveVideo(file,request);
        return ResponseEntity.ok(videoResponse);
    }
}
