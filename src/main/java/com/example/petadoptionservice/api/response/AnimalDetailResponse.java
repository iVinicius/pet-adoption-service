package com.example.petadoptionservice.api.response;

import com.example.petadoptionservice.model.AdoptionStatus;
import com.example.petadoptionservice.model.AnimalCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnimalDetailResponse {
  private String name;
  private String description;
  private String imageId;
  private AnimalCategory category;
  private AdoptionStatus status;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate creationDate;
}
