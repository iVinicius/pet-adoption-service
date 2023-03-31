package com.example.petadoptionservice.service;

import com.example.petadoptionservice.model.AdoptionStatus;
import com.example.petadoptionservice.model.AnimalCategory;
import com.example.petadoptionservice.model.entity.Animal;
import com.example.petadoptionservice.repository.AnimalRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnimalService {
  private final AnimalRepository animalRepository;
  private final MongoTemplate mongoTemplate;

  public Animal indexAnimal(String name, String description, String imageId,
                            AnimalCategory category, AdoptionStatus status,
                            LocalDate creationDate) {
    return animalRepository.save(
        Animal.builder()
            .name(name)
            .category(category)
            .creationDate(creationDate)
            .description(description)
            .imageId(imageId)
            .status(status)
            .build()
    );
  }

  public Animal updateStatus(String animalId, AdoptionStatus status){
    Animal byId = animalRepository.findById(animalId)
        .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Animal doesnt exists"));

    byId.setStatus(status);

    return animalRepository.save(byId);
  }

  public Page<Animal> searchAnimalsPaged(String term, AnimalCategory category,
                                    AdoptionStatus status, LocalDate date, Pageable pageable) {
    List<Animal> animals = this.searchAnimals(term, category, status, date, pageable);

    return new PageImpl<>(animals, pageable, animals.size());
  }

  public List<Animal> searchAnimals(String term, AnimalCategory category,
                                    AdoptionStatus status, LocalDate date, Pageable pageable) {
    Query query = new Query();

    if (term != null && !term.isEmpty()) {
      Criteria termCriteria = new Criteria().orOperator(
          Criteria.where("name").is(term),
          Criteria.where("description").regex(term, "i")
      );
      query.addCriteria(termCriteria);
    }

    if (category != null) {
      query.addCriteria(Criteria.where("category").is(category));
    }

    // Add status filter if present
    if (status != null) {
      query.addCriteria(Criteria.where("status").is(status));
    }

    if (date != null) {
      query.addCriteria(Criteria.where("date").is(date));
    }

    if(pageable != null) {
      query.with(pageable);
    }

    return mongoTemplate.find(query, Animal.class);
  }

}
