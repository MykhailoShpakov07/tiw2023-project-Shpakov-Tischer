package com.example.project2023shpakovtischer.dao;

import com.example.project2023shpakovtischer.javaBeans.ReportBean;

import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportDAO {
    private Connection connection;


    private static final String REPORT_MARKS = "UPDATE attends SET evaluationStatus = 4 WHERE roundId = ? and ( evaluationStatus = 2 or evaluationStatus = 3 )";
    private static final String UPDATE_MARK_ON_REFUSED_ATTENDANCES = "UPDATE attends SET mark = 16 WHERE roundId = ?";
    private static final String CREATE_REPORT = "UPDATE round SET reportIsCreated = 1, reportDateTime = NOW() WHERE roundId = ?";
    private static final String GET_REPORT_BY_ROUND_ID = "SELECT reportCode, reportDateTime, name, date FROM round join course WHERE roundId = ? AND reportIsCreated = 1";



    public ReportDAO(Connection connection) {
        this.connection = connection;
    }

    public ReportBean createReport(int roundId) throws UnavailableException, SQLException {
        try {
            connection.setAutoCommit(false);
            reportRound(roundId);
            reportMarks(roundId);
            connection.commit();
        } catch (UnavailableException e) {
            connection.rollback();
            throw e;
        } catch (SQLException e) {
            connection.rollback();
            throw new UnavailableException(e.getMessage());
        }
        connection.setAutoCommit(true);

        return getReportByRoundId(roundId);
    }


    void reportRound(int roundId) throws UnavailableException {
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
    }


    void reportMarks(int roundId) throws UnavailableException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(UPDATE_MARK_ON_REFUSED_ATTENDANCES);
            preparedStatement.setInt(1, roundId);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(REPORT_MARKS);
            preparedStatement.setInt(1, roundId);
            preparedStatement.executeUpdate();

            AttendanceDAO attendanceDAO = new AttendanceDAO(connection);
            attendanceDAO.deleteAttendancesForNextRounds(roundId);
        }
        catch (SQLException e) {
            closeStatement(preparedStatement);
            throw new UnavailableException(e.getMessage());
        }

        closeStatement(preparedStatement);
    }


    public ReportBean getReportByRoundId(int roundId) throws UnavailableException {
        ReportBean report = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet;
        try {
            preparedStatement = connection.prepareStatement(GET_REPORT_BY_ROUND_ID);
            preparedStatement.setInt(1, roundId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                report = new ReportBean();
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


    private void closeStatement(PreparedStatement preparedStatement){
        try {
            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
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
