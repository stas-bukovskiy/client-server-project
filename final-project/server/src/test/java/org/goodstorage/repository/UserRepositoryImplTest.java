package org.goodstorage.repository;

import org.goodstorage.domain.User;
import org.goodstorage.exceptions.AlreadyExistException;
import org.goodstorage.util.RandomUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryImplTest extends AbstractContainerDatabaseTest {

    static UserRepository userRepository = new UserRepositoryImpl(connection);

    @AfterEach
    void tearDown() throws SQLException {
        connection.createStatement().execute("DELETE FROM \"User\"");
    }

    @Test
    void testExistsById() {
        User user = userRepository.save(RandomUtil.radnomUser());

        assertTrue(userRepository.existsById(user.getId()));
        assertFalse(userRepository.existsById(UUID.randomUUID().toString()));
    }

    @Test
    void testExistsByUsername() {
        User user = userRepository.save(RandomUtil.radnomUser());

        assertTrue(userRepository.existsByUsername(user.getUsername()));
        assertFalse(userRepository.existsByUsername(RandomUtil.randomString(10)));
    }

    @Test
    void testFindByUsername() {
        User user = userRepository.save(RandomUtil.radnomUser());

        Optional<User> retrievedUser = userRepository.findByUsername(user.getUsername());

        assertTrue(retrievedUser.isPresent());
        assertEquals(user, retrievedUser.get());
    }

    @Test
    void testFindById() {
        User user = userRepository.save(RandomUtil.radnomUser());

        Optional<User> retrievedUser = userRepository.findById(user.getId());

        assertTrue(retrievedUser.isPresent());
        assertEquals(user, retrievedUser.get());
    }

    @Test
    void testSave() {
        User user = RandomUtil.radnomUser();

        User savedUser = userRepository.save(user);
        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());

        assertTrue(retrievedUser.isPresent());
        assertEquals(savedUser, retrievedUser.get());
    }

    @Test
    void testUpdate() {
        User user = userRepository.save(RandomUtil.radnomUser());

        user.setFullName(RandomUtil.randomString(15));
        user.setUsername(RandomUtil.randomString(10));
        user.setPassword(RandomUtil.randomString(8));
        userRepository.update(user);

        Optional<User> retrievedUser = userRepository.findById(user.getId());

        assertTrue(retrievedUser.isPresent());
        assertEquals(user, retrievedUser.get());
    }

    @Test
    void testDelete() {
        User user = userRepository.save(RandomUtil.radnomUser());

        userRepository.delete(user.getId());

        Optional<User> retrievedUser = userRepository.findById(user.getId());
        assertFalse(retrievedUser.isPresent());
    }

    @Test
    void testSave_WithDuplicateUsername_ThrowsException() {
        User user1 = userRepository.save(RandomUtil.radnomUser());
        User user2 = RandomUtil.radnomUser();
        user2.setUsername(user1.getUsername());

        assertThrows(AlreadyExistException.class, () -> userRepository.save(user2));
    }
}
    

