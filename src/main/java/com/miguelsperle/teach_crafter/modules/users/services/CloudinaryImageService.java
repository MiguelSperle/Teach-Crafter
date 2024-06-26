package com.miguelsperle.teach_crafter.modules.users.services;

import com.cloudinary.Cloudinary;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;

@Service
public class CloudinaryImageService {
    @Resource
    private Cloudinary cloudinary;

    public String uploadImageFile(MultipartFile file, String folderName) {
        try {
            HashMap<Object, Object> options = new HashMap<>();
            options.put("folder", folderName);
            options.put("resource_type", "image");

            byte[] fileBytes = file.getBytes();
            var uploadedFile = this.cloudinary.uploader().upload(fileBytes, options);

            String publicId = (String) uploadedFile.get("public_id");
            return this.cloudinary.url().resourceType("image").secure(true).generate(publicId);
        } catch (IOException exception) {
            throw new RuntimeException("Error while retrieving bytes for image upload", exception);
        }
    }
}
