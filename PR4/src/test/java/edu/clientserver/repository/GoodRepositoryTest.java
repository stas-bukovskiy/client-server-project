package edu.clientserver.repository;

import edu.clientserver.connection.ConnectionFactory;
import edu.clientserver.domain.Good;
import edu.clientserver.domain.Group;
import edu.clientserver.util.RandomUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GoodRepositoryTest {

    private static GoodRepository goodRepository;
    private static GroupRepository groupRepository;
    private static Connection connection;

    @BeforeAll
    static void beforeAll() {
        goodRepository = new GoodRepository();
        groupRepository = new GroupRepository();
        connection = ConnectionFactory.getConnection();
    }

    @AfterAll
    public static void cleanup() throws SQLException {
        connection.close();
    }

    @BeforeEach
    void setUp() throws SQLException {
        truncateTables();
    }

    private void truncateTables() throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.executeUpdate("TRUNCATE TABLE good CASCADE");
            statement.executeUpdate("TRUNCATE TABLE good_group CASCADE");
        }
    }

    @Test
    public void testCreate() throws SQLException {
        Group group = groupRepository.create(RandomUtil.randomGroup());
        Good good = RandomUtil.radnomGood(group);

        Good createdGood = goodRepository.create(good);

        assertNotNull(createdGood.getId());
        assertEquals(good.getName(), createdGood.getName());
        assertEquals(good.getQuantity(), createdGood.getQuantity());
        assertEquals(good.getPrice(), createdGood.getPrice());
        assertEquals(good.getGroup(), createdGood.getGroup());
    }

    @Test
    public void testRead() throws SQLException {
        Group group = groupRepository.create(RandomUtil.randomGroup());
        Good good = RandomUtil.radnomGood(group);
        Good createdGood = goodRepository.create(good);

        Good retrievedGood = goodRepository.read(createdGood.getId());

        assertNotNull(retrievedGood);
        assertEquals(createdGood.getId(), retrievedGood.getId());
        assertEquals(createdGood.getName(), retrievedGood.getName());
        assertEquals(createdGood.getQuantity(), retrievedGood.getQuantity());
        assertEquals(createdGood.getPrice(), retrievedGood.getPrice());
        assertEquals(createdGood.getGroup(), retrievedGood.getGroup());
    }

    @Test
    public void testUpdate() throws SQLException {
        Group group = groupRepository.create(RandomUtil.randomGroup());
        Good good = RandomUtil.radnomGood(group);
        Good createdGood = goodRepository.create(good);

        createdGood.setName("Updated Good");
        createdGood.setQuantity(20);
        createdGood.setPrice(new BigDecimal("25.75"));
        goodRepository.update(createdGood);

        Good updatedGood = goodRepository.read(createdGood.getId());

        assertEquals(createdGood.getName(), updatedGood.getName());
        assertEquals(createdGood.getQuantity(), updatedGood.getQuantity());
        assertEquals(createdGood.getPrice(), updatedGood.getPrice());
    }

    @Test
    public void testDelete() throws SQLException {
        Group group = groupRepository.create(RandomUtil.randomGroup());
        Good good = RandomUtil.radnomGood(group);
        Good createdGood = goodRepository.create(good);

        goodRepository.delete(createdGood.getId());
        Good deletedGood = goodRepository.read(createdGood.getId());

        assertNull(deletedGood);
    }

    @Test
    public void testListByCriteria() throws SQLException {
        Group groupA = groupRepository.create(RandomUtil.randomGroup());
        Group groupB = groupRepository.create(RandomUtil.randomGroup());


        Good good1 = RandomUtil.radnomGood(groupA);
        goodRepository.create(good1);

        Good good2 = RandomUtil.radnomGood(groupB);
        goodRepository.create(good2);

        Good good3 = RandomUtil.radnomGood(groupA);
        goodRepository.create(good3);

        List<Good> goodsInGroupA = goodRepository.listByCriteria("group_id = " + groupA.getId());
        List<Good> goodsInGroupB = goodRepository.listByCriteria("group_id = " + groupB.getId());

        assertEquals(2, goodsInGroupA.size());
        assertEquals(1, goodsInGroupB.size());
    }
}