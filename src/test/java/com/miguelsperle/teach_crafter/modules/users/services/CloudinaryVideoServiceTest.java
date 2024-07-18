package com.miguelsperle.teach_crafter.modules.users.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.Url;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CloudinaryVideoServiceTest {
    @InjectMocks
    private CloudinaryVideoService cloudinaryVideoService;

    @Mock
    private Cloudinary cloudinary;

    private MultipartFile mockFile;

    @BeforeEach
    public void setUp() {
        this.mockFile = mock(MultipartFile.class);
    }

    @Test
    @DisplayName("Should be able to upload a video to cloud on cloudinary service")
    public void should_be_able_to_upload_a_video_to_cloud_on_cloudinary_service() throws IOException {
        Uploader mockUploader = mock(Uploader.class);
        Url mockUrl = mock(Url.class);

        when(this.mockFile.getBytes()).thenReturn("test-video-file-content".getBytes());

        when(this.cloudinary.uploader()).thenReturn(mockUploader);

        HashMap<String, Object> uploadedFileResponse = new HashMap<>();
        uploadedFileResponse.put("public_id", "m7Nq5Ls2VcH9A4X8BwK1");
        // We generate a random public_id

        when(this.cloudinary.uploader().upload(any(), any())).thenReturn(uploadedFileResponse);

        when(this.cloudinary.url()).thenReturn(mockUrl);

        when(this.cloudinary.url().transformation(any())).thenReturn(mockUrl);

        when(this.cloudinary.url().resourceType(any())).thenReturn(mockUrl);

        when(this.cloudinary.url().format(any())).thenReturn(mockUrl);

        when(this.cloudinary.url().secure(anyBoolean())).thenReturn(mockUrl);

        String expectedUrl = "https://test-url/course_videos/m7Nq5Ls2VcH9A4X8BwK1";
        when(this.cloudinary.url().generate((String) uploadedFileResponse.get("public_id"))).thenReturn(expectedUrl);

        String expectedJson = "{" +
                "\"1080p\": \"https://test-url/course_videos/m7Nq5Ls2VcH9A4X8BwK1\", " +
                "\"720p\": \"https://test-url/course_videos/m7Nq5Ls2VcH9A4X8BwK1\", " +
                "\"360p\": \"https://test-url/course_videos/m7Nq5Ls2VcH9A4X8BwK1\", " +
                "\"480p\": \"https://test-url/course_videos/m7Nq5Ls2VcH9A4X8BwK1\"" +
                "}";

        String resultUrl = this.cloudinaryVideoService.uploadVideoFile(this.mockFile, "course_videos");

        assertNotNull(resultUrl);
        assertEquals(expectedJson, resultUrl);
        // First argument is what I expect
        // Second argument is the real value obtained

        verify(this.cloudinary.uploader(), atLeastOnce()).upload(eq("test-video-file-content".getBytes()), argThat(map -> "course_videos".equals(map.get("folder"))));
        // Verify if the method upload was called with specifics arguments ( if it has a key = folder with value = course_videos )
    }

    @Test
    @DisplayName("Should throw an exception when a byte retrieval error occurs during video upload")
    public void should_throw_an_exception_when_a_byte_retrieval_error_occurs_during_video_upload() throws IOException {
        when(this.mockFile.getBytes()).thenThrow(IOException.class);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            this.cloudinaryVideoService.uploadVideoFile(this.mockFile, "course_videos");
        });

        String expectedErrorMessage = "Error while retrieving bytes for video upload";

        // Verify if the cause is kind of IOException.class
        assertInstanceOf(IOException.class, exception.getCause());

        assertEquals(expectedErrorMessage, exception.getMessage());
        // First argument is what I expect
        // Second argument is the real value obtained
    }
}
