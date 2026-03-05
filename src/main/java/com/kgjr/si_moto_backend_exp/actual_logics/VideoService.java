package com.kgjr.si_moto_backend_exp.actual_logics;

import com.kgjr.si_moto_backend_exp.database_tables.Video;
import com.kgjr.si_moto_backend_exp.dto.VideoUploadRequest;
import com.kgjr.si_moto_backend_exp.dto.response.VideoResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface VideoService {

     VideoResponse processAndSaveVideo(MultipartFile file, VideoUploadRequest request);

}
