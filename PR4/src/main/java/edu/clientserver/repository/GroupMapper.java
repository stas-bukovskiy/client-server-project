package edu.clientserver.repository;

import edu.clientserver.domain.Group;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GroupMapper {
    public Group map(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        return new Group(id, name);
    }
}
