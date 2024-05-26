package com.miguelsperle.teach_crafter.modules.users.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryVideoService {
    @Resource
    private Cloudinary cloudinary;

    public String uploadVideoFile(MultipartFile file, String folderName) {
        try {
            HashMap<Object, Object> options = new HashMap<>();
            options.put("folder", folderName);
            options.put("resource_type", "video");

            byte[] fileBytes = file.getBytes();
            Map uploadedFile = this.cloudinary.uploader().upload(fileBytes, options);

            String publicId = (String) uploadedFile.get("public_id");

            Map<String, String> videoUrls = new HashMap<>(); // key and value

            videoUrls.put("1080p", this.cloudinary.url().transformation(new Transformation().width(1920).height(1080)
                    .crop("limit").quality(70)).resourceType("video").format("m3u8").secure(true).generate(publicId));

            videoUrls.put("720p", this.cloudinary.url().transformation(new Transformation().width(1280).height(720)
                    .crop("limit").quality(70)).resourceType("video").format("m3u8").secure(true).generate(publicId));

            videoUrls.put("480p", this.cloudinary.url().transformation(new Transformation().width(854).height(480)
                    .crop("limit").quality(70)).resourceType("video").format("m3u8").secure(true).generate(publicId));

            videoUrls.put("360p", this.cloudinary.url().transformation(new Transformation().width(480).height(360)
                    .crop("limit").quality(70)).resourceType("video").format("m3u8").secure(true).generate(publicId));

            // Convert url maps to string in JSON format
            StringBuilder urlsString = new StringBuilder("{");

            for (Map.Entry<String, String> entry : videoUrls.entrySet()) {
                urlsString.append("\"").append(entry.getKey()).append("\": \"").append(entry.getValue()).append("\", ");
            }

            // Remove the last comma and space, and close json
            if (urlsString.length() > 1) {
                urlsString.setLength(urlsString.length() - 2);
            }

            urlsString.append("}");

            return urlsString.toString();
        } catch (IOException exception) {
            throw new RuntimeException("Error while uploading a video file", exception);
        }
    }
}
// HLS is the most used, it is a streaming protocol for videos
// it loads only the part who user is watching, and we can change the videos quality whether the internet is bad
