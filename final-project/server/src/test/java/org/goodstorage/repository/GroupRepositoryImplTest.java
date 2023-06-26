package org.goodstorage.repository;

import org.goodstorage.domain.Group;
import org.goodstorage.exceptions.AlreadyExistException;
import org.goodstorage.util.RandomUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GroupRepositoryImplTest extends AbstractContainerDatabaseTest {

    static final GroupRepository groupRepository = new GroupRepositoryImpl(connection);

    @AfterEach
    void tearDown() throws SQLException {
        connection.createStatement().execute("DELETE FROM \"Group\"");
        connection.createStatement().execute("DELETE FROM Good");
    }

    @Test
    void testSave() {
        Group group = RandomUtil.radndomGroup();

        Group savedGroup = groupRepository.save(group);

        assertNotNull(savedGroup.getId());
    }

    @Test
    void testSave_WithDuplicateName_ThrowsException() {
        Group group1 = groupRepository.save(RandomUtil.radndomGroup());
        Group group2 = RandomUtil.radndomGroup();
        group2.setName(group1.getName());

        assertThrows(AlreadyExistException.class, () -> groupRepository.save(group2));
    }

    @Test
    void testFindById() {
        Group group = groupRepository.save(RandomUtil.radndomGroup());

        Optional<Group> retrievedGroup = groupRepository.findById(group.getId());

        assertTrue(retrievedGroup.isPresent());
        assertEquals(group, retrievedGroup.get());
    }

    @Test
    void testFindAll() {
        Group group1 = groupRepository.save(RandomUtil.radndomGroup());
        Group group2 = groupRepository.save(RandomUtil.radndomGroup());

        List<Group> groups = groupRepository.findAll();

        assertEquals(2, groups.size());
        assertTrue(groups.contains(group1));
        assertTrue(groups.contains(group2));
    }

    @Test
    void testExistsByName() {
        Group group = groupRepository.save(RandomUtil.radndomGroup());

        assertTrue(groupRepository.existsByName(group.getName()));
        assertFalse(groupRepository.existsByName(RandomUtil.randomString(10)));
    }

    @Test
    void testExistsById() {
        Group group = groupRepository.save(RandomUtil.radndomGroup());

        assertTrue(groupRepository.existsById(group.getId()));
        assertFalse(groupRepository.existsById(UUID.randomUUID().toString()));
    }

    @Test
    void testUpdate() {
        Group group = groupRepository.save(RandomUtil.radndomGroup());

        group.setName(RandomUtil.randomString(10));
        group.setDescription(RandomUtil.randomString(100));
        group.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        groupRepository.update(group);

        Optional<Group> retrievedGroup = groupRepository.findById(group.getId());

        assertTrue(retrievedGroup.isPresent());
        assertEquals(group, retrievedGroup.get());
    }

    @Test
    void testDelete() {
        Group group = groupRepository.save(RandomUtil.radndomGroup());

        groupRepository.delete(group.getId());

        Optional<Group> retrievedGroup = groupRepository.findById(group.getId());
        assertFalse(retrievedGroup.isPresent());
    }

}