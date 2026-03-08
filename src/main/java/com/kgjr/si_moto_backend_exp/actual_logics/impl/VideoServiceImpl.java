package com.kgjr.si_moto_backend_exp.actual_logics.impl;

import com.kgjr.si_moto_backend_exp.actual_logics.VideoService;
import com.kgjr.si_moto_backend_exp.database_tables.Video;
import com.kgjr.si_moto_backend_exp.db_queries.VideoRepository;
import com.kgjr.si_moto_backend_exp.dto.VideoFeedRequest;
import com.kgjr.si_moto_backend_exp.dto.VideoUploadRequest;
import com.kgjr.si_moto_backend_exp.dto.mapper.VideoMapper;
import com.kgjr.si_moto_backend_exp.dto.response.VideoFeedResponse;
import com.kgjr.si_moto_backend_exp.dto.response.VideoResponse;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.HttpRange;


import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private static final int PAGE_SIZE = 10;
    private static final long CHUNK_SIZE = 1024 * 1024;

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

            String newFilename = UUID.randomUUID() + extension;
            Path root = Paths.get(uploadDir);

            if (!Files.exists(root)) Files.createDirectories(root);
            Files.copy(file.getInputStream(), root.resolve(newFilename));

            Video video = new Video();
            video.setCompanyId(request.getCompanyId());
            video.setFilePath(uploadDir + newFilename);
            video.setFileExtension(extension.replace(".", ""));
            video.setCountry(request.getCountry());
            video.setState(request.getState());
            video.setTags(request.getTags());
            video.setLocation(
                    geometryFactory.createPoint(
                            new Coordinate(request.getLongitude(), request.getLatitude())
                    )
            );

            return videoMapper.toResponse(videoRepository.save(video));
        } catch (IOException e) {
            throw new RuntimeException("Storage error: " + e.getMessage());
        }
    }

    @Override
    public ResourceRegion streamVideo(String filename, HttpHeaders headers) throws IOException {

        // 1. Build the full path and load as a Spring Resource
        Path videoPath = Paths.get(uploadDir + filename);
        Resource video = new FileSystemResource(videoPath);

        if (!video.exists()) {
            throw new RuntimeException("Video not found: " + filename);
        }

        long contentLength = video.contentLength();

        // 2. Check if the client sent a Range header
        //    e.g. browser sends "Range: bytes=0-1048576" automatically
        Optional<HttpRange> range = headers.getRange().stream().findFirst();

        if (range.isPresent()) {
            // Client wants a specific byte range — serve exactly that chunk
            long start       = range.get().getRangeStart(contentLength);
            long end         = range.get().getRangeEnd(contentLength);
            long rangeLength = Math.min(CHUNK_SIZE, end - start + 1);
            return new ResourceRegion(video, start, rangeLength);
        }

        // No Range header — serve the first chunk
        // Browser will then automatically request subsequent ranges
        long rangeLength = Math.min(CHUNK_SIZE, contentLength);
        return new ResourceRegion(video, 0, rangeLength);
    }

    @Override
    public VideoFeedResponse getVideoFeed(VideoFeedRequest request) {

        // 1. Decode the cursor back into a timestamp (null = first page)
        LocalDateTime cursorTimestamp = decodeCursor(request.cursor());

        // 2. SQL IN() cannot be empty — use a dummy ID that never matches
        List<Long> seenIds = (request.seenVideoIds() == null || request.seenVideoIds().isEmpty())
                ? List.of(-1L)
                : request.seenVideoIds();

        double radiusMeters = request.radiusKm() * 1000;

        // 3. Fetch one extra row to detect whether more pages exist
        List<Video> rows;

        if (cursorTimestamp == null) {
            rows = videoRepository.findFeedVideosFirstPage(
                    request.latitude(),
                    request.longitude(),
                    radiusMeters,
                    seenIds,
                    PAGE_SIZE + 1
            );
        } else {
            rows = videoRepository.findFeedVideosWithCursor(
                    request.latitude(),
                    request.longitude(),
                    radiusMeters,
                    cursorTimestamp,
                    seenIds,
                    PAGE_SIZE + 1
            );
        }

        // 4. Determine hasMore and trim to real page size
        boolean hasMore = rows.size() > PAGE_SIZE;
        List<Video> page = hasMore ? rows.subList(0, PAGE_SIZE) : rows;

        // 5. Build next cursor from the LAST video's created_at
        //    (immutable field → stable anchor even if new videos are uploaded)
        String nextCursor = null;
        if (hasMore && !page.isEmpty()) {
            nextCursor = encodeCursor(page.get(page.size() - 1).getCreatedAt());
        }

        // 6. Map entities → response DTOs and return
        List<VideoResponse> videos = page.stream()
                .map(videoMapper::toResponse)
                .toList();

        return new VideoFeedResponse(videos, nextCursor, hasMore);
    }

    /**
     * Encode: timestamp → Base64 string.
     * Base64 just makes the cursor opaque to the client —
     * they can't tamper with the timestamp directly.
     */
    private String encodeCursor(LocalDateTime timestamp) {
        return Base64.getEncoder()
                .encodeToString(timestamp.toString().getBytes());
    }

    /**
     * Decode: Base64 string → timestamp.
     * Returns null when cursor is absent (first page).
     */
    private LocalDateTime decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) return null;
        String raw = new String(Base64.getDecoder().decode(cursor));
        return LocalDateTime.parse(raw);
    }
}