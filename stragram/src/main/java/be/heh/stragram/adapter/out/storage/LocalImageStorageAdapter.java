package be.heh.stragram.adapter.out.storage;

import be.heh.stragram.application.port.out.ImageStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class LocalImageStorageAdapter implements ImageStoragePort {

    private final Path storageLocation;
    private final String baseUrl;

    public LocalImageStorageAdapter(
            @Value("${storage.location}") String storageLocation,
            @Value("${server.port}") String serverPort) {
        this.storageLocation = Paths.get(storageLocation).toAbsolutePath().normalize();
        this.baseUrl = "http://localhost:" + serverPort + "/images/";
        
        try {
            Files.createDirectories(this.storageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    @Override
    public String store(InputStream fileContent, String originalFilename, String contentType) {
        try {
            String fileExtension = StringUtils.getFilenameExtension(originalFilename);
            String filename = UUID.randomUUID() + "." + fileExtension;
            Path targetLocation = this.storageLocation.resolve(filename);
            
            Files.copy(fileContent, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public void delete(String imagePath) {
        try {
            Path targetLocation = this.storageLocation.resolve(imagePath);
            Files.deleteIfExists(targetLocation);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    @Override
    public String getImageUrl(String imagePath) {
        return baseUrl + imagePath;
    }
}
