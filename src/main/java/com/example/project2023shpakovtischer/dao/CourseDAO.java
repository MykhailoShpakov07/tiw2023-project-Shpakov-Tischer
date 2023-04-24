package com.example.project2023shpakovtischer.dao;

import com.example.project2023shpakovtischer.javaBeans.CourseBean;

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
    private static final String GET_COURSES_BY_STUDENT_ID = "SELECT DISTINCT courseId, name FROM ( attends join round ) join course WHERE studentId = ?";
    private static final String GET_COURSE_BY_ROUND_ID = "SELECT name FROM course JOIN round WHERE roundId = ?";

    public CourseBean getCourseById(int id) throws UnavailableException {
        CourseBean course = new CourseBean();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(GET_COURSE_BY_ID);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                course.setId(resultSet.getInt("courseId"));
                course.setName(resultSet.getString("courseName"));
            }
            else{
                return null;
            }
        } catch (SQLException e) {
            closeResultAndStatement(resultSet, preparedStatement);
            throw new UnavailableException(e.getMessage());
        }
        closeResultAndStatement(resultSet, preparedStatement);
        return course;
    }

    public CourseBean getCourseByRoundId(int roundId) throws UnavailableException {
        CourseBean course = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(GET_COURSE_BY_ROUND_ID);
            preparedStatement.setInt(1, roundId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                course = new CourseBean();
                course.setName(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            closeResultAndStatement(resultSet, preparedStatement);
            throw new UnavailableException(e.getMessage());
        }

        closeResultAndStatement(resultSet, preparedStatement);
        return course;
    }

    public ArrayList<CourseBean> getCoursesByProfessorId(int id) throws UnavailableException {
        ArrayList<CourseBean> courses = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(GET_COURSES_BY_PROFESSOR_ID);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                CourseBean course = new CourseBean();
                course.setId(resultSet.getInt("courseId"));
                course.setName(resultSet.getString("name"));
                courses.add(course);
            }
        } catch (SQLException e) {
            closeResultAndStatement(null, null);
            throw new UnavailableException(e.getMessage());
        }
        closeResultAndStatement(resultSet, preparedStatement);
        return courses;
    }

    public ArrayList<CourseBean> getCoursesByStudentId(int id) throws UnavailableException {
        ArrayList<CourseBean> courses = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(GET_COURSES_BY_STUDENT_ID);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                CourseBean course = new CourseBean();
                course.setId(resultSet.getInt("courseId"));
                course.setName(resultSet.getString("courseName"));
                courses.add(course);
            }
        } catch (SQLException e) {
            closeResultAndStatement(null, null);
            throw new UnavailableException(e.getMessage());
        }
        closeResultAndStatement(resultSet, preparedStatement);
        return courses;
    }

    private void closeResultAndStatement(ResultSet resultSet, PreparedStatement preparedStatement){
        try {
            resultSet.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void closeStatement(PreparedStatement preparedStatement){
        try {
            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
