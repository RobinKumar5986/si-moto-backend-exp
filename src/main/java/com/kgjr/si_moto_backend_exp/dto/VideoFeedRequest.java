package com.kgjr.si_moto_backend_exp.dto;

import java.util.List;

public record VideoFeedRequest(
        double latitude,
        double longitude,
        double radiusKm,          // e.g. 50.0 → search within 50km
        String cursor,            // null on first call
        List<Long> seenVideoIds   // IDs the client has already seen
) {}