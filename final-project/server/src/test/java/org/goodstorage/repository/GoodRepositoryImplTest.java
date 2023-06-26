package org.goodstorage.repository;

import org.goodstorage.domain.Good;
import org.goodstorage.domain.Group;
import org.goodstorage.exceptions.ResponseStatusException;
import org.goodstorage.util.RandomUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GoodRepositoryImplTest extends AbstractContainerDatabaseTest {

    static GoodRepository goodRepository = new GoodRepositoryImpl(connection);

    Group group;


    @BeforeEach
    void setUp() {
        group = RandomUtil.radndomGroup();
        String sql = "INSERT INTO \"Group\" (name, description, createdAt, updatedAt) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, group.getName());
            statement.setString(2, group.getDescription());
            statement.setTimestamp(3, group.getCreatedAt());
            statement.setTimestamp(4, group.getUpdatedAt());

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                String generatedId = generatedKeys.getString(1);
                group.setId(generatedId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.createStatement().execute("DELETE FROM Good");
        connection.createStatement().execute("DELETE FROM \"Group\"");
    }

    @Test
    void testSave() {
        Good good = RandomUtil.randomGood(group);

        Good savedGood = goodRepository.save(good);

        assertNotNull(savedGood.getId());
    }

    @Test
    void testFindById() {
        Good good = goodRepository.save(RandomUtil.randomGood(group));

        Optional<Good> retrievedGood = goodRepository.findById(good.getId());

        assertTrue(retrievedGood.isPresent());
        assertEquals(good, retrievedGood.get());
    }

    @Test
    void testFindAll() {
        Good good1 = goodRepository.save(RandomUtil.randomGood(group));
        Good good2 = goodRepository.save(RandomUtil.randomGood(group));
        Good good3 = goodRepository.save(RandomUtil.randomGood(group));

        List<Good> goods = goodRepository.findAll();

        assertEquals(3, goods.size());
        assertTrue(goods.contains(good1));
        assertTrue(goods.contains(good2));
        assertTrue(goods.contains(good3));
    }

    @Test
    void testUpdate() {
        Good good = goodRepository.save(RandomUtil.randomGood(group));

        good.setName(RandomUtil.randomString(10));
        good.setDescription(RandomUtil.randomString(100));
        good.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        goodRepository.update(good);
        Optional<Good> retrievedGood = goodRepository.findById(good.getId());

        assertTrue(retrievedGood.isPresent());
        assertEquals(good, retrievedGood.get());
    }

    @Test
    void testDelete() {
        Good good = goodRepository.save(RandomUtil.randomGood(group));

        goodRepository.delete(good.getId());

        Optional<Good> retrievedGood = goodRepository.findById(good.getId());
        assertFalse(retrievedGood.isPresent());
    }

    @Test
    void testAddQuantityToExistingProduct() {
        Good good = goodRepository.save(RandomUtil.randomGood(group));

        goodRepository.addQuantity(good.getId(), 5);
        Optional<Good> updatedGood = goodRepository.findById(good.getId());

        assertTrue(updatedGood.isPresent());
        assertEquals(good.getQuantity() + 5, updatedGood.get().getQuantity());
    }

    @Test
    void testWriteOffQuantityToExistingProduct() {
        final int dQuantity = 89;
        final int initialQuantity = 100;
        Good good = RandomUtil.randomGood(group);
        good.setQuantity(initialQuantity);
        good = goodRepository.save(good);

        goodRepository.writeOffQuantity(good.getId(), dQuantity);
        Optional<Good> updatedGood = goodRepository.findById(good.getId());

        assertTrue(updatedGood.isPresent());
        assertEquals(good.getQuantity() - dQuantity, updatedGood.get().getQuantity());
    }

    @Test
    void testWriteOffQuantity() {
        final int dQuantity = 101;
        final int initialQuantity = 100;
        Good good = RandomUtil.randomGood(group);
        good.setQuantity(initialQuantity);
        good = goodRepository.save(good);

        Good finalGood = good;
        assertThrows(ResponseStatusException.class, () -> goodRepository.writeOffQuantity(finalGood.getId(), dQuantity));
    }

}
