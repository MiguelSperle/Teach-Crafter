package com.miguelsperle.teach_crafter.modules.users.dtos.cloudinary;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UploadImageModelDTO(
        @NotNull(message = "An image is required")
        MultipartFile imageFile
) {
}