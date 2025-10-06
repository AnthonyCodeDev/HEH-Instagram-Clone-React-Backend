package com.example.backendstragram.domain.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final Cloudinary cloudinary;

    /**
     * Upload un fichier vers Cloudinary et retourne l'URL de l'image
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // Générer un nom unique pour éviter les collisions
        String publicId = UUID.randomUUID().toString();

        Map<?, ?> uploadResult = cloudinary.uploader().upload(
            file.getBytes(),
            ObjectUtils.asMap(
                "public_id", publicId,
                "folder", "stragram",
                "resource_type", "auto"
            )
        );

        return (String) uploadResult.get("secure_url");
    }

    /**
     * Supprime un fichier de Cloudinary en utilisant son URL
     */
    public void deleteFile(String imageUrl) throws IOException {
        // Extraire le public_id de l'URL
        if (imageUrl != null && imageUrl.contains("/stragram/")) {
            String[] parts = imageUrl.split("/stragram/");
            if (parts.length > 1) {
                String publicId = "stragram/" + parts[1].split("\\.")[0];

                cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.emptyMap()
                );
            }
        }
    }
}
