package edu.clientserver.repository;

import edu.clientserver.domain.Good;
import edu.clientserver.domain.Group;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GoodMapper {
    public Good mapResultSetToGood(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong(1);
        String name = resultSet.getString(2);
        Integer quantity = resultSet.getInt(3);
        BigDecimal price = resultSet.getBigDecimal(4);
        Long groupId = resultSet.getLong(5);
        String groupName = resultSet.getString(6);
        Group group = new Group(groupId, groupName);
        return new Good(id, name, quantity, price, group);
    }
}
