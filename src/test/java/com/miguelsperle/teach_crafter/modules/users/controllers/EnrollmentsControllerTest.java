package com.miguelsperle.teach_crafter.modules.users.controllers;

import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.enrollments.EnrollmentsEntity;
import com.miguelsperle.teach_crafter.modules.users.entities.users.UsersEntity;
import com.miguelsperle.teach_crafter.modules.users.repositories.CoursesRepository;
import com.miguelsperle.teach_crafter.modules.users.repositories.EnrollmentsRepository;
import com.miguelsperle.teach_crafter.modules.users.repositories.UsersRepository;
import com.miguelsperle.teach_crafter.utils.TokenGenerator;
import com.miguelsperle.teach_crafter.utils.integration.CoursesUtils;
import com.miguelsperle.teach_crafter.utils.integration.EnrollmentsUtils;
import com.miguelsperle.teach_crafter.utils.integration.UsersUtils;
import com.miguelsperle.teach_crafter.utils.integration.configuration.H2CleanUpAndFakerExtension;
import com.miguelsperle.teach_crafter.utils.integration.configuration.interfaces.IntegrationTestSetup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@IntegrationTestSetup
public class EnrollmentsControllerTest {
    @Value("${api.security.token.secret}")
    private String secret;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CoursesRepository coursesRepository;

    @Autowired
    private EnrollmentsRepository enrollmentsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Should be able to subscribe in the course")
    public void should_be_able_to_subscribe_in_the_course() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        List<UsersEntity> usersSaved = this.usersRepository.saveAllAndFlush(List.of(user, userOwner));

        CoursesEntity course = CoursesUtils.createCourse(usersSaved.get(1), H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        String token = TokenGenerator.generateToken(usersSaved.get(0), this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.post("/enrollment/" + courseSaved.getId() + "/create")
                .header("Authorization", token));

        String expectedMessage = "Enrollment created successfully";

        result.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.CREATED.value()));
    }

    @Test
    @DisplayName("Should be able to unsubscribe from a course")
    public void should_be_able_to_unsubscribe_from_a_course() throws Exception {
        String password = H2CleanUpAndFakerExtension.getFaker().internet().password();

        UsersEntity user = UsersUtils.createUser("ROLE_USER", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        UsersEntity userOwner = UsersUtils.createUser("ROLE_CREATOR", this.passwordEncoder.encode(password), H2CleanUpAndFakerExtension.getFaker());

        List<UsersEntity> usersSaved = this.usersRepository.saveAllAndFlush(List.of(user, userOwner));

        CoursesEntity course = CoursesUtils.createCourse(usersSaved.get(1), H2CleanUpAndFakerExtension.getFaker());

        CoursesEntity courseSaved = this.coursesRepository.saveAndFlush(course);

        EnrollmentsEntity enrollment = EnrollmentsUtils.createEnrollment(courseSaved, usersSaved.get(0));

        this.enrollmentsRepository.save(enrollment);

        String token = TokenGenerator.generateToken(usersSaved.get(0), this.secret);

        ResultActions result = this.mvc.perform(MockMvcRequestBuilders.delete("/enrollment/" + courseSaved.getId() + "/delete")
                .header("Authorization", token));

        String expectedMessage = "Enrollment deleted successfully";

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.OK.value()));
    }
}
