package com.kgjr.si_moto_backend_exp.actual_logics.impl;

import com.kgjr.si_moto_backend_exp.actual_logics.VideoService;
import com.kgjr.si_moto_backend_exp.database_tables.Video;
import com.kgjr.si_moto_backend_exp.db_queries.VideoRepository;
import com.kgjr.si_moto_backend_exp.dto.VideoUploadRequest;
import com.kgjr.si_moto_backend_exp.dto.mapper.VideoMapper;
import com.kgjr.si_moto_backend_exp.dto.response.VideoResponse;
import org.locationtech.jts.geom.Point;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private final VideoMapper videoMapper;

    @Value("${files.video}")
    private String uploadDir;

    @Override
    public VideoResponse processAndSaveVideo(MultipartFile file, VideoUploadRequest request) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = (originalFilename != null && originalFilename.contains("."))
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";

            String newFilename = UUID.randomUUID().toString() + extension;
            Path root = Paths.get(uploadDir);

            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            Files.copy(file.getInputStream(), root.resolve(newFilename));

            Video video = new Video();
            video.setCompanyId(request.getCompanyId());
            video.setFilePath(uploadDir + newFilename);
            video.setFileExtension(extension.replace(".", ""));
            video.setCountry(request.getCountry());
            video.setState(request.getState());
            video.setTags(request.getTags());

            Point location = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
            video.setLocation(location);

            Video savedVideo =  videoRepository.save(video);

            return videoMapper.toResponse(savedVideo);
        } catch (IOException e) {
            throw new RuntimeException("Storage error: " + e.getMessage());
        }
    }
}