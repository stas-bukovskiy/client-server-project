package org.goodstorage.repository;

import lombok.extern.slf4j.Slf4j;
import org.goodstorage.domain.Good;
import org.goodstorage.domain.Group;
import org.goodstorage.exceptions.AlreadyExistException;
import org.goodstorage.exceptions.DatabaseException;
import org.goodstorage.exceptions.ResponseStatusException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class GoodRepositoryImpl implements GoodRepository {
    private final Connection connection;

    public GoodRepositoryImpl(Connection connection) {
        this.connection = connection;
    }


    @Override
    public List<Good> findAll() {
        List<Good> goods = new ArrayList<>();
        String sql = """
                SELECT g.*, gr.id AS groupId, gr.name AS groupName, gr.description AS groupDescription, gr.createdAt AS groupCreatedAt, gr.updatedAt AS groupUpdatedAt
                FROM good g
                JOIN "Group" gr ON g.groupId = gr.id
                """;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Good good = mapRowToDomain(resultSet);
                goods.add(good);
            }
        } catch (SQLException e) {
            log.error("Error occurred while good finding:", e);
            throw new DatabaseException(e);
        }

        return goods;
    }

    @Override
    public List<Good> findAllByGroupId(String groupId) {
        List<Good> goods = new ArrayList<>();
        String sql = """
                SELECT g.*, gr.id AS groupId, gr.name AS groupName, gr.description AS groupDescription, gr.createdAt AS groupCreatedAt, gr.updatedAt AS groupUpdatedAt
                FROM Good g
                JOIN "Group" gr ON g.groupId = gr.id
                WHERE g.groupId = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, UUID.fromString(groupId));

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Good good = mapRowToDomain(resultSet);
                goods.add(good);
            }
        } catch (SQLException e) {
            log.error("Error occurred while good finding:", e);
            throw new DatabaseException(e);
        }

        return goods;
    }

    @Override
    public List<Good> searchGoods(String expression) {
        List<Good> goods = new ArrayList<>();
        String sql = """
                SELECT g.*, gr.id AS groupId, gr.name AS groupName, gr.description AS groupDescription, gr.createdAt AS groupCreatedAt, gr.updatedAt AS groupUpdatedAt
                FROM Good g
                JOIN "Group" gr ON g.groupId = gr.id
                WHERE LOWER(g.name) LIKE ? OR LOWER(g.description) LIKE ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            String searchExpression = "%" + expression.toLowerCase() + "%";
            statement.setString(1, searchExpression);
            statement.setString(2, searchExpression);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Good good = mapRowToDomain(resultSet);
                goods.add(good);
            }
        } catch (SQLException e) {
            log.error("Error occurred while good searching by expression <{}>:", expression, e);
            throw new DatabaseException(e);
        }
        return goods;
    }


    @Override
    public Optional<Good> findById(String id) {
        String sql = """
                SELECT g.*, gr.id AS groupId, gr.name AS groupName, gr.description AS groupDescription, gr.createdAt AS groupCreatedAt, gr.updatedAt AS groupUpdatedAt
                FROM Good g
                JOIN "Group" gr ON g.groupId = gr.id
                WHERE g.id = ?
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, UUID.fromString(id));


            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Good good = mapRowToDomain(resultSet);
                    return Optional.of(good);
                }
            }
        } catch (SQLException e) {
            log.error("Error occurred while good finding by id <{}>:", id, e);
            throw new DatabaseException(e);
        }

        return Optional.empty();
    }

    @Override
    public boolean existsByName(String name) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM Good WHERE name = ?")) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            log.error("Error occurred while good existing check by name <{}>:", name, e);
            throw new DatabaseException(e);
        }
        return false;
    }

    @Override
    public boolean existsById(String id) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM Good WHERE id = ?")) {
            statement.setObject(1, UUID.fromString(id));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            log.error("Error occurred while good existing check by id <{}>:", id, e);
            throw new DatabaseException(e);
        }
        return false;
    }

    @Override
    public boolean existsByNameAndIdIsNot(String name, String id) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM Good WHERE name = ? AND id != ?")) {
            statement.setString(1, name);
            statement.setObject(2, UUID.fromString(id));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            log.error("Error occurred while good existing check by id <{}>:", id, e);
            throw new DatabaseException(e);
        }
        return false;
    }

    @Override
    public Good save(Good goodToSave) {
        try {
            String sql = """
                    INSERT INTO Good (name, description, producer, price, quantity, createdAt, updatedAt, groupId)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                setGoodData(goodToSave, statement);

                statement.executeUpdate();

                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    String generatedId = generatedKeys.getString(1);
                    goodToSave.setId(generatedId);
                }
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals(AlreadyExistException.SQL_ALREADY_EXISTS_STATE)) {
                throw new AlreadyExistException("Good already exists with name <%s>", goodToSave.getName());
            } else {
                log.error("Error occurred while good <{}> saving:", goodToSave, e);
                throw new DatabaseException(e);
            }
        }
        return goodToSave;
    }

    @Override
    public Good update(Good goodToUpdate) {
        String sql = "UPDATE Good SET name = ?, description = ?, producer = ?, price = ?, quantity = ?, createdAt = ?, updatedAt = ?, groupId = ? " +
                "WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setGoodData(goodToUpdate, statement);
            statement.setObject(9, UUID.fromString(goodToUpdate.getId()));

            statement.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState().equals(AlreadyExistException.SQL_ALREADY_EXISTS_STATE)) {
                throw new AlreadyExistException("Good already exists with name <%s>", goodToUpdate.getName());
            } else {
                log.error("Error occurred while good <{}> updating:", goodToUpdate, e);
                throw new DatabaseException(e);
            }
        }
        return goodToUpdate;
    }

    private void setGoodData(Good good, PreparedStatement statement) throws SQLException {
        statement.setString(1, good.getName());
        statement.setString(2, good.getDescription());
        statement.setString(3, good.getProducer());
        statement.setBigDecimal(4, good.getPrice());
        statement.setInt(5, good.getQuantity());
        statement.setTimestamp(6, good.getCreatedAt());
        statement.setTimestamp(7, good.getUpdatedAt());
        statement.setObject(8, UUID.fromString(good.getGroup().getId()));
    }

    @Override
    public void delete(String id) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Good WHERE id = ?")) {
            statement.setObject(1, UUID.fromString(id));
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Error occurred while good deleting by id <{}>:", id, e);
            throw new DatabaseException(e);
        }
    }

    @Override
    public void addQuantity(String id, int quantityToAdd) {
        try {
            setAutoCommit(false);

            int currentQuantity = getProductQuantity(id);
            int newQuantity = currentQuantity + quantityToAdd;
            updateProductQuantity(id, newQuantity);

            connection.commit();
        } catch (SQLException e) {
            rollbackTransaction();
            e.printStackTrace();
        } finally {
            setAutoCommit(true);
        }
    }

    @Override
    public void writeOffQuantity(String id, int quantityToWriteOff) {
        try {
            setAutoCommit(false);

            int currentQuantity = getProductQuantity(id);
            int newQuantity = currentQuantity - quantityToWriteOff;
            if (newQuantity < 0) {
                throw new ResponseStatusException(404, "Insufficient quantity for product with id: " + id);
            }

            updateProductQuantity(id, newQuantity);
            connection.commit();
        } catch (SQLException e) {
            rollbackTransaction();
            e.printStackTrace();
        } finally {
            setAutoCommit(true);
        }
    }

    private int getProductQuantity(String id) throws SQLException {
        int quantity = 0;
        String query = "SELECT quantity FROM Good WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, UUID.fromString(id));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    quantity = resultSet.getInt("quantity");
                }
            }
        }

        return quantity;
    }

    private void updateProductQuantity(String id, int quantity) throws SQLException {
        String query = "UPDATE Good SET quantity = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, quantity);
            statement.setObject(2, UUID.fromString(id));
            statement.executeUpdate();
        }
    }

    private void rollbackTransaction() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setAutoCommit(boolean autoCommit) {
        try {
            connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Good mapRowToDomain(ResultSet resultSet) throws SQLException {
        Group group = Group.builder()
                .id(resultSet.getString("groupId"))
                .name(resultSet.getString("groupName"))
                .description(resultSet.getString("groupDescription"))
                .createdAt(resultSet.getTimestamp("groupCreatedAt"))
                .updatedAt(resultSet.getTimestamp("groupUpdatedAt"))
                .build();
        return Good.builder()
                .id(resultSet.getString("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .producer(resultSet.getString("producer"))
                .price(resultSet.getBigDecimal("price"))
                .quantity(resultSet.getInt("quantity"))
                .createdAt(resultSet.getTimestamp("createdAt"))
                .updatedAt(resultSet.getTimestamp("updatedAt"))
                .group(group)
                .build();
    }
}

