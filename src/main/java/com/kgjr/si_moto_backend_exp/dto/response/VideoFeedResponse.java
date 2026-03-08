package com.kgjr.si_moto_backend_exp.dto.response;

import java.util.List;

public record VideoFeedResponse(
        List<VideoResponse> videos,
        String nextCursor,        // null = feed exhausted
        boolean hasMore
) {}