package org.goodstorage.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goodstorage.domain.Group;
import org.goodstorage.exceptions.AlreadyExistException;
import org.goodstorage.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class GroupRepositoryImpl implements GroupRepository {

    private final Connection connection;


    @Override
    public List<Group> findAll() {
        List<Group> groups = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM \"Group\"")) {
            while (resultSet.next()) {
                Group group = mapRowToDomain(resultSet);
                groups.add(group);
            }
        } catch (SQLException e) {
            log.error("Error occurred while group finding:", e);
            throw new DatabaseException(e);
        }
        return groups;
    }

    @Override
    public List<Group> searchGroups(String expression) {
        String sql = """
                SELECT *
                FROM "Group"
                 WHERE LOWER(name) LIKE ? OR LOWER(description) LIKE ?
                """;
        List<Group> groups = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            String searchExpression = "%" + expression.toLowerCase() + "%";
            preparedStatement.setString(1, searchExpression);
            preparedStatement.setString(2, searchExpression);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Group group = mapRowToDomain(resultSet);
                groups.add(group);
            }
        } catch (SQLException e) {
            log.error("Error occurred while group searching by expression <{}>:", expression, e);
            throw new DatabaseException(e);
        }
        return groups;
    }

    @Override
    public Optional<Group> findById(String id) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM \"Group\" WHERE id = ?")) {
            statement.setObject(1, UUID.fromString(id));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Group group = mapRowToDomain(resultSet);
                return Optional.of(group);
            }
        } catch (SQLException e) {
            log.error("Error occurred while group finding by id <{}>:", id, e);
            throw new DatabaseException(e);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsByName(String name) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM \"Group\" WHERE name = ?")) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            log.error("Error occurred while group existing check by name <{}>:", name, e);
            throw new DatabaseException(e);
        }
        return false;
    }

    @Override
    public boolean existsByNameAndIdIsNot(String name, String id) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM \"Group\" WHERE name = ? AND id != ?")) {
            statement.setString(1, name);
            statement.setObject(2, UUID.fromString(id));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            log.error("Error occurred while group existing check by name <{}>:", name, e);
            throw new DatabaseException(e);
        }
        return false;
    }

    @Override
    public Group save(Group groupToSave) {
        String sql = "INSERT INTO \"Group\" (name, description, createdAt, updatedAt) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"})) {
            statement.setString(1, groupToSave.getName());
            statement.setString(2, groupToSave.getDescription());
            statement.setTimestamp(3, groupToSave.getCreatedAt());
            statement.setTimestamp(4, groupToSave.getUpdatedAt());

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                String generatedId = generatedKeys.getString(1);
                groupToSave.setId(generatedId);
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals(AlreadyExistException.SQL_ALREADY_EXISTS_STATE)) {
                throw new AlreadyExistException("Group already exists with name <%s>", groupToSave.getName());
            } else {
                log.error("Error occurred while group <{}> saving:", groupToSave, e);
                throw new DatabaseException(e);
            }
        }
        return groupToSave;
    }

    @Override
    public boolean existsById(String id) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM \"Group\" WHERE id = ?")) {
            statement.setObject(1, UUID.fromString(id));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            log.error("Error occurred while group existing check by id <{}>:", id, e);
            throw new DatabaseException(e);
        }
        return false;
    }

    @Override
    public void delete(String id) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM \"Group\" WHERE id = ?")) {
            statement.setObject(1, UUID.fromString(id));
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Error occurred while group deleting by id <{}>:", id, e);
            throw new DatabaseException(e);
        }
    }

    @Override
    public Group update(Group groupToUpdate) {
        String sql = "UPDATE \"Group\" SET name = ?, description = ?, createdAt = ?, updatedAt = ? " +
                "WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, groupToUpdate.getName());
            statement.setString(2, groupToUpdate.getDescription());
            statement.setTimestamp(3, groupToUpdate.getCreatedAt());
            statement.setTimestamp(4, groupToUpdate.getUpdatedAt());
            statement.setObject(5, UUID.fromString(groupToUpdate.getId()));

            statement.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState().equals(AlreadyExistException.SQL_ALREADY_EXISTS_STATE)) {
                throw new AlreadyExistException("Group already exists with name <%s>", groupToUpdate.getName());
            } else {
                log.error("Error occurred while group <{}> updating:", groupToUpdate, e);
                throw new DatabaseException(e);
            }
        }
        return groupToUpdate;
    }

    public Group mapRowToDomain(ResultSet resultSet) throws SQLException {
        return Group.builder()
                .id(resultSet.getString("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .createdAt(resultSet.getTimestamp("createdAt"))
                .updatedAt(resultSet.getTimestamp("updatedAt"))
                .build();
    }

}
