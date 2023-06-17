package edu.clientserver.repository;

import edu.clientserver.connection.ConnectionFactory;
import edu.clientserver.domain.Good;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GoodRepository {

    private final Connection connection;
    private final GoodMapper goodMapper;

    public GoodRepository() {
        this.connection = ConnectionFactory.getConnection();
        this.goodMapper = new GoodMapper();
    }

    public Good create(Good good) throws SQLException {
        String sql = "INSERT INTO good (name, quantity, price, group_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, good.getName());
            statement.setInt(2, good.getQuantity());
            statement.setBigDecimal(3, good.getPrice());
            statement.setLong(4, good.getGroup().getId());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating Good failed, no rows affected.");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    good.setId(generatedId);
                } else {
                    throw new SQLException("Creating Good failed, no ID obtained.");
                }
            }
        }
        return good;
    }

    public Good read(Long goodId) throws SQLException {
        String sql = """
                SELECT g.id, g.name, g.quantity, g.price, g.group_id, gg.name as group_name
                FROM good g
                JOIN good_group gg ON g.group_id = gg.id
                WHERE g.id = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, goodId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return goodMapper.mapResultSetToGood(resultSet);
                }
            }
        }
        return null;
    }

    public int update(Good good) throws SQLException {
        String sql = "UPDATE good SET name = ?, quantity = ?, price = ?, group_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, good.getName());
            statement.setInt(2, good.getQuantity());
            statement.setBigDecimal(3, good.getPrice());
            statement.setLong(4, good.getGroup().getId());
            statement.setLong(5, good.getId());
            return statement.executeUpdate();
        }
    }

    public int delete(Long goodId) throws SQLException {
        String sql = "DELETE FROM good WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, goodId);
            return statement.executeUpdate();
        }
    }

    public List<Good> listByCriteria(String criteria) throws SQLException {
        List<Good> goods = new ArrayList<>();
        String sql = "SELECT g.id, g.name, g.quantity, g.price, g.group_id, gg.name as group_name " +
                "FROM good g " +
                "JOIN good_group gg ON g.group_id = gg.id " +
                "WHERE " + criteria;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Good good = goodMapper.mapResultSetToGood(resultSet);
                goods.add(good);
            }
        }
        return goods;
    }
}
