package com.example.project2023shpakovtischer.dao;

import com.example.project2023shpakovtischer.javaBeans.ReportBean;

import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.SQLException;

public class ReportDAO {
    private Connection connection;

    public ReportDAO(Connection connection) {
        this.connection = connection;
    }

    public ReportBean createReport(int roundId) throws UnavailableException, SQLException {
        RoundDAO roundDAO = new RoundDAO(connection);
        AttendanceDAO attendanceDAO = new AttendanceDAO(connection);
        ReportBean report;

        try {
            connection.setAutoCommit(false);
            report = roundDAO.createReport(roundId);
            attendanceDAO.verbalizeMarks(roundId);
            connection.commit();
        } catch (UnavailableException e) {
            connection.rollback();
            throw e;
        } catch (SQLException e) {
            connection.rollback();
            throw new UnavailableException(e.getMessage());
        }

        connection.setAutoCommit(true);
        return report;
    }
}
