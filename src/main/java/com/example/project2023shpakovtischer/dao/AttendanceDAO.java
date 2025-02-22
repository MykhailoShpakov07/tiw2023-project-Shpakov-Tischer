package com.example.project2023shpakovtischer.dao;

import com.example.project2023shpakovtischer.beans.AttendanceBean;
import com.example.project2023shpakovtischer.enums.EvaluationStatus;
import com.example.project2023shpakovtischer.enums.Mark;

import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {
    private final Connection connection;

    private static final String GET_ATTENDANCES_BY_ROUND_ID_ORDERED_BY = "SELECT studentId, name, surname, email, studyCourse, mark, evaluationStatus " +
            "FROM attends join user on studentId = userId WHERE roundId = ? ORDER BY ";
    private static final String GET_ATTENDANCE_BY_ROUND_ID_AND_STUDENT_ID = "SELECT studentId, name, surname, email, studyCourse, mark, evaluationStatus " +
            "FROM attends join user on studentId = userId WHERE roundId = ? AND studentId = ?";
    private static final String ASSIGN_MARK = "UPDATE attends SET mark = ?, evaluationStatus = ? WHERE roundId = ? AND studentId = ? AND evaluationStatus < 2";
    private static final String PUBLISH_MARKS = "UPDATE attends SET evaluationStatus = 2 WHERE roundId = ? AND evaluationStatus = 1";
    private static final String REFUSE_MARK = "UPDATE attends SET evaluationStatus = 3 WHERE studentId = ? AND roundId = ? AND evaluationStatus = 2";
    private static final String DELETE_FURTHER_ATTENDANCES = "DELETE a1 FROM attends a1" +
                                                    "                   JOIN round r1 ON a1.roundId = r1.roundId" +
                                                    "                   JOIN attends a2 ON a1.studentId = a2.studentId" +
                                                    "                   JOIN round r2 ON a2.roundId = r2.roundId" +
                                                    "  WHERE r1.courseId = r2.courseId" +
                                                    "  AND a1.roundId != a2.roundId" +
                                                    "  AND r2.date < r1.date" +
                                                    "  AND a2.mark BETWEEN 18 AND 31" +
                                                    "  AND a2.evaluationStatus = 4";
    private static final String CAN_BE_PUBLISHED = "SELECT * FROM attends WHERE roundId=? and evaluationStatus = 1";


    public AttendanceDAO(Connection connection){
        this.connection = connection;
    }

    //suppose that parameters received were controlled by servlet previously
    public List<AttendanceBean> getOrderedAttendances(int roundId, String orderLabel, boolean reverse) throws UnavailableException {
        List<AttendanceBean> attendances = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            if (reverse)
                preparedStatement = connection.prepareStatement(GET_ATTENDANCES_BY_ROUND_ID_ORDERED_BY + orderLabel + " DESC");
            else
                preparedStatement = connection.prepareStatement(GET_ATTENDANCES_BY_ROUND_ID_ORDERED_BY + orderLabel + " ASC");

            preparedStatement.setInt(1, roundId);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                AttendanceBean attendance = new AttendanceBean();
                attendance.setStudentId(resultSet.getInt("studentId"));
                attendance.setRoundId(roundId);
                attendance.setStudentName(resultSet.getString("name"));
                attendance.setStudentSurname(resultSet.getString("surname"));
                attendance.setStudentEmail(resultSet.getString("email"));
                attendance.setStudentStudyCourse(resultSet.getString("studyCourse"));
                attendance.setMark(Mark.getMarkFromInt(resultSet.getInt("mark")));
                attendance.setEvaluationStatus(EvaluationStatus.getEvaluationStatusFromInt(resultSet.getInt("evaluationStatus")));
                attendances.add(attendance);
            }
        } catch (SQLException e) {
            closeResultAndStatement(resultSet, preparedStatement);

            throw new UnavailableException(e.getMessage());
        }

        closeResultAndStatement(resultSet, preparedStatement);

        return attendances;
    }

    public boolean canBePublished(int roundId) throws UnavailableException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet;
        try {
            preparedStatement = connection.prepareStatement(CAN_BE_PUBLISHED);
            preparedStatement.setInt(1, roundId);
            resultSet = preparedStatement.executeQuery();
            return resultSet.isBeforeFirst();
        }
        catch (SQLException ex){
            throw new UnavailableException(ex.getMessage());
        }
    }

    public AttendanceBean getAttendance(int roundId, int studentId) throws UnavailableException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        AttendanceBean attendance = null;
        try {
            preparedStatement = connection.prepareStatement(GET_ATTENDANCE_BY_ROUND_ID_AND_STUDENT_ID);
            preparedStatement.setInt(1, roundId);
            preparedStatement.setInt(2, studentId);

            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                attendance = new AttendanceBean();
                attendance.setStudentId(resultSet.getInt("studentId"));
                attendance.setRoundId(roundId);
                attendance.setStudentName(resultSet.getString("name"));
                attendance.setStudentSurname(resultSet.getString("surname"));
                attendance.setStudentEmail(resultSet.getString("email"));
                attendance.setStudentStudyCourse(resultSet.getString("studyCourse"));
                attendance.setMark(Mark.getMarkFromInt(resultSet.getInt("mark")));
                attendance.setEvaluationStatus(EvaluationStatus.getEvaluationStatusFromInt(resultSet.getInt("evaluationStatus")));
            }
        } catch (SQLException e) {
            closeResultAndStatement(resultSet, preparedStatement);
            throw new UnavailableException(e.getMessage());
        }

        closeResultAndStatement(resultSet, preparedStatement);

        return attendance;
    }

    public void assignMark(int roundId, int studentId, int markValue) throws UnavailableException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(ASSIGN_MARK);
            preparedStatement.setObject(1, markValue == 0 ? null:markValue);
            preparedStatement.setInt(2, markValue == 0 ? 0:1);
            preparedStatement.setInt(3, roundId);
            preparedStatement.setInt(4, studentId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            closeStatement(preparedStatement);
            throw new UnavailableException(e.getMessage());
        }

        closeStatement(preparedStatement);
    }

    public void publishMarks(int roundId) throws UnavailableException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(PUBLISH_MARKS);
            preparedStatement.setInt(1, roundId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            closeStatement(preparedStatement);
            throw new UnavailableException(e.getMessage());
        }

        closeStatement(preparedStatement);
    }

    public void refuseMark(int roundId, int studentId) throws UnavailableException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(REFUSE_MARK);
            preparedStatement.setInt(1, studentId);
            preparedStatement.setInt(2, roundId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            closeStatement(preparedStatement);
            throw new UnavailableException(e.getMessage());
        }

        closeStatement(preparedStatement);
    }


    void deleteAttendancesForNextRounds() throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_FURTHER_ATTENDANCES);
        preparedStatement.executeUpdate();
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
