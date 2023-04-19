package com.example.project2023shpakovtischer.dao;

import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CourseDAO {
    private Connection connection;

    public CourseDAO(Connection connection){
        this.connection = connection;
    }

    private static final String GET_COURSE_BY_ID = "SELECT * FROM course WHERE courseId = ?";
    private static final String GET_COURSES_BY_PROFESSOR_ID = "SELECT * FROM course WHERE profid = ?";

    public CourseBean getCourseById(int id) throws UnavailableException {
        CourseBean course = new CourseBean();
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_COURSE_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                course.setCourseId(resultSet.getInt("courseId"));
                course.setCourseName(resultSet.getString("courseName"));
            }
            else{
                return null;
            }
        } catch (SQLException e) {
            throw new UnavailableException("Error with SQL");
        }
        return course;
    }

    public ArrayList<CourseBean> getCoursesByProfessorId(int id) throws UnavailableException {
        ArrayList<CourseBean> courses = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_COURSES_BY_PROFESSOR_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                CourseBean course = new CourseBean();
                course.setCourseId(resultSet.getInt("courseId"));
                course.setCourseName(resultSet.getString("courseName"));
                courses.add(course);
            }
        } catch (SQLException e) {
            throw new UnavailableException("Error with SQL");
        }
        return courses;
    }
}
