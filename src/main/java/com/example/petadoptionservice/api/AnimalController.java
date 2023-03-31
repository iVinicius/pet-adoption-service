package com.example.petadoptionservice.api;

import com.example.petadoptionservice.api.request.IndexAnimalRequest;
import com.example.petadoptionservice.api.response.AnimalDetailResponse;
import com.example.petadoptionservice.model.AdoptionStatus;
import com.example.petadoptionservice.model.AnimalCategory;
import com.example.petadoptionservice.model.entity.Animal;
import com.example.petadoptionservice.service.AnimalService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/animal")
public class AnimalController {

  private final AnimalService animalService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public String indexAnimal(
      @RequestBody IndexAnimalRequest request) {

    return animalService.indexAnimal(request.getName(), request.getDescription(),
            request.getImageId(), request.getCategory(), request.getStatus(),
            request.getCreationDate()).getId();
  }

  @GetMapping
  public Page<AnimalDetailResponse> searchAnimals(
      @RequestParam(value = "term", required = false) String term,
      @RequestParam(value = "category", required = false) AnimalCategory category,
      @RequestParam(value = "status", required = false) AdoptionStatus status,
      @RequestParam(value = "date", required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      Pageable pageable) {

    return animalService.searchAnimalsPaged(term, category, status, date, pageable)
        .map(entity -> AnimalDetailResponse.builder()
            .name(entity.getName())
            .category(entity.getCategory())
            .creationDate(entity.getCreationDate())
            .description(entity.getDescription())
            .imageId(entity.getImageId())
            .status(entity.getStatus())
            .build());
  }

  @PutMapping("/{id}/status")
  public Animal updateAdoptionStatus(
      @PathVariable("id") String id,
      @RequestParam("value") AdoptionStatus status) {

    return animalService.updateStatus(id, status);
  }

}
