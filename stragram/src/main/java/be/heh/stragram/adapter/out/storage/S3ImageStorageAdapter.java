package be.heh.stragram.adapter.out.storage;

import be.heh.stragram.application.port.out.ImageStoragePort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.UUID;

/**
 * S3 implementation of the ImageStoragePort.
 * This is a placeholder implementation that would be completed when AWS S3 integration is needed.
 */
@Component
@Profile("s3")
public class S3ImageStorageAdapter implements ImageStoragePort {

    @Override
    public String store(InputStream fileContent, String originalFilename, String contentType) {
        // This would be implemented with AWS SDK to upload to S3
        String fileExtension = StringUtils.getFilenameExtension(originalFilename);
        String filename = UUID.randomUUID() + "." + fileExtension;
        
        // Placeholder for S3 upload logic
        
        return filename;
    }

    @Override
    public void delete(String imagePath) {
        // This would be implemented with AWS SDK to delete from S3
    }

    @Override
    public String getImageUrl(String imagePath) {
        // This would return the S3 URL for the image
        return "https://your-s3-bucket.s3.amazonaws.com/" + imagePath;
    }
}
