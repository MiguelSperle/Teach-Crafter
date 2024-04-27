package com.miguelsperle.teach_crafter.modules.users.services;

import com.miguelsperle.teach_crafter.modules.users.dtos.CreateCourseDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;
import com.miguelsperle.teach_crafter.modules.users.repository.CoursesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoursesService {
    private final CoursesRepository coursesRepository;
    private final UsersService usersService;

    public void createCourse(CreateCourseDTO createCourseDTO){
        CoursesEntity newCourse = new CoursesEntity();

        newCourse.setName(createCourseDTO.name());
        newCourse.setDescription(createCourseDTO.description());
        newCourse.setMaximumAttendees(createCourseDTO.maximumAttendees());
        newCourse.setUsersEntity(this.usersService.getUserAuthenticated());

        this.coursesRepository.save(newCourse);
    }
}
