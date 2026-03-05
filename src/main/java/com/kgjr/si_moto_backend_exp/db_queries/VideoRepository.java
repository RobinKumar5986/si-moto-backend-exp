package com.kgjr.si_moto_backend_exp.db_queries;

import com.kgjr.si_moto_backend_exp.database_tables.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    //here we have bunch of auto implemented methods
}