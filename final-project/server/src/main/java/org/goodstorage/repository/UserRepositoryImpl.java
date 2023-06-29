package org.goodstorage.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goodstorage.domain.User;
import org.goodstorage.exceptions.AlreadyExistException;
import org.goodstorage.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final Connection connection;

    private static User mapRowToDomain(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getString("id"))
                .fullName(resultSet.getString("fullName"))
                .username(resultSet.getString("username"))
                .password(resultSet.getString("password"))
                .role(resultSet.getString("role"))
                .updatedAt(resultSet.getTimestamp("updatedAt"))
                .createdAt(resultSet.getTimestamp("createdAt"))
                .build();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = """
                SELECT *
                FROM "User"
                """;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                User user = mapRowToDomain(resultSet);
                users.add(user);
            }
        } catch (SQLException e) {
            log.error("Error occurred while user finding:", e);
            throw new DatabaseException(e);
        }
        return users;
    }

    @Override
    public boolean existsById(String id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM \"User\" WHERE id = ?");
            statement.setObject(1, UUID.fromString(id));
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            return count > 0;
        } catch (SQLException e) {
            log.error("Error occurred while user existing check by id <{}>:", id, e);
            throw new DatabaseException(e);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM \"User\" WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            return count > 0;
        } catch (SQLException e) {
            log.error("Error occurred while user existing check by username <{}>:", username, e);
            throw new DatabaseException(e);
        }
    }

    @Override
    public boolean existsByUsernameAndIdIsNot(String username, String id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM \"User\" WHERE username = ? AND id != ?");
            statement.setString(1, username);
            statement.setObject(2, UUID.fromString(id));
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            return count > 0;
        } catch (SQLException e) {
            log.error("Error occurred while user existing check by username and id <{}>:", username, e);
            throw new DatabaseException(e);
        }

    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM \"User\" WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapRowToDomain(resultSet));
            }
        } catch (SQLException e) {
            log.error("Error occurred while user finding by username <{}>:", username, e);
            throw new DatabaseException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(String id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM \"User\" WHERE id = ?");
            statement.setObject(1, UUID.fromString(id));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapRowToDomain(resultSet));
            }
        } catch (SQLException e) {
            log.error("Error occurred while user finding by id <{}>:", id, e);
            throw new DatabaseException(e);
        }
        return Optional.empty();
    }

    @Override
    public User save(User userToCreate) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO \"User\" (fullName, username, password, role, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setString(1, userToCreate.getFullName());
            statement.setString(2, userToCreate.getUsername());
            statement.setString(3, userToCreate.getPassword());
            statement.setString(4, userToCreate.getRole());
            statement.setTimestamp(5, userToCreate.getCreatedAt());
            statement.setTimestamp(6, userToCreate.getUpdatedAt());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                userToCreate.setId(generatedKeys.getString(1));
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals(AlreadyExistException.SQL_ALREADY_EXISTS_STATE)) {
                throw new AlreadyExistException("User already exists with username <%s>", userToCreate.getUsername());
            } else {
                log.error("Error occurred while user <{}> saving:", userToCreate, e);
                throw new DatabaseException(e);
            }
        }

        return userToCreate;
    }

    @Override
    public User update(User userToUpdate) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE \"User\" SET fullName = ?, username = ?, password = ?, role = ?, updatedAt = ? WHERE id = ?"
        )) {
            statement.setString(1, userToUpdate.getFullName());
            statement.setString(2, userToUpdate.getUsername());
            statement.setString(3, userToUpdate.getPassword());
            statement.setString(4, userToUpdate.getRole());
            statement.setTimestamp(5, userToUpdate.getUpdatedAt());
            statement.setObject(6, UUID.fromString(userToUpdate.getId()));
            statement.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState().equals(AlreadyExistException.SQL_ALREADY_EXISTS_STATE)) {
                throw new AlreadyExistException("User already exists with name <%s>", userToUpdate.getUsername());
            } else {
                log.error("Error occurred while user <{}> updating:", userToUpdate, e);
                throw new DatabaseException(e);
            }
        }

        return userToUpdate;
    }

    @Override
    public void delete(String id) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM \"User\" WHERE id = ?")) {
            statement.setObject(1, UUID.fromString(id));
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Error occurred while user deleting by id <{}>:", id, e);
            throw new DatabaseException(e);
        }
    }
}
