package com.example.demo.sping.boot.util.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.sping.boot.util.entity.ContentEntity;

@Repository
public interface ContentRepository extends MongoRepository<ContentEntity, String> {
    ContentEntity findByContentUuid(String contentUuid);
}