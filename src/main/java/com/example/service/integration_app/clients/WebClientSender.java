package com.example.service.integration_app.clients;
import com.example.service.integration_app.model.EntityModel;
import com.example.service.integration_app.model.UpsertEntityRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WebClientSender {

    private final WebClient webClient;

    public void uploadFile(MultipartFile file){
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file.getResource())
                .filename(file.getOriginalFilename())
                .contentType(MediaType.APPLICATION_OCTET_STREAM);
        webClient.post()
                .uri("/api/v1/file/upload")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @SneakyThrows
    public void downloadFile(String fileName){
        Resource resource = webClient.get()
                .uri("/api/v1/file/download/{fileName}", fileName)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(Resource.class)
                .block();
        if (resource == null){
            throw new IllegalStateException("Response is null");
        }
        Path dir = Paths.get("download");
        Files.createDirectories(dir);
        Path filePath = dir.resolve(fileName);
        Files.copy(
               resource.getInputStream(),
               filePath,
               StandardCopyOption.REPLACE_EXISTING
        );
    }

    public List<EntityModel> getEntityList(){
        return webClient.get()
                .uri("/api/v1/entity")
                .retrieve()
                .bodyToFlux(EntityModel.class)
                .collectList()
                .block();
    }

    public EntityModel getEntityByName(String name){
        return webClient.get()
                .uri("/api/v1/entity/{name}", name)
                .retrieve()
                .bodyToMono(EntityModel.class)
                .block();
    }

    public EntityModel createEntity(UpsertEntityRequest request){
        return webClient.post()
                .uri("/api/v1/entity")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(EntityModel.class)
                .block();
    }

    public EntityModel updateEntity(UUID id, UpsertEntityRequest request){
        return webClient.put()
                .uri("/api/v1/entity/{id}", id)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(EntityModel.class)
                .block();
    }

    public void deleteEntity(UUID id){
        webClient.delete()
                .uri("/api/v1/entity/{id}")
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}