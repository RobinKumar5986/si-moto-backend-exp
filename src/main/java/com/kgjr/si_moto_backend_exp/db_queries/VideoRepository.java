package com.kgjr.si_moto_backend_exp.db_queries;

import com.kgjr.si_moto_backend_exp.database_tables.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    // First page — no cursor
    @Query(value = """
            SELECT v.*
            FROM videos v
            WHERE
                ST_DWithin(
                    v.location::geography,
                    ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
                    :radiusMeters
                )
                AND v.id NOT IN (:seenIds)
            ORDER BY
                ST_Distance(
                    v.location::geography,
                    ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography
                ) ASC,
                v.created_at DESC
            LIMIT :limit
            """,
            nativeQuery = true)
    List<Video> findFeedVideosFirstPage(
            @Param("lat")          double lat,
            @Param("lng")          double lng,
            @Param("radiusMeters") double radiusMeters,
            @Param("seenIds")      List<Long> seenIds,
            @Param("limit")        int limit
    );

    // Subsequent pages — with cursor
    @Query(value = """
            SELECT v.*
            FROM videos v
            WHERE
                ST_DWithin(
                    v.location::geography,
                    ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
                    :radiusMeters
                )
                AND v.created_at < :cursor
                AND v.id NOT IN (:seenIds)
            ORDER BY
                ST_Distance(
                    v.location::geography,
                    ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography
                ) ASC,
                v.created_at DESC
            LIMIT :limit
            """,
            nativeQuery = true)
    List<Video> findFeedVideosWithCursor(
            @Param("lat")          double lat,
            @Param("lng")          double lng,
            @Param("radiusMeters") double radiusMeters,
            @Param("cursor")       LocalDateTime cursor,
            @Param("seenIds")      List<Long> seenIds,
            @Param("limit")        int limit
    );
}