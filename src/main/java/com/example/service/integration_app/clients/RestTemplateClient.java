package com.example.service.integration_app.clients;
import com.example.service.integration_app.model.EntityModel;
import com.example.service.integration_app.model.UpsertEntityRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RestTemplateClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.integration.base-url}")
    private String baseUrl;

    public void uploadFile(MultipartFile file){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        restTemplate.postForObject(baseUrl+"/api/v1/file/upload", requestEntity, String.class);
    }

    @SneakyThrows
    public void downloadFile(String fileName){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        ResponseEntity<Resource> response = restTemplate.exchange(
                baseUrl + "/api/v1/file/download/{fileName}",
                 HttpMethod.GET,
                 new HttpEntity<>(headers),
                 Resource.class,
                 fileName
        );
        Path dir = Paths.get("download");
        Files.createDirectories(dir);
        Path filePath = dir.resolve(fileName);
        Files.write(filePath, response.getBody().getContentAsByteArray());
        Files.copy(
                response.getBody().getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    public List<EntityModel> getEntityList(){
        ResponseEntity<List<EntityModel>> response = restTemplate.exchange(
                baseUrl + "/api/v1/entity",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<EntityModel>>() {}
        );
        return response.getBody();
    }

    public EntityModel getEntityByName(String name){
        ResponseEntity<EntityModel> response = restTemplate.exchange(
                baseUrl + "/api/v1/entity/{name}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                EntityModel.class,
                name
        );
        return response.getBody();
    }

    public EntityModel createEntity(UpsertEntityRequest request){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(
                baseUrl + "/api/v1/entity",
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                EntityModel.class
        ).getBody();
    }

    public EntityModel updateEntity(UUID id, UpsertEntityRequest request){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(
                baseUrl + "/api/v1/entity/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(request, headers),
                EntityModel.class,
                id
        ).getBody();
    }

    public void deleteEntity(UUID id){
        restTemplate.exchange(
                baseUrl + "/api/v1/entity/{id}",
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class,
                id
        );
    }
}