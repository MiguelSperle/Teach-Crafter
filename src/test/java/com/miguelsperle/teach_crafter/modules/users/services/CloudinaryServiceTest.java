package com.miguelsperle.teach_crafter.modules.users.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.Url;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CloudinaryServiceTest {
    @InjectMocks
    private CloudinaryService cloudinaryService;

    @Mock
    private Cloudinary cloudinary;

    private Uploader mockUploader;
    private Url mockUrl;


    private void configureCloudinaryMocksForTest() {
        this.mockUploader = mock(Uploader.class);
        this.mockUrl = mock(Url.class);

        when(this.cloudinary.uploader()).thenReturn(mockUploader);
        when(this.cloudinary.url()).thenReturn(mockUrl);
        when(this.mockUrl.secure(true)).thenReturn(mockUrl);
    }

    @Test
    @DisplayName("Should be able to upload an image to cloud on cloudinary service")
    public void should_be_able_to_upload_image_to_cloud_on_cloudinary_service() throws IOException {
        this.configureCloudinaryMocksForTest();

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getBytes()).thenReturn("test-file-content".getBytes());

        Map<String, Object> uploadedFileResponse = new HashMap<>();
        uploadedFileResponse.put("public_id", "8Bs90kDnE3vz2X1rWj4T");
        // We generate a random public_id

        when(this.mockUploader.upload(any(), any())).thenReturn(uploadedFileResponse);

        String expectedUrl = "https://test-url/profile_pics/8Bs90kDnE3vz2X1rWj4T";
        when(this.mockUrl.generate("8Bs90kDnE3vz2X1rWj4T")).thenReturn(expectedUrl);

        String resultUrl = this.cloudinaryService.uploadFile(mockFile, "profile_pics");

        assertNotNull(resultUrl);
        assertEquals(expectedUrl, resultUrl);
        // First argument is what I expect
        // Second argument is the real value obtained

        verify(this.mockUploader).upload(eq("test-file-content".getBytes()), argThat(map -> "profile_pics".equals(map.get("folder"))));
        // Verify if the method upload was called with specifics arguments ( if it has a key = folder with value = profile_pics )
    }

    @Test
    @DisplayName("Should be able to throw an exception when occurs an error to upload an image")
    public void should_be_able_to_throw_an_exception() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getBytes()).thenThrow(new IOException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            this.cloudinaryService.uploadFile(mockFile, "profile_pics");
        });

        String resultMessage = "Error while uploading a file";

        assertEquals(resultMessage, exception.getMessage());
        // First argument is what I expect
        // Second argument is the real value obtained
    }
}
