package com.kgjr.si_moto_backend_exp.database_tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "videos")
@Getter
@Setter
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filePath;

    @Column(name = "company_id", nullable = false)
    private String companyId;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;

    private String country;
    private String state;

    private String fileExtension;

    @ElementCollection
    @CollectionTable(name = "video_tags", joinColumns = @JoinColumn(name = "video_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}