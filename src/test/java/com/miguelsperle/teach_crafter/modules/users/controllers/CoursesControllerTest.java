package com.miguelsperle.teach_crafter.modules.users.controllers;

import com.miguelsperle.teach_crafter.modules.users.dtos.courses.CreateCourseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.courses.UpdateCourseDescriptionDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.courses.UpdateCourseNameDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents.CreateCourseContentDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents.UpdateCourseContentDescriptionDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents.UpdateCourseContentModuleDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents.UpdateCourseContentReleaseDateDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.CoursesContentsEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.enrollments.EnrollmentsEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.CoursesContentsRepository;
import com.miguelsperle.teach_crafter.modules.users.repositories.CoursesRepository;
import com.miguelsperle.teach_crafter.modules.users.repositories.EnrollmentsRepository;
import com.miguelsperle.teach_crafter.modules.users.repositories.UsersRepository;
import com.miguelsperle.teach_crafter.utils.TokenGenerator;
import com.miguelsperle.teach_crafter.utils.integration.*;
import com.miguelsperle.teach_crafter.utils.integration.configuration.H2CleanUpAndFakerExtension;
import com.miguelsperle.teach_crafter.utils.integration.configuration.interfaces.IntegrationTestSetup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@IntegrationTestSetup
public class CoursesControllerTest {
    @Value("${api.security.token.secret}")
    private String secret;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CoursesRepository coursesRepository;

    @Autowired
    private EnrollmentsRepository enrollmentsRepository;

