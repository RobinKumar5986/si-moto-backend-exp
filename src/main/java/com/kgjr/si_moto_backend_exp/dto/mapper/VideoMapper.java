package com.kgjr.si_moto_backend_exp.dto.mapper;


import com.kgjr.si_moto_backend_exp.database_tables.Video;
import com.kgjr.si_moto_backend_exp.dto.response.VideoResponse;
import org.springframework.stereotype.Component;
import java.nio.file.Paths;

@Component
public class VideoMapper {

    public VideoResponse toResponse(Video video) {
        if (video == null) return null;

        VideoResponse response = new VideoResponse();
        response.setId(video.getId());
        
        String fileName = Paths.get(video.getFilePath()).getFileName().toString();
        response.setVideoUrl("http://localhost:8080/api/v1/videos/stream/" + fileName);
        
        response.setCompanyId(video.getCompanyId());
        response.setCountry(video.getCountry());
        response.setState(video.getState());
        response.setTags(video.getTags());
        response.setCreatedAt(video.getCreatedAt());
        if (video.getLocation() != null) {
            response.setLatitude(video.getLocation().getY());
            response.setLongitude(video.getLocation().getX());
        }

        return response;
    }
}