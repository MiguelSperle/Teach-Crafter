package com.miguelsperle.teach_crafter.modules.users.controllers;

import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.CoursesContentsEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.UsersRepository;
import com.miguelsperle.teach_crafter.utils.TokenGenerator;
import com.miguelsperle.teach_crafter.utils.integration.UsersUtils;
import com.miguelsperle.teach_crafter.utils.integration.configuration.H2CleanUpAndFakerExtension;
import com.miguelsperle.teach_crafter.utils.integration.configuration.interfaces.IntegrationTestSetup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

@IntegrationTestSetup
public class AuthenticationAndAuthorizationTest {
    @Value("${api.security.token.secret}")
    private String secret;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Should not be able to update user name if authorization token is missing in the header")
    public void should_not_be_able_to_update_user_name_if_authorization_token_is_missing_in_the_header() throws Exception {
        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/users/update-name"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to update user username if authorization token is missing in the header")
    public void should_not_be_able_to_update_user_username_if_authorization_token_is_missing_in_the_header() throws Exception {
        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/users/update-username"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to update user email if authorization token is missing in the header")
    public void should_not_be_able_to_update_user_email_if_authorization_token_is_missing_in_the_header() throws Exception {
        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/users/update-email"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to update loggedUser password if authorization token is missing in the header")
    public void should_not_be_able_to_update_loggedUser_password_if_authorization_token_is_missing_in_the_header() throws Exception {
        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/users/update-password"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to update user image if authorization token is missing in the header")
    public void should_not_be_able_to_update_user_image_if_authorization_token_is_missing_in_the_header() throws Exception {
        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, ("/users/update-image")));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to fetch authenticated user profile if authorization token is missing in the header")
    public void should_not_be_able_to_fetch_authenticated_user_profile_if_authorization_token_is_missing_in_the_header() throws Exception {
        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.get("/users/profile"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to subscribe in the course if authorization token is missing in the header")
    public void should_not_be_able_to_subscribe_in_the_course_if_authorization_token_is_missing_in_the_header() throws Exception {
        CoursesEntity course = new CoursesEntity();
        course.setId(String.valueOf(UUID.randomUUID()));

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/enrollment/" + course.getId() + "/create"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to unsubscribe from a course if authorization token is missing in the header")
    public void should_not_be_able_to_unsubscribe_from_a_course_if_authorization_token_is_missing_in_the_header() throws Exception {
        CoursesEntity course = new CoursesEntity();
        course.setId(String.valueOf(UUID.randomUUID()));

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.delete("/enrollment/" + course.getId() + "/delete"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to create a course if authorization token is missing in the header")
    public void should_not_be_able_to_create_a_course_if_authorization_token_is_missing_in_the_header() throws Exception {
        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/courses/create"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("should not be able to create a course because there is no permission to access the route even if an authorization token is provided")
    public void should_not_be_able_to_create_a_course_because_there_is_no_permission_to_access_the_route_even_if_an_authorization_token_is_provided() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/courses/create")
                .header("Authorization", token));

        String expectedMessage = "Access to this resource is restricted";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to update course name if authorization token is missing in the header")
    public void should_not_be_able_to_update_course_name_if_authorization_token_is_missing_in_the_header() throws Exception {
        CoursesEntity course = new CoursesEntity();
        course.setId(String.valueOf(UUID.randomUUID()));

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/courses/" + course.getId() + "/update-name"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to update course name because there is no permission to access the route even if an authorization token is provided")
    public void should_not_be_able_to_update_course_name_because_there_is_no_permission_to_access_the_route_even_if_an_authorization_token_is_provided() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        CoursesEntity course = new CoursesEntity();
        course.setId(String.valueOf(UUID.randomUUID()));

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/courses/" + course.getId() + "/update-name")
                .header("Authorization", token));

        String expectedMessage = "Access to this resource is restricted";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to update course description if authorization token is missing in the header")
    public void should_not_be_able_to_update_course_description_if_authorization_token_is_missing_in_the_header() throws Exception {
        CoursesEntity course = new CoursesEntity();
        course.setId(String.valueOf(UUID.randomUUID()));

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/courses/" + course.getId() + "/update-description"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to update course description because there is no permission to access the route even if an authorization token is provided")
    public void should_not_be_able_to_update_course_description_because_there_is_no_permission_to_access_the_route_even_if_an_authorization_token_is_provided() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        CoursesEntity course = new CoursesEntity();
        course.setId(String.valueOf(UUID.randomUUID()));

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/courses/" + course.getId() + "/update-description")
                .header("Authorization", token));

        String expectedMessage = "Access to this resource is restricted";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to fetch all courses created by creator user if authorization token is missing in the header")
    public void should_not_be_able_to_fetch_all_courses_created_by_creator_user_if_authorization_token_is_missing_in_the_header() throws Exception {
        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.get("/courses/creator-owned"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to fetch all courses created by creator user because there is no permission to access the route even if an authorization token is provided")
    public void should_not_be_able_to_fetch_all_courses_created_by_creator_user_because_there_is_no_permission_to_access_the_route_even_if_an_authorization_token_is_provided() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.get("/courses/creator-owned")
                .header("Authorization", token));

        String expectedMessage = "Access to this resource is restricted";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to deactivate a course if authorization token is missing in the header")
    public void should_not_be_able_to_deactivate_a_course_if_authorization_token_is_missing_in_the_header() throws Exception {
        CoursesEntity course = new CoursesEntity();
        course.setId(String.valueOf(UUID.randomUUID()));

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.delete("/courses/" + course.getId() + "/deactivate"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to deactivate a course because there is no permission to access the route even if an authorization token is provided")
    public void should_not_be_able_to_deactivate_a_course_because_there_is_no_permission_to_access_the_route_even_if_an_authorization_token_is_provided() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        CoursesEntity course = new CoursesEntity();
        course.setId(String.valueOf(UUID.randomUUID()));

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.delete("/courses/" + course.getId() + "/deactivate")
                .header("Authorization", token));

        String expectedMessage = "Access to this resource is restricted";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to fetch all courses if authorization token is missing in the header")
    public void should_not_be_able_to_fetch_all_courses_if_authorization_token_is_missing_in_the_header() throws Exception {
        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.get("/courses"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to fetch courses by user enrollments if authorization token is missing in the header")
    public void should_not_be_able_to_fetch_courses_by_user_enrollments_if_authorization_token_is_missing_in_the_header() throws Exception {
        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.get("/courses/subscribed"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to create a course content if authorization token is missing in the header")
    public void should_not_be_able_to_create_a_course_content_if_authorization_token_is_missing_in_the_header() throws Exception {
        CoursesEntity course = new CoursesEntity();
        course.setId(String.valueOf(UUID.randomUUID()));

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/courses/" + course.getId() + "/content"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to create a course content because there is no permission to access the route even if an authorization token is provided")
    public void should_not_be_able_to_create_a_course_content_because_there_is_no_permission_to_access_the_route_even_if_an_authorization_token_is_provided() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        CoursesEntity course = new CoursesEntity();
        course.setId(String.valueOf(UUID.randomUUID()));

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/courses/" + course.getId() + "/content")
                .header("Authorization", token));

        String expectedMessage = "Access to this resource is restricted";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to upload course content video if authorization token is missing in the header")
    public void should_not_be_able_to_upload_course_content_video_if_authorization_token_is_missing_in_the_header() throws Exception {
        CoursesContentsEntity courseContent = new CoursesContentsEntity();
        courseContent.setId(String.valueOf(UUID.randomUUID()));

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, "/courses/" + courseContent.getId() + "/content/upload-video"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to upload course content video because there is no permission to access the route even if an authorization token is provided")
    public void should_not_be_able_to_upload_course_content_video_because_there_is_no_permission_to_access_the_route_even_if_an_authorization_token_is_provided() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        CoursesContentsEntity courseContent = new CoursesContentsEntity();
        courseContent.setId(String.valueOf(UUID.randomUUID()));

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, "/courses/" + courseContent.getId() + "/content/upload-video")
                .header("Authorization", token));

        String expectedMessage = "Access to this resource is restricted";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to update course content description if authorization token is missing in the header")
    public void should_not_be_able_to_update_course_content_description_if_authorization_token_is_missing_in_the_header() throws Exception {
        CoursesContentsEntity courseContent = new CoursesContentsEntity();
        courseContent.setId(String.valueOf(UUID.randomUUID()));

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/courses/" + courseContent.getId() + "/content/update-description"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to update course content description because there is no permission to access the route even if an authorization token is provided")
    public void should_not_be_able_to_update_course_content_description_because_there_is_no_permission_to_access_the_route_even_if_an_authorization_token_is_provided() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        CoursesContentsEntity courseContent = new CoursesContentsEntity();
        courseContent.setId(String.valueOf(UUID.randomUUID()));

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/courses/" + courseContent.getId() + "/content/update-description")
                .header("Authorization", token));

        String expectedMessage = "Access to this resource is restricted";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to update course content video if authorization token is missing in the header")
    public void should_not_be_able_to_update_course_content_video_if_authorization_token_is_missing_in_the_header() throws Exception {
        CoursesContentsEntity courseContent = new CoursesContentsEntity();
        courseContent.setId(String.valueOf(UUID.randomUUID()));

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/courses/" + courseContent.getId() + "/content/update-video"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to update course content video because there is no permission to access the route even if an authorization token is provided")
    public void should_not_be_able_to_update_course_content_video_because_there_is_no_permission_to_access_the_route_even_if_an_authorization_token_is_provided() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        CoursesContentsEntity courseContent = new CoursesContentsEntity();
        courseContent.setId(String.valueOf(UUID.randomUUID()));

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/courses/" + courseContent.getId() + "/content/update-video")
                .header("Authorization", token));

        String expectedMessage = "Access to this resource is restricted";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to update course content release date if authorization token is missing in the header")
    public void should_not_be_able_to_update_course_content_release_date_if_authorization_token_is_missing_in_the_header() throws Exception {
        CoursesContentsEntity courseContent = new CoursesContentsEntity();
        courseContent.setId(String.valueOf(UUID.randomUUID()));

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/courses/" + courseContent.getId() + "/content/update-release-date"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to update course content release date because there is no permission to access the route even if an authorization token is provided")
    public void should_not_be_able_to_update_course_content_release_date_because_there_is_no_permission_to_access_the_route_even_if_an_authorization_token_is_provided() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        CoursesContentsEntity courseContent = new CoursesContentsEntity();
        courseContent.setId(String.valueOf(UUID.randomUUID()));

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/courses/" + courseContent.getId() + "/content/update-release-date")
                .header("Authorization", token));

        String expectedMessage = "Access to this resource is restricted";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to update course content module if authorization token is missing in the header")
    public void should_not_be_able_to_update_course_content_module_if_authorization_token_is_missing_in_the_header() throws Exception {
        CoursesContentsEntity courseContent = new CoursesContentsEntity();
        courseContent.setId(String.valueOf(UUID.randomUUID()));

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/courses/" + courseContent.getId() + "/content/update-course-module"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to update course content module because there is no permission to access the route even if an authorization token is provided")
    public void should_not_be_able_to_update_course_content_module_because_there_is_no_permission_to_access_the_route_even_if_an_authorization_token_is_provided() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        CoursesContentsEntity courseContent = new CoursesContentsEntity();
        courseContent.setId(String.valueOf(UUID.randomUUID()));

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/courses/" + courseContent.getId() + "/content/update-course-module")
                .header("Authorization", token));

        String expectedMessage = "Access to this resource is restricted";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to fetch all contents of a specific course if authorization token is missing in the header")
    public void should_not_be_able_to_fetch_all_contents_of_a_specific_course_if_authorization_token_is_missing_in_the_header() throws Exception {
        CoursesEntity course = new CoursesEntity();
        course.setId(String.valueOf(UUID.randomUUID()));

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.get("/courses/" + course.getId() + "/contents/creator-owned"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to fetch all contents of a specific course because there is no permission to access the route even if an authorization token is provided")
    public void should_not_be_able_to_fetch_all_contents_of_a_specific_course_because_there_is_no_permission_to_access_the_route_even_if_an_authorization_token_is_provided() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(user);

        CoursesEntity course = new CoursesEntity();
        course.setId(String.valueOf(UUID.randomUUID()));

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.get("/courses/" + course.getId() + "/contents/creator-owned")
                .header("Authorization", token));

        String expectedMessage = "Access to this resource is restricted";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @DisplayName("Should not be able to fetch all published contents of a specific course if authorization token is missing in the header")
    public void should_not_be_able_to_fetch_all_published_contents_of_a_specific_course_if_authorization_token_is_missing_in_the_header() throws Exception {
        CoursesEntity course = new CoursesEntity();
        course.setId(String.valueOf(UUID.randomUUID()));

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.get("/courses/" + course.getId() + "/contents/subscribed"));

        String expectedMessage = "Authorization token missing in request header";

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
    }
}