    @Autowired
    private CoursesContentsRepository coursesContentsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Should be able to create a course")
    public void should_be_able_to_create_a_course() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        CreateCourseDTO createCourseDTO = new CreateCourseDTO(
                H2CleanUpAndFakerExtension.getFaker().educator().course(), // GENERATE A RANDOM NAME FOR THE COURSE
                H2CleanUpAndFakerExtension.getFaker().lorem().paragraph(), // GENERATE A RANDOM DESCRIPTION FOR THE COURSE
                10
        );

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/courses/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(createCourseDTO))
                .header("Authorization", token));

        String expectedMessage = "Course created successfully";

        result.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.CREATED.value()));
    }

    @Test
    @DisplayName("Should not be able to create a course if name is missing")
    public void should_not_be_able_to_create_a_course_if_name_is_missing() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        CreateCourseDTO createCourseDTO = new CreateCourseDTO(
                "",
                H2CleanUpAndFakerExtension.getFaker().lorem().paragraph(), // GENERATE A RANDOM DESCRIPTION FOR THE COURSE
                10
        );

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/courses/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(createCourseDTO))
                .header("Authorization", token));

        String expectedMessage = "Name is required to create a course";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should not be able to create a course if description is missing")
    public void should_not_be_able_to_create_a_course_if_description_is_missing() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        CreateCourseDTO createCourseDTO = new CreateCourseDTO(
                H2CleanUpAndFakerExtension.getFaker().educator().course(), // GENERATE A RANDOM NAME FOR THE COURSE
                "",
                10
        );

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/courses/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(createCourseDTO))
                .header("Authorization", token));

        String expectedMessage = "Description is required to create a course";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should not be able to create a course if maximum attendees is missing")
    public void should_not_be_able_to_create_a_course_if_maximum_attendees_is_missing() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        CreateCourseDTO createCourseDTO = new CreateCourseDTO(
                H2CleanUpAndFakerExtension.getFaker().educator().course(), // GENERATE A RANDOM NAME FOR THE COURSE
                H2CleanUpAndFakerExtension.getFaker().lorem().paragraph(), // GENERATE A RANDOM DESCRIPTION FOR THE COURSE
                null
        );

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/courses/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(createCourseDTO))
                .header("Authorization", token));

        String expectedMessage = "Maximum attendees is required to create a course";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }


    @Test
    @DisplayName("Should be able to update course name")
    public void should_be_able_to_update_course_name() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        CoursesEntity course = CoursesUtils.createCourse(userSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        UpdateCourseNameDTO updateCourseNameDTO = new UpdateCourseNameDTO(
                H2CleanUpAndFakerExtension.getFaker().educator().course() // GENERATE A RANDOM NEW NAME FOR THE COURSE
        );

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/courses/" + courseSaved.getId() + "/update-name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateCourseNameDTO))
                .header("Authorization", token));

        String expectedMessage = "Course name updated successfully";

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("Should not be able to update course name if new course name is missing")
    public void should_not_be_able_to_update_course_name_if_new_course_name_is_missing() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        CoursesEntity course = CoursesUtils.createCourse(userSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        UpdateCourseNameDTO updateCourseNameDTO = new UpdateCourseNameDTO("");

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/courses/" + courseSaved.getId() + "/update-name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateCourseNameDTO))
                .header("Authorization", token));

        String expectedMessage = "A new course name is required to update the current course name";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should be able to update course description")
    public void should_be_able_to_update_course_description() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        CoursesEntity course = CoursesUtils.createCourse(userSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        UpdateCourseDescriptionDTO updateCourseDescriptionDTO = new UpdateCourseDescriptionDTO(
                H2CleanUpAndFakerExtension.getFaker().lorem().paragraph() // GENERATE A RANDOM NEW DESCRIPTION FOR THE COURSE
        );

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/courses/" + courseSaved.getId() + "/update-description")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateCourseDescriptionDTO))
                .header("Authorization", token));

        String expectedMessage = "Course description updated successfully";

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("Should not be able to update course description if new course description is missing")
    public void should_not_be_able_to_update_course_description_if_new_course_description_is_missing() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        CoursesEntity course = CoursesUtils.createCourse(userSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        UpdateCourseDescriptionDTO updateCourseDescriptionDTO = new UpdateCourseDescriptionDTO("");

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/courses/" + courseSaved.getId() + "/update-description")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateCourseDescriptionDTO))
                .header("Authorization", token));

        String expectedMessage = "A new course description is required to update the current course description";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should be able to fetch all courses created by creator user")
    public void should_be_able_to_fetch_all_courses_created_by_creator_user() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        CoursesEntity course = CoursesUtils.createCourse(userSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.get("/courses/creator-owned")
                .header("Authorization", token));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(courseSaved.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(courseSaved.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(courseSaved.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].maximumAttendees").value(courseSaved.getMaximumAttendees()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].numberAvailableSpots").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].amountEnrollment").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].createdAt").value(courseSaved.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].createdBy").value(courseSaved.getUsersEntity().getName()));
    }

    @Test
    @DisplayName("Should be able to deactivate a course")
    public void should_be_able_to_deactivate_a_course() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        CoursesEntity course = CoursesUtils.createCourse(userSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.delete("/courses/" + courseSaved.getId() + "/deactivate")
                .header("Authorization", token));

        String expectedMessage = "Course successfully deactivated";

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("Should be able to fetch all courses")
    public void should_be_able_to_fetch_all_courses() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        List<UsersEntity> usersSaved = this.usersRepository.saveAllAndFlush(List.of(user, userOwner));

        CoursesEntity course = CoursesUtils.createCourse(usersSaved.get(1), H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        String token = TokenGenerator.generateToken(usersSaved.get(0), this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.get("/courses")
                .param("description_keyword", "")
                .header("Authorization", token));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(courseSaved.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(courseSaved.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(courseSaved.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].maximumAttendees").value(courseSaved.getMaximumAttendees()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].numberAvailableSpots").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].amountEnrollment").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].createdAt").value(courseSaved.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].createdBy").value(courseSaved.getUsersEntity().getName()));
    }

    @Test
    @DisplayName("Should be able to fetch courses by user enrollments")
    public void should_be_able_to_fetch_courses_by_user_enrollments() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        List<UsersEntity> usersSaved = this.usersRepository.saveAllAndFlush(List.of(user, userOwner));

        CoursesEntity course = CoursesUtils.createCourse(usersSaved.get(1), H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        EnrollmentsEntity enrollment = EnrollmentsUtils.createEnrollment(courseSaved, usersSaved.get(0));

        EnrollmentsEntity enrollmentSaved = this.enrollmentsRepository.saveAndFlush(enrollment);

        String token = TokenGenerator.generateToken(usersSaved.get(0), this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.get("/courses/subscribed")
                .header("Authorization", token));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(enrollmentSaved.getCoursesEntity().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(enrollmentSaved.getCoursesEntity().getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(enrollmentSaved.getCoursesEntity().getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].createdAt").value(enrollmentSaved.getCoursesEntity().getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].enrollmentCreatedAt").value(enrollmentSaved.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].createdBy").value(enrollmentSaved.getCoursesEntity().getUsersEntity().getName()));
    }

    @Test
    @DisplayName("Should be able to create a course content")
    public void should_be_able_to_create_a_course_content() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        CoursesEntity course = CoursesUtils.createCourse(userSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        CreateCourseContentDTO createCourseContentDTO = new CreateCourseContentDTO(
                H2CleanUpAndFakerExtension.getFaker().lorem().paragraph(), // GENERATE A RANDOM DESCRIPTION FOR THE COURSE CONTENT
                LocalDate.now(),
                "Introduction to " + H2CleanUpAndFakerExtension.getFaker().lorem().word() // GENERATE RANDOM COURSE MODULE FOR THE COURSE CONTENT
        );

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/courses/" + courseSaved.getId() + "/content")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(createCourseContentDTO))
                .header("Authorization", token));

        String expectedMessage = "Course content created successfully";

        result.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.CREATED.value()));
    }

    @Test
    @DisplayName("Should not be able to create a course content if description is missing")
    public void should_not_be_able_to_create_a_course_content_if_description_is_missing() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        CoursesEntity course = CoursesUtils.createCourse(userSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        CreateCourseContentDTO createCourseContentDTO = new CreateCourseContentDTO(
                "",
                LocalDate.now(),
                "Introduction to " + H2CleanUpAndFakerExtension.getFaker().lorem().word() // GENERATE RANDOM COURSE MODULE FOR THE COURSE CONTENT
        );

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/courses/" + courseSaved.getId() + "/content")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(createCourseContentDTO))
                .header("Authorization", token));

        String expectedMessage = "Description is required to create a content";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should not be able to create a course content if release date is missing")
    public void should_not_be_able_to_create_a_course_content_if_release_date_is_missing() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        CoursesEntity course = CoursesUtils.createCourse(userSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        CreateCourseContentDTO createCourseContentDTO = new CreateCourseContentDTO(
                H2CleanUpAndFakerExtension.getFaker().lorem().paragraph(), // GENERATE A RANDOM DESCRIPTION FOR THE COURSE CONTENT
                null,
                "Introduction to " + H2CleanUpAndFakerExtension.getFaker().lorem().word() // GENERATE RANDOM COURSE MODULE FOR THE COURSE CONTENT
        );

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/courses/" + courseSaved.getId() + "/content")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(createCourseContentDTO))
                .header("Authorization", token));

        String expectedMessage = "A release date is required to create a content";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should not be able to create a course content if course module is missing")
    public void should_not_be_able_to_create_a_course_content_if_course_module_is_missing() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        CoursesEntity course = CoursesUtils.createCourse(userSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        CreateCourseContentDTO createCourseContentDTO = new CreateCourseContentDTO(
                H2CleanUpAndFakerExtension.getFaker().lorem().paragraph(), // GENERATE A RANDOM DESCRIPTION FOR THE COURSE CONTENT
                LocalDate.now(),
                ""
        );

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/courses/" + courseSaved.getId() + "/content")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(createCourseContentDTO))
                .header("Authorization", token));

        String expectedMessage = "Course module is required to create a content";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should be able to upload course content video")
    public void should_be_able_to_upload_course_content_video() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        CoursesEntity course = CoursesUtils.createCourse(userSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        CoursesContentsEntity courseContent = CoursesContentsUtils.createCourseContent(courseSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesContentsEntity courseContentSaved = this.coursesContentsRepository.saveAndFlush(courseContent);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        Path videoPath = Paths.get("src/test/java/com/miguelsperle/teach_crafter/utils/integration/resources/test_videos/video.mp4");

        MockMultipartFile videoFile = new MockMultipartFile("videoFile", "video.mp4", "video/mp4", Files.readAllBytes(videoPath));

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, "/courses/" + courseContentSaved.getId() + "/content/upload-video")
                .file(videoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", token));

        String expectedMessage = "Course content video uploaded successfully";

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("Should not be able to upload course content video if video is missing")
    public void should_not_be_able_to_upload_course_content_video_if_video_is_missing() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        CoursesEntity course = CoursesUtils.createCourse(userSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        CoursesContentsEntity courseContent = CoursesContentsUtils.createCourseContent(courseSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesContentsEntity courseContentSaved = this.coursesContentsRepository.saveAndFlush(courseContent);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, "/courses/" + courseContentSaved.getId() + "/content/upload-video")
                .header("Authorization", token));

        String expectedMessage = "Video file is required";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should be able to update course content description")
    public void should_be_able_to_update_course_content_description() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        CoursesEntity course = CoursesUtils.createCourse(userSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        CoursesContentsEntity courseContent = CoursesContentsUtils.createCourseContent(courseSaved, H2CleanUpAndFakerExtension.getFaker());
        courseContent.setVideoUrl("{\"1080p\": \"https://video_url\", \"720p\": \"https://video_url\", \"360p\": \"https://video_url\", \"480p\": \"https://video_url\"}");

        CoursesContentsEntity courseContentSaved = this.coursesContentsRepository.saveAndFlush(courseContent);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        UpdateCourseContentDescriptionDTO updateCourseContentDescriptionDTO = new UpdateCourseContentDescriptionDTO(H2CleanUpAndFakerExtension.getFaker().lorem().paragraph());

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/courses/" + courseContentSaved.getId() + "/content/update-description")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateCourseContentDescriptionDTO))
                .header("Authorization", token));

        String expectedMessage = "Course content description updated successfully";

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("Should not be able to update course content description if new content description is missing")
    public void should_not_be_able_to_update_course_content_description_if_new_content_description_is_missing() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        CoursesEntity course = CoursesUtils.createCourse(userSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        CoursesContentsEntity courseContent = CoursesContentsUtils.createCourseContent(courseSaved, H2CleanUpAndFakerExtension.getFaker());
        courseContent.setVideoUrl("{\"1080p\": \"https://video_url\", \"720p\": \"https://video_url\", \"360p\": \"https://video_url\", \"480p\": \"https://video_url\"}");

        CoursesContentsEntity courseContentSaved = this.coursesContentsRepository.saveAndFlush(courseContent);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        UpdateCourseContentDescriptionDTO updateCourseContentDescriptionDTO = new UpdateCourseContentDescriptionDTO("");

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/courses/" + courseContentSaved.getId() + "/content/update-description")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateCourseContentDescriptionDTO))
                .header("Authorization", token));

        String expectedMessage = "A new content description is required to update the current content description";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should be able to update course content video")
    public void should_be_able_to_update_course_content_video() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        CoursesEntity course = CoursesUtils.createCourse(userSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        CoursesContentsEntity courseContent = CoursesContentsUtils.createCourseContent(courseSaved, H2CleanUpAndFakerExtension.getFaker());
        courseContent.setVideoUrl("{\"1080p\": \"https://video_url\", \"720p\": \"https://video_url\", \"360p\": \"https://video_url\", \"480p\": \"https://video_url\"}");

        CoursesContentsEntity courseContentSaved = this.coursesContentsRepository.saveAndFlush(courseContent);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        Path videoPath = Paths.get("src/test/java/com/miguelsperle/teach_crafter/utils/integration/resources/test_videos/video.mp4");

        MockMultipartFile videoFile = new MockMultipartFile("videoFile", "video.mp4", "video/mp4", Files.readAllBytes(videoPath));

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/courses/" + courseContentSaved.getId() + "/content/update-video")
                .file(videoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", token));

        String expectedMessage = "Course content video updated successfully";

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("Should not be able to update course content video if video is missing")
    public void should_not_be_able_to_update_course_content_video_if_video_is_missing() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        CoursesEntity course = CoursesUtils.createCourse(userSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        CoursesContentsEntity courseContent = CoursesContentsUtils.createCourseContent(courseSaved, H2CleanUpAndFakerExtension.getFaker());
        courseContent.setVideoUrl("{\"1080p\": \"https://video_url\", \"720p\": \"https://video_url\", \"360p\": \"https://video_url\", \"480p\": \"https://video_url\"}");

        CoursesContentsEntity courseContentSaved = this.coursesContentsRepository.saveAndFlush(courseContent);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/courses/" + courseContentSaved.getId() + "/content/update-video")
                .header("Authorization", token));

        String expectedMessage = "Video file is required";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }


    @Test
    @DisplayName("Should be able to update course content release date")
    public void should_be_able_to_update_course_content_release_date() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        CoursesEntity course = CoursesUtils.createCourse(userSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        CoursesContentsEntity courseContent = CoursesContentsUtils.createCourseContent(courseSaved, H2CleanUpAndFakerExtension.getFaker());
        courseContent.setVideoUrl("{\"1080p\": \"https://video_url\", \"720p\": \"https://video_url\", \"360p\": \"https://video_url\", \"480p\": \"https://video_url\"}");

        CoursesContentsEntity courseContentSaved = this.coursesContentsRepository.saveAndFlush(courseContent);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        UpdateCourseContentReleaseDateDTO updateCourseContentReleaseDateDTO = new UpdateCourseContentReleaseDateDTO(LocalDate.now().plusDays(2));

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/courses/" + courseContentSaved.getId() + "/content/update-release-date")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateCourseContentReleaseDateDTO))
                .header("Authorization", token));

        String expectedMessage = "Course content release date updated successfully";

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("Should not be able to update course content release date if new content release date is missing")
    public void should_not_be_able_to_update_course_content_release_date_if_new_content_release_date_is_missing() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        CoursesEntity course =  CoursesUtils.createCourse(userSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        CoursesContentsEntity courseContent = CoursesContentsUtils.createCourseContent(courseSaved, H2CleanUpAndFakerExtension.getFaker());
        courseContent.setVideoUrl("{\"1080p\": \"https://video_url\", \"720p\": \"https://video_url\", \"360p\": \"https://video_url\", \"480p\": \"https://video_url\"}");

        CoursesContentsEntity courseContentSaved = this.coursesContentsRepository.saveAndFlush(courseContent);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        UpdateCourseContentReleaseDateDTO updateCourseContentReleaseDateDTO = new UpdateCourseContentReleaseDateDTO(null);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/courses/" + courseContentSaved.getId() + "/content/update-release-date")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateCourseContentReleaseDateDTO))
                .header("Authorization", token));

        String expectedMessage = "A new content release date is required to update the current content release date";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should be able to update course content module")
    public void should_be_able_to_update_course_content_module() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        CoursesEntity course = CoursesUtils.createCourse(userSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        CoursesContentsEntity courseContent = CoursesContentsUtils.createCourseContent(courseSaved, H2CleanUpAndFakerExtension.getFaker());
        courseContent.setVideoUrl("{\"1080p\": \"https://video_url\", \"720p\": \"https://video_url\", \"360p\": \"https://video_url\", \"480p\": \"https://video_url\"}");

        CoursesContentsEntity courseContentSaved = this.coursesContentsRepository.saveAndFlush(courseContent);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        UpdateCourseContentModuleDTO updateCourseContentModuleDTO = new UpdateCourseContentModuleDTO("Introduction to Java");

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/courses/" + courseContentSaved.getId() + "/content/update-course-module")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateCourseContentModuleDTO))
                .header("Authorization", token));

        String expectedMessage = "Course content module updated successfully";

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("Should not be able to update course content module if new content module is missing")
    public void should_not_be_able_to_update_course_content_module_if_new_content_module_is_missing() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        CoursesEntity course = CoursesUtils.createCourse(userSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        CoursesContentsEntity courseContent = CoursesContentsUtils.createCourseContent(courseSaved, H2CleanUpAndFakerExtension.getFaker());
        courseContent.setVideoUrl("{\"1080p\": \"https://video_url\", \"720p\": \"https://video_url\", \"360p\": \"https://video_url\", \"480p\": \"https://video_url\"}");

        CoursesContentsEntity courseContentSaved = this.coursesContentsRepository.saveAndFlush(courseContent);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        UpdateCourseContentModuleDTO updateCourseContentModuleDTO = new UpdateCourseContentModuleDTO("");

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.put("/courses/" + courseContentSaved.getId() + "/content/update-course-module")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONConverter.objectToJSON(updateCourseContentModuleDTO))
                .header("Authorization", token));

        String expectedMessage = "A new content module is required to update the current content module";

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("Should be able to fetch all contents of a specific course")
    public void should_be_able_to_fetch_all_contents_of_a_specific_course() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userSaved = this.usersRepository.saveAndFlush(userOwner);

        CoursesEntity course = CoursesUtils.createCourse(userSaved, H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        CoursesContentsEntity courseContent = CoursesContentsUtils.createCourseContent(courseSaved, H2CleanUpAndFakerExtension.getFaker());
        courseContent.setVideoUrl("{\"1080p\": \"https://video_url\", \"720p\": \"https://video_url\", \"360p\": \"https://video_url\", \"480p\": \"https://video_url\"}");

        CoursesContentsEntity courseContentSaved = this.coursesContentsRepository.saveAndFlush(courseContent);

        String token = TokenGenerator.generateToken(userSaved, this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.get("/courses/" + courseSaved.getId() + "/contents/creator-owned")
                .header("Authorization", token));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(courseContentSaved.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(courseContentSaved.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].videoUrl").value(courseContentSaved.getVideoUrl()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value(courseContentSaved.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].releaseDate").value(courseContentSaved.getReleaseDate().format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].contentModule").value(courseContentSaved.getContentModule()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].createdAt").value(courseContentSaved.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    @Test
    @DisplayName("Should be able to fetch all published contents of a specific course")
    public void should_be_able_to_fetch_all_published_contents_of_a_specific_course() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        List<UsersEntity> usersSaved = this.usersRepository.saveAllAndFlush(List.of(user, userOwner));

        CoursesEntity course = CoursesUtils.createCourse(usersSaved.get(1), H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        CoursesContentsEntity courseContent = CoursesContentsUtils.createCourseContent(courseSaved, H2CleanUpAndFakerExtension.getFaker());
        courseContent.setVideoUrl("{\"1080p\": \"https://video_url\", \"720p\": \"https://video_url\", \"360p\": \"https://video_url\", \"480p\": \"https://video_url\"}");

        CoursesContentsEntity courseContentSaved = this.coursesContentsRepository.saveAndFlush(courseContent);

        EnrollmentsEntity enrollment = EnrollmentsUtils.createEnrollment(courseSaved, usersSaved.get(0));

        this.enrollmentsRepository.save(enrollment);

        String token = TokenGenerator.generateToken(usersSaved.get(0), this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.get("/courses/" + courseSaved.getId() + "/contents/subscribed")
                .header("Authorization", token));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(courseContentSaved.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(courseContentSaved.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].videoUrl").value(courseContentSaved.getVideoUrl()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value(courseContentSaved.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].releaseDate").value(courseContentSaved.getReleaseDate().format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].contentModule").value(courseContentSaved.getContentModule()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].createdAt").value(courseContentSaved.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }
}
