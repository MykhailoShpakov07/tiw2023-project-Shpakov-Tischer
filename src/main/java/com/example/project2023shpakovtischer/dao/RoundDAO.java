package com.example.project2023shpakovtischer.dao;

import com.example.project2023shpakovtischer.beans.RoundBean;

import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RoundDAO {
    private Connection connection;

    private static final String GET_ROUND_BY_ID_FOR_PROF = "SELECT roundId, date, profId FROM round JOIN course WHERE roundId = ?";
    private static final String GET_ROUNDS_BY_COURSE_ID = "SELECT roundId, date FROM round WHERE courseId = ?";
    private static final String GET_ROUNDS_BY_COURSE_ID_AND_STUDENT_ID = "SELECT DISTINCT round.roundId, round.date FROM attends join round WHERE courseId = ? AND studentId = ? ";

    public RoundDAO(Connection connection) {
        this.connection = connection;
    }

    public ArrayList<RoundBean> getRoundsByCourseId(int courseId) throws UnavailableException {
        ArrayList<RoundBean> rounds = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(GET_ROUNDS_BY_COURSE_ID);
            preparedStatement.setInt(1, courseId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                RoundBean round = new RoundBean();
                round.setId(resultSet.getInt("roundId"));
                round.setDate(resultSet.getDate("date"));
                rounds.add(round);
            }
        }
        catch (SQLException ex){
            closeResultAndStatement(resultSet, preparedStatement);
            throw new UnavailableException(ex.getMessage());
        }

        closeResultAndStatement(resultSet, preparedStatement);
        return rounds;
    }

    public ArrayList<RoundBean> getRoundsByCourseIdAndStudentId(int courseId, int studentId) throws UnavailableException {
        ArrayList<RoundBean> rounds = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try{
            preparedStatement = connection.prepareStatement(GET_ROUNDS_BY_COURSE_ID_AND_STUDENT_ID);
            preparedStatement.setInt(1, courseId);
            preparedStatement.setInt(2, studentId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                RoundBean round = new RoundBean();
                round.setId(resultSet.getInt("roundId"));
                round.setDate(resultSet.getDate("date"));
                rounds.add(round);
            }
        }
        catch (SQLException ex){
            closeResultAndStatement(resultSet, preparedStatement);
            throw new UnavailableException(ex.getMessage());
        }

        closeResultAndStatement(resultSet, preparedStatement);
        return rounds;
    }

    public RoundBean getRoundById(int roundId) throws UnavailableException {
        RoundBean round = new RoundBean();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            preparedStatement = connection.prepareStatement(GET_ROUND_BY_ID_FOR_PROF);
            preparedStatement.setInt(1, roundId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                round.setId(resultSet.getInt("roundId"));
                round.setDate(resultSet.getDate("date"));
                round.setProfessorId(resultSet.getInt("profId"));
            }
        } catch (SQLException ex) {
            throw new UnavailableException(ex.getMessage());
        }
        return round;
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

}
