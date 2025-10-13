package be.heh.stragram.application.port.out;

import java.io.InputStream;

public interface ImageStoragePort {
    
    String store(InputStream fileContent, String originalFilename, String contentType);
    
    void delete(String imagePath);
    
    String getImageUrl(String imagePath);
}
