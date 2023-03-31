package com.example.petadoptionservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.petadoptionservice.model.AdoptionStatus;
import com.example.petadoptionservice.model.AnimalCategory;
import com.example.petadoptionservice.model.entity.Animal;
import com.example.petadoptionservice.repository.AnimalRepository;
import com.example.petadoptionservice.service.AnimalService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@ExtendWith(MockitoExtension.class)
public class AnimalServiceTest {

  @InjectMocks
  private AnimalService animalService;

  @Mock
  private AnimalRepository animalRepository;

  @Mock
  private MongoTemplate mongoTemplate;

  private final EasyRandom easyRandom = new EasyRandom();

  @Test
  public void testIndexAnimal() {
    Animal inputAnimal = easyRandom.nextObject(Animal.class);
    Animal savedAnimal = easyRandom.nextObject(Animal.class);

    when(animalRepository.save(any(Animal.class))).thenReturn(savedAnimal);

    Animal result = animalService.indexAnimal(inputAnimal.getName(), inputAnimal.getDescription(),
        inputAnimal.getImageId(), inputAnimal.getCategory(), inputAnimal.getStatus(),
        inputAnimal.getCreationDate());

    verify(animalRepository, times(1)).save(any(Animal.class));
    assertEquals(savedAnimal, result);
  }


  @Test
  public void testUpdateStatus() {
    String animalId = UUID.randomUUID().toString();
    AdoptionStatus newStatus = AdoptionStatus.ADOPTED;
    Animal existingAnimal = easyRandom.nextObject(Animal.class);
    Animal updatedAnimal = new Animal(existingAnimal.getId(), existingAnimal.getName(),
        existingAnimal.getDescription(), existingAnimal.getImageId(), existingAnimal.getCategory(),
        newStatus, existingAnimal.getCreationDate());

    when(animalRepository.findById(animalId)).thenReturn(Optional.of(existingAnimal));
    when(animalRepository.save(existingAnimal)).thenReturn(updatedAnimal);

    Animal result = animalService.updateStatus(animalId, newStatus);

    verify(animalRepository, times(1)).findById(animalId);
    verify(animalRepository, times(1)).save(existingAnimal);
    assertEquals(updatedAnimal, result);
  }

  @Test
  public void testSearchAnimals() {
    List<Animal> animalList = easyRandom.objects(Animal.class, 5).collect(Collectors.toList());
    Pageable pageable = PageRequest.of(0, 5);

    String term = "test";
    AnimalCategory category = AnimalCategory.DOG;
    AdoptionStatus status = AdoptionStatus.AVAILABLE;
    LocalDate date = LocalDate.now();

    Query query = new Query();

    Criteria termCriteria = new Criteria().orOperator(
        Criteria.where("name").is(term),
        Criteria.where("description").regex(term, "i")
    );
    query.addCriteria(termCriteria);
    query.addCriteria(Criteria.where("category").is(category));
    query.addCriteria(Criteria.where("status").is(status));
    query.addCriteria(Criteria.where("date").is(date));
    query.with(pageable);

    when(mongoTemplate.find(query, Animal.class)).thenReturn(animalList);

    List<Animal> result = animalService.searchAnimals(term, category, status, date, pageable);

    verify(mongoTemplate, times(1)).find(query, Animal.class);
    assertEquals(animalList, result);
  }

  @Test
  public void testSearchAnimalsPaged() {
    List<Animal> animalList = easyRandom.objects(Animal.class, 5).collect(Collectors.toList());
    Pageable pageable = PageRequest.of(0, 5);
    Page<Animal> animalPage = new PageImpl<>(animalList, pageable, animalList.size());

    String term = "test";
    AnimalCategory category = AnimalCategory.DOG;
    AdoptionStatus status = AdoptionStatus.AVAILABLE;
    LocalDate date = LocalDate.now();

    Query query = new Query();
    Criteria termCriteria = new Criteria().orOperator(
        Criteria.where("name").is(term),
        Criteria.where("description").regex(term, "i")
    );
    query.addCriteria(termCriteria);
    query.addCriteria(Criteria.where("category").is(category));
    query.addCriteria(Criteria.where("status").is(status));
    query.addCriteria(Criteria.where("date").is(date));
    query.with(pageable);

    when(mongoTemplate.find(query, Animal.class)).thenReturn(animalList);

    Page<Animal> result = animalService.searchAnimalsPaged(term, category, status, date, pageable);

    verify(mongoTemplate, times(1)).find(query, Animal.class);
    assertEquals(animalPage, result);
  }

}
