package com.example.petadoptionservice.integration;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.petadoptionservice.api.AnimalController;
import com.example.petadoptionservice.api.request.IndexAnimalRequest;
import com.example.petadoptionservice.model.AdoptionStatus;
import com.example.petadoptionservice.model.AnimalCategory;
import com.example.petadoptionservice.model.entity.Animal;
import com.example.petadoptionservice.service.AnimalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class AnimalControllerIT {
  @Mock
  private AnimalService animalService;

  @InjectMocks
  private AnimalController animalController;

  private MockMvc mockMvc;

  private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(animalController)
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
  }

  @Test
  public void testIndexAnimal() throws Exception {
    IndexAnimalRequest request = new IndexAnimalRequest("Test", "Test Description",
        UUID.randomUUID().toString(), AnimalCategory.DOG,
        AdoptionStatus.AVAILABLE, LocalDate.now());

    when(animalService.indexAnimal(request.getName(), request.getDescription(),
        request.getImageId(), request.getCategory(), request.getStatus(),
        request.getCreationDate())
    ).thenReturn(Animal.builder().id("xx").build());

    mockMvc.perform(post("/api/animal")
            .contentType("application/json")
            .content(mapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated())
        .andExpect(content().string("xx"));

    verify(animalService, times(1))
        .indexAnimal(request.getName(), request.getDescription(),
            request.getImageId(), request.getCategory(),
            request.getStatus(), request.getCreationDate());
  }

  @Test
  public void testSearchAnimals() throws Exception {
    String term = "Test";
    String category = "DOG";
    String status = "AVAILABLE";
    String date = "1996-03-21";
    String imageId = UUID.randomUUID().toString();

    Page<Animal> page = new PageImpl<>(
        Arrays.asList(new Animal(null, term, "Test Description",
            imageId, AnimalCategory.valueOf(category),
            AdoptionStatus.valueOf(status), LocalDate.parse(date))),
        PageRequest.of(0, 10), 1);

    when(animalService.searchAnimalsPaged(anyString(), any(AnimalCategory.class),
        any(AdoptionStatus.class), any(LocalDate.class), any(Pageable.class))
    ).thenReturn(page);

    mockMvc.perform(get("/api/animal")
            .param("term", term)
            .param("category", category)
            .param("status", status)
            .param("date", date))
        .andExpect(status().isOk())
        .andExpect(content().json(
            "{\"content\":[{\"name\":\"Test\"," +
                "\"description\":\"Test Description\"," +
                "\"imageId\":\"" + imageId + "\"" +
                ",\"category\":\"DOG\",\"status\":\"AVAILABLE\"," +
                "\"creationDate\":\"" + date + "\"}]" +
                ",\"totalElements\":1,\"totalPages\":1,\"size\":10,\"number\":0,\"first\":true" +
                ",\"last\":true,\"empty\":false}"));

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(animalService, times(1)).searchAnimalsPaged(eq(term), eq(AnimalCategory.valueOf(category)), eq(AdoptionStatus.valueOf(status)), eq(LocalDate.parse(date)), pageableCaptor.capture());
  }

  @Test
  public void testUpdateAdoptionStatus() throws Exception {
    String animalId = UUID.randomUUID().toString();
    AdoptionStatus newStatus = AdoptionStatus.ADOPTED;
    String date = "1996-03-21";
    Animal updatedAnimal = new Animal(animalId, "Test", "Test Description", UUID.randomUUID().toString(), AnimalCategory.DOG, newStatus, LocalDate.parse(date));

    when(animalService.updateStatus(animalId, newStatus))
        .thenReturn(updatedAnimal);

    mockMvc.perform(put("/api/animal/{id}/status", animalId)
            .param("value", newStatus.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", Matchers.is(animalId)))
        .andExpect(jsonPath("$.name", Matchers.is("Test")))
        .andExpect(jsonPath("$.description", Matchers.is("Test Description")))
        .andExpect(jsonPath("$.imageId", Matchers.is(updatedAnimal.getImageId())))
        .andExpect(jsonPath("$.category", Matchers.is("DOG")))
        .andExpect(jsonPath("$.status", Matchers.is("ADOPTED")));

    verify(animalService, times(1)).updateStatus(anyString(), any(AdoptionStatus.class));
  }

}
