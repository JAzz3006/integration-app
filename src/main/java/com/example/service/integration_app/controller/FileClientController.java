package com.example.service.integration_app.controller;
import com.example.service.integration_app.clients.OkHttpClientSender;
import com.example.service.integration_app.clients.RestTemplateClient;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/client/file")
public class FileClientController {

    //private final OkHttpClientSender client;
    private final RestTemplateClient client;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestPart MultipartFile file){
        client.uploadFile(file);
//        return ResponseEntity.ok("File uploaded successfully"); - вариант спикера
        //String response = client.uploadFile(file);
        return ResponseEntity.ok("File uploaded successfully");
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<String> downloadFile(@PathVariable String fileName){
        client.downloadFile(fileName);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.setContentType(MediaType.TEXT_PLAIN);
        return ResponseEntity.ok()
                .headers(headers)
                .body(String.format("File %s downloaded successfully", fileName));
    }
}