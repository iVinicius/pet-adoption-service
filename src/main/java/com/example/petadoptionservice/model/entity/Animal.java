package com.example.petadoptionservice.model.entity;

import com.example.petadoptionservice.model.AdoptionStatus;
import com.example.petadoptionservice.model.AnimalCategory;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@AllArgsConstructor
@Data
@Builder
@Document
public class Animal {
  @MongoId
  private String id;
  private String name;
  private String description;
  private String imageId;
  private AnimalCategory category;
  private AdoptionStatus status;
  private LocalDate creationDate;
}
