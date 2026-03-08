package com.kgjr.si_moto_backend_exp.actual_logics;

import com.kgjr.si_moto_backend_exp.database_tables.Video;
import com.kgjr.si_moto_backend_exp.dto.VideoFeedRequest;
import com.kgjr.si_moto_backend_exp.dto.VideoUploadRequest;
import com.kgjr.si_moto_backend_exp.dto.response.VideoFeedResponse;
import com.kgjr.si_moto_backend_exp.dto.response.VideoResponse;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface VideoService {

     VideoResponse processAndSaveVideo(MultipartFile file, VideoUploadRequest request);
     VideoFeedResponse getVideoFeed(VideoFeedRequest request);
     ResourceRegion streamVideo(String filename, HttpHeaders headers) throws IOException;
}
