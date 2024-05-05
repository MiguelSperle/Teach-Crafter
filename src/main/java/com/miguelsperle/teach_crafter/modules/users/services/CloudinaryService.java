package com.miguelsperle.teach_crafter.modules.users.services;

import com.cloudinary.Cloudinary;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {
    @Resource
    private Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String folderName) {
        try {
            HashMap<Object, Object> options = new HashMap<>();
            options.put("folder", folderName);

            byte[] fileBytes = file.getBytes();
            Map uploadedFile = cloudinary.uploader().upload(fileBytes, options);

            String publicId = (String) uploadedFile.get("public_id");
            return cloudinary.url().secure(true).generate(publicId);
        } catch (IOException exception) {
            throw new RuntimeException("Error while upload a file", exception);
        }
    }
}
