package com.miguelsperle.teach_crafter.utils.mappers;

import com.miguelsperle.teach_crafter.modules.users.dtos.courses.CreateCourseDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.courses.UpdateCourseDescriptionDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.courses.UpdateCourseNameDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.courses.CoursesEntity;

public class CoursesMapper {
    public static CreateCourseDTO toConvertCreateCourseDTO(CoursesEntity coursesEntity) {
        return new CreateCourseDTO(coursesEntity.getName(), coursesEntity.getDescription(), coursesEntity.getMaximumAttendees());
    }

    public static UpdateCourseNameDTO toConvertUpdateCourseNameDTO(CoursesEntity coursesEntity) {
        return new UpdateCourseNameDTO(coursesEntity.getName());
    }

    public static UpdateCourseDescriptionDTO toConvertUpdateCourseDescriptionDTO(CoursesEntity coursesEntity){
        return new UpdateCourseDescriptionDTO(coursesEntity.getDescription());
    }
}
