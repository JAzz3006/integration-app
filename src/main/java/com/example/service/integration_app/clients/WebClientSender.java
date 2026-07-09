package com.example.service.integration_app.clients;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

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
                .uri("/api/v1/file/{fileName}", fileName)
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
}