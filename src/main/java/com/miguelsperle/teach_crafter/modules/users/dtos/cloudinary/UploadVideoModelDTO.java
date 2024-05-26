package com.miguelsperle.teach_crafter.modules.users.dtos.cloudinary;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UploadVideoModelDTO(
        @NotNull(message = "A video is required")
        MultipartFile videoFile
) {
}
