package com.example.petadoptionservice.repository;

import com.example.petadoptionservice.model.entity.Animal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalRepository extends MongoRepository<Animal, String> {
}
