package edu.clientserver.repository;

import edu.clientserver.connection.ConnectionFactory;
import edu.clientserver.domain.Group;
import edu.clientserver.util.RandomUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroupRepositoryTest {

    private static GroupRepository groupRepository;
    private static Connection connection;

    @BeforeAll
    public static void setup() {
        groupRepository = new GroupRepository();
        connection = ConnectionFactory.getConnection();
    }

    @AfterAll
    public static void cleanup() throws SQLException {
        connection.close();
    }

    @BeforeEach
    public void clearData() throws SQLException {
        truncateTable();
    }

    private void truncateTable() throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.executeUpdate("TRUNCATE TABLE good_group CASCADE");
        }
    }

    @Test
    public void testCreate() throws SQLException {
        Group group = RandomUtil.randomGroup();
        Group createdGroup = groupRepository.create(group);

        assertNotNull(createdGroup.getId());
        assertEquals(group.getName(), createdGroup.getName());
    }

    @Test
    public void testRead() throws SQLException {
        Group createdGroup = groupRepository.create(RandomUtil.randomGroup());

        Group retrievedGroup = groupRepository.read(createdGroup.getId());

        assertNotNull(retrievedGroup);
        assertEquals(createdGroup.getId(), retrievedGroup.getId());
        assertEquals(createdGroup.getName(), retrievedGroup.getName());
    }

    @Test
    public void testUpdate() throws SQLException {
        Group createdGroup = groupRepository.create(RandomUtil.randomGroup());

        createdGroup.setName("Updated Group");
        groupRepository.update(createdGroup);
        Group updatedGroup = groupRepository.read(createdGroup.getId());

        assertEquals(createdGroup.getName(), updatedGroup.getName());
    }

    @Test
    public void testDelete() throws SQLException {
        Group createdGroup = groupRepository.create(RandomUtil.randomGroup());

        groupRepository.delete(createdGroup.getId());

        Group deletedGroup = groupRepository.read(createdGroup.getId());

        assertNull(deletedGroup);
    }

    @Test
    public void testListAll() throws SQLException {
        groupRepository.create(RandomUtil.randomGroup());
        groupRepository.create(RandomUtil.randomGroup());
        groupRepository.create(RandomUtil.randomGroup());

        List<Group> groups = groupRepository.listByCriteria("1 = 1");

        assertEquals(3, groups.size());
    }
}