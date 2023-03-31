package com.example.petadoptionservice.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.petadoptionservice.model.entity.Animal;
import com.example.petadoptionservice.repository.AnimalRepository;
import java.util.Optional;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@DataMongoTest
public class AnimalRepositoryIT {

  @Autowired
  private AnimalRepository repository;
  @Autowired
  private MongoTemplate mongoTemplate;

  private Animal animal;

  EasyRandom ezRandom = new EasyRandom();

  @BeforeEach
  public void setUp() {
    animal = ezRandom.nextObject(Animal.class);
    mongoTemplate.save(animal);
  }

  @AfterEach
  public void tearDown() {
    mongoTemplate.dropCollection(Animal.class);
  }

  @Test
  public void testFindById() {
    Optional<Animal> foundEntity = repository.findById(animal.getId());
    assertThat(foundEntity).isPresent();
    assertThat(foundEntity.get()).isEqualTo(animal);
  }

  @Test
  public void testSave() {
    Animal newEntity = ezRandom.nextObject(Animal.class);
    newEntity.setId("humm");
    Animal savedEntity = repository.save(newEntity);

    assertThat(savedEntity).isNotNull();
    assertThat(savedEntity.getId()).isEqualTo("humm");
  }
}
