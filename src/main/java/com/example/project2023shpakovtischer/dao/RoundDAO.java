package com.example.project2023shpakovtischer.dao;

import com.example.project2023shpakovtischer.javaBeans.ReportBean;
import com.example.project2023shpakovtischer.javaBeans.RoundBean;

import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RoundDAO {
    private Connection connection;

    public RoundDAO(Connection connection) {
        this.connection = connection;
    }

    private static final String GET_ROUNDS_BY_COURSE_ID = "SELECT roundId, date FROM round WHERE courseId = ?";

    private static final String GET_ROUNDS_BY_COURSE_ID_AND_STUDENT_ID = "SELECT DISTINCT roundId, date FROM attends join round WHERE courseId = ? AND studentId = ? ";
    private static final String CREATE_REPORT = "UPDATE round SET reportIsCreated = 1, reportDateTime = NOW() WHERE roundId = ?";

    private static final String GET_REPORT_BY_ROUND_ID = "SELECT reportCode, reportDateTime, c.name , date FROM round join course c on courseId = c.courseId WHERE roundId = ?";

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

    ReportBean createReport(int roundId) throws UnavailableException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(CREATE_REPORT);
            preparedStatement.setInt(1, roundId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException ex){
            closeStatement(preparedStatement);
            throw new UnavailableException(ex.getMessage());
        }
        closeStatement(preparedStatement);

        return getReportByRoundId(roundId);
    }

    public ReportBean getReportByRoundId(int roundId) throws UnavailableException {
        ReportBean report = new ReportBean();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet;
        try {
            preparedStatement = connection.prepareStatement(GET_REPORT_BY_ROUND_ID);
            preparedStatement.setInt(1, roundId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                report.setReportCode(Integer.parseInt(resultSet.getString("reportCode")));
                report.setReportDateTime(resultSet.getTimestamp("reportDateTime"));
                report.setCourseName(resultSet.getString("name"));
                report.setRoundDate(resultSet.getDate("date"));
            }
        }
        catch (SQLException ex){
            throw new UnavailableException(ex.getMessage());
        }

        closeResultAndStatement(resultSet, preparedStatement);
        return report;
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
