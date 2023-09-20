package backend.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


@RestController
public class FileController {

    private static final Logger log = LogManager.getLogger(FileController.class);

    /* private final Path fileLocation = Paths.get("G:\\Mi unidad\\Trabajo\\PlataformaNCjava11\\media-files"); */
    private final Path fileLocation = Paths.get("/media-files");

    @GetMapping("/media/{folder}/{filename:.+}")
    public ResponseEntity<?> serveFile(@PathVariable String folder, @PathVariable String filename, @RequestParam(required = true) String downloadName) { 
        filename = URLDecoder.decode(filename, StandardCharsets.UTF_8); // decode the filename 
        
        Path filePath = fileLocation.resolve(folder).resolve(filename).normalize();

        log.info("---- Attempting to serve: " + filePath.toString());
        
        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().body("Error retrieving file: " + e.getMessage());
        }

        String contentType;
        try {
            contentType = Files.probeContentType(filePath);
        } catch (IOException e) {
            contentType = "application/octet-stream"; // default to binary if unable to determine
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadName + "\"")
            .body(resource);
    }

}


