package com.example.project2023shpakovtischer.dao;

import com.example.project2023shpakovtischer.enums.EvaluationStatus;
import com.example.project2023shpakovtischer.enums.Mark;
import com.example.project2023shpakovtischer.javaBeans.AttendanceBean;

import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {
    private Connection connection;

    private static final String GET_ATTENDANCES_BY_ROUND_ID_ORDERED_BY = "SELECT studentId, name, surname, email, studyCourse, mark, evaluationStatus " +
            "FROM attends join user on studentId = userId WHERE roundId = ? ";

    private static final String GET_ATTENDANCE_BY_ROUND_ID_AND_STUDENT_ID = "SELECT studentId, name, surname, email, studyCourse, mark, evaluationStatus " +
            "FROM attends join user on studentId = userId WHERE roundId = ? AND studentId = ?";

    private static final String ASSIGN_MARK = "UPDATE attends SET mark = ?, evaluationStatus = 1 WHERE roundId = ? AND studentId = ?";

    private static final String PUBLISH_MARKS = "UPDATE attends SET evaluationStatus = 2 WHERE roundId = ? and evaluationStatus = 1";

    private static final String REFUSE_MARK = "UPDATE attends SET evaluationStatus = 3 WHERE studentId = ? AND roundId = ?";

    private static final String VERBALIZE_MARKS = "UPDATE attends SET evaluationStatus = 4 WHERE roundId = ? and ( evaluationStatus = 2 or evaluationStatus = 3 )";

    private static final String UPDATE_MARK_ON_REFUSED_ATTENDANCES = "UPDATE attends SET mark = 16 WHERE roundId = ?";

    private static final String CREATE_VIEW_ROUNDS_ON_NEXT_DATES = "CREATE OR REPLACE VIEW SameCourseNextDate AS " +
            "SELECT roundId FROM round WHERE courseId = (SELECT courseId FROM round WHERE roundId = ?) AND date > (SELECT date FROM round WHERE roundId = ?) ";

    private static final String DELETE_FURTHER_ATTENDANCES = "DELETE FROM attends WHERE (studentId, roundId ) in (SELECT studentId, roundId FROM attends join SameCourseNextDate S on roundId = S.roundId WHERE roundId = ? and mark between 18 and 31)";


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
                attendance.setRoundId(resultSet.getInt("roundId"));
                attendance.setName(resultSet.getString("name"));
                attendance.setSurname(resultSet.getString("surname"));
                attendance.setEmail(resultSet.getString("email"));
                attendance.setStudyCourse(resultSet.getString("studyCourse"));
                attendance.setMark(Mark.getUserRoleFromInt(resultSet.getInt("mark")));
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
                attendance.setRoundId(resultSet.getInt("roundId"));
                attendance.setName(resultSet.getString("name"));
                attendance.setSurname(resultSet.getString("surname"));
                attendance.setEmail(resultSet.getString("email"));
                attendance.setStudyCourse(resultSet.getString("studyCourse"));
                attendance.setMark(Mark.getUserRoleFromInt(resultSet.getInt("mark")));
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
            preparedStatement.setInt(1, markValue);
            preparedStatement.setInt(2, roundId);
            preparedStatement.setInt(3, studentId);
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

    void verbalizeMarks(int roundId) throws UnavailableException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(UPDATE_MARK_ON_REFUSED_ATTENDANCES);
            preparedStatement.setInt(1, roundId);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement(VERBALIZE_MARKS);
            preparedStatement.setInt(1, roundId);
            preparedStatement.executeUpdate();

            deleteAttendancesForNextRounds(roundId);
        }
        catch (SQLException e) {
            closeStatement(preparedStatement);
            throw new UnavailableException(e.getMessage());
        }

        closeStatement(preparedStatement);
    }

    private void deleteAttendancesForNextRounds(int roundId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(CREATE_VIEW_ROUNDS_ON_NEXT_DATES);
        preparedStatement.setInt(1, roundId);
        preparedStatement.setInt(2, roundId);
        preparedStatement.executeUpdate();

        preparedStatement = connection.prepareStatement(DELETE_FURTHER_ATTENDANCES);
        preparedStatement.setInt(1, roundId);
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
