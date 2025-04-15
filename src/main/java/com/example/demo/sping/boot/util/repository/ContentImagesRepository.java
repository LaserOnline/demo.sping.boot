package com.example.demo.sping.boot.util.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.sping.boot.util.entity.ContentImages;

@Repository
public interface ContentImagesRepository extends MongoRepository<ContentImages, String> {
    List<ContentImages> findByContentUuid(String contentUuid);
}
