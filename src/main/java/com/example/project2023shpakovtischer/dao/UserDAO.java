package com.example.project2023shpakovtischer.dao;

import com.example.project2023shpakovtischer.enums.UserRole;
import com.example.project2023shpakovtischer.javaBeans.UserBean;

import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    private Connection connection;

    public UserDAO(Connection connection){
        this.connection = connection;
    }

    private static final String GET_USER_BY_EMAIL = "SELECT * FROM user WHERE email = ?";
    private static final String GET_USER_BY_ID = "SELECT * FROM user WHERE userId = ?";

    public UserBean getUserByEmail(String email) throws UnavailableException {
        UserBean user = new UserBean();
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_BY_EMAIL)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user.setUserId(resultSet.getInt("userId"));
                user.setName(resultSet.getString("name"));
                user.setSurname(resultSet.getString("surname"));
                user.setEmail(resultSet.getString("email"));
                user.setRole(UserRole.getUserRoleFromInt(resultSet.getInt("role")));
                user.setStudyCourse(resultSet.getString("studyCourse"));
            }
            else{
                return null;
            }
        } catch (SQLException e) {
            throw new UnavailableException("Error with SQL");
        }
        return user;
    }

    public UserBean getUserById(int id) throws UnavailableException {
        UserBean user = new UserBean();
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user.setUserId(resultSet.getInt("userId"));
                user.setName(resultSet.getString("name"));
                user.setSurname(resultSet.getString("surname"));
                user.setEmail(resultSet.getString("email"));
                user.setRole(UserRole.getUserRoleFromInt(resultSet.getInt("role")));
                user.setStudyCourse(resultSet.getString("studyCourse"));
            }
            else{
                return null;
            }
        } catch (SQLException e) {
            throw new UnavailableException("Error with SQL");
        }
        return user;
    }
}
