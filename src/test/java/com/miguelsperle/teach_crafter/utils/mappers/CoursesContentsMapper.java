package com.miguelsperle.teach_crafter.utils.mappers;

import com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents.CreateCourseContentDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents.UpdateCourseContentDescriptionDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents.UpdateCourseContentModuleDTO;
import com.miguelsperle.teach_crafter.modules.users.dtos.coursesContents.UpdateCourseContentReleaseDateDTO;
import com.miguelsperle.teach_crafter.modules.users.entities.coursesContents.CoursesContentsEntity;

public class CoursesContentsMapper {
    public static CreateCourseContentDTO toConvertCreateCourseContentDTO(CoursesContentsEntity coursesContentsEntity) {
        return new CreateCourseContentDTO(coursesContentsEntity.getDescription(), coursesContentsEntity.getReleaseDate(), coursesContentsEntity.getCourseModule());
    }

    public static UpdateCourseContentDescriptionDTO toConvertUpdateCourseContentDescriptionDTO(CoursesContentsEntity coursesContentsEntity) {
        return new UpdateCourseContentDescriptionDTO(coursesContentsEntity.getDescription());
    }

    public static UpdateCourseContentReleaseDateDTO toConvertUpdateCourseContentReleaseDateDTO(CoursesContentsEntity coursesContentsEntity) {
        return new UpdateCourseContentReleaseDateDTO(coursesContentsEntity.getReleaseDate());
    }

    public static UpdateCourseContentModuleDTO toConvertUpdateCourseContentModuleDTO(CoursesContentsEntity coursesContentsEntity) {
        return new UpdateCourseContentModuleDTO(coursesContentsEntity.getCourseModule());
    }
}
