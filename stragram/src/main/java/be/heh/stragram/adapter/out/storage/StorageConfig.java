package be.heh.stragram.adapter.out.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StorageConfig implements WebMvcConfigurer {

    @Value("${storage.location}")
    private String storageLocation;

    @Bean
    public Path storageDirectoryPath() throws IOException {
        Path path = Paths.get(storageLocation);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return path;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + storageLocation + "/");
    }
}
