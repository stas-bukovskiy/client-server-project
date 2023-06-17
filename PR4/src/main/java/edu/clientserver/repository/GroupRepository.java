package edu.clientserver.repository;

import edu.clientserver.connection.ConnectionFactory;
import edu.clientserver.domain.Group;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupRepository {

    private final Connection connection;
    private final GroupMapper mapper;

    public GroupRepository() {
        connection = ConnectionFactory.getConnection();
        mapper = new GroupMapper();
    }

    public Group create(Group group) throws SQLException {
        String sql = "INSERT INTO good_group (name) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, group.getName());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating Group failed, no rows affected.");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    group.setId(generatedId);
                } else {
                    throw new SQLException("Creating Group failed, no ID obtained.");
                }
            }
        }
        return group;
    }

    public Group read(Long groupId) throws SQLException {
        String sql = "SELECT id, name FROM good_group WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, groupId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapper.map(resultSet);
                }
            }
        }
        return null;
    }

    public int update(Group group) throws SQLException {
        String sql = "UPDATE good_group SET name = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, group.getName());
            statement.setLong(2, group.getId());
            return statement.executeUpdate();
        }
    }

    public int delete(Long groupId) throws SQLException {
        String sql = "DELETE FROM good_group WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, groupId);
            return statement.executeUpdate();
        }
    }

    public List<Group> listByCriteria(String criteria) throws SQLException {
        List<Group> groups = new ArrayList<>();
        String sql = "SELECT id, name FROM good_group WHERE " + criteria;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Group group = mapper.map(resultSet);
                groups.add(group);
            }
        }
        return groups;
    }
}