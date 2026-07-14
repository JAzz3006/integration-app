package com.example.service.integration_app.controller;
import com.example.service.integration_app.clients.OkHttpClientSender;
import com.example.service.integration_app.clients.OpenFeignClient;
import com.example.service.integration_app.clients.RestTemplateClient;
import com.example.service.integration_app.clients.WebClientSender;
import com.example.service.integration_app.entity.DataBaseEntity;
import com.example.service.integration_app.model.EntityModel;
import com.example.service.integration_app.model.UpsertEntityRequest;
import com.example.service.integration_app.service.DataBaseEntityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/client/entity")
public class EntityClientController {

    //private final OkHttpClientSender client;
//    private final RestTemplateClient client;
//    private final ObjectMapper objectMapper;
    //private final WebClientSender client;
    private final OpenFeignClient client;
    private final DataBaseEntityService service;

    @GetMapping
    ResponseEntity<List<EntityModel>> entityList(){
//        return ResponseEntity.ok(
//                client.getEntityList()
//        );
        return ResponseEntity.ok(
                service.findAll()
                        .stream()
                        .map(dbe -> EntityModel.from(dbe))
                        .toList()
        );
    }

    @GetMapping("/{name}")
    ResponseEntity<EntityModel> entityByName(@PathVariable String name){
//        return ResponseEntity.ok(
//                client.getEntityByName(name)
//        );
        return ResponseEntity.ok(
                EntityModel.from(service.findByName(name))
        );
    }

    @PostMapping
    ResponseEntity<EntityModel> createEntity(@RequestBody UpsertEntityRequest request){
//        return ResponseEntity.status(HttpStatus.CREATED).body(
//                client.createEntity(request)
//        );
        EntityModel newEntity = client.createEntity(request);
        DataBaseEntity savedEntity = DataBaseEntity.from(newEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                EntityModel.from(
                        service.create(savedEntity)
                )
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<EntityModel> updateEntity(
            @PathVariable UUID id,
            @RequestBody UpsertEntityRequest request
    ){
        EntityModel updatedEntityModel = client.updateEntity(id, request);
        DataBaseEntity updatedDBEntity = service.update(id, DataBaseEntity.from(updatedEntityModel));
        return ResponseEntity.ok(
                EntityModel.from(updatedDBEntity)
        );
//        return ResponseEntity.ok(
//                client.updateEntity(id, request)
//        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteEntity(@PathVariable UUID id){
//        client.deleteEntity(id);
        service.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}