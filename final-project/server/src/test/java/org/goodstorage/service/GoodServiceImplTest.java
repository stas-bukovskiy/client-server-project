package org.goodstorage.service;

import org.goodstorage.domain.Good;
import org.goodstorage.exceptions.NotFoundException;
import org.goodstorage.repository.GoodRepository;
import org.goodstorage.util.RandomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoodServiceImplTest {
    @Mock
    private GoodRepository goodRepository;
    @Mock
    private GroupService groupService;

    private GoodService goodService;

    @BeforeEach
    void setup() {
        goodService = new GoodServiceImpl(goodRepository, groupService);
    }

    @Test
    void testGetGoods() {
        List<Good> mockGoods = List.of(
                RandomUtil.randomGood(),
                RandomUtil.randomGood(),
                RandomUtil.randomGood()
        );
        when(goodRepository.findAll()).thenReturn(mockGoods);

        List<Good> result = goodService.getGoods();

        assertEquals(mockGoods, result);
    }

    @Test
    void testGetById_ExistingId() {
        Good mockGood = RandomUtil.randomGood();
        when(goodRepository.findById(mockGood.getId())).thenReturn(Optional.of(mockGood));

        Good result = goodService.getById(mockGood.getId());

        assertEquals(mockGood, result);
    }

    @Test
    void testGetById_NonExistingId() {
        String id = UUID.randomUUID().toString();
        when(goodRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> goodService.getById(id));
    }

    @Test
    void testCreate() throws SQLException {
        Good savedMockedGood = RandomUtil.randomGood();

        GoodService.GoodRequest request = new GoodService.GoodRequest();
        request.setName(savedMockedGood.getName());
        request.setDescription(savedMockedGood.getDescription());
        request.setGroupId(savedMockedGood.getGroup().getId());

        when(goodRepository.save(any())).thenReturn(savedMockedGood);
        when(groupService.getById(request.getGroupId())).thenReturn(savedMockedGood.getGroup());

        Good result = goodService.create(request);

        assertEquals(savedMockedGood, result);
        verify(goodRepository, times(1)).save(any());
        verify(groupService, times(1)).getById(any());
    }

    @Test
    void testUpdate() throws SQLException {
        Good existingMockedGood = RandomUtil.randomGood();
        Good updatedMockedGood = RandomUtil.randomGood();
        existingMockedGood.setId(updatedMockedGood.getId());
        GoodService.GoodRequest request = new GoodService.GoodRequest();
        request.setName(updatedMockedGood.getName());
        request.setDescription(updatedMockedGood.getDescription());
        request.setGroupId(updatedMockedGood.getGroup().getId());

        when(groupService.getById(request.getGroupId())).thenReturn(existingMockedGood.getGroup());
        when(goodRepository.findById(existingMockedGood.getId())).thenReturn(Optional.of(existingMockedGood));
        when(goodRepository.update(any())).thenReturn(updatedMockedGood);

        Good result = goodService.update(existingMockedGood.getId(), request);

        assertEquals(updatedMockedGood, result);
        verify(goodRepository, times(1)).findById(existingMockedGood.getId());
        verify(goodRepository, times(1)).update(any());
    }

    @Test
    void testDelete() throws SQLException {
        String id = UUID.randomUUID().toString();
        goodService.delete(id);

        verify(goodRepository, times(1)).delete(id);
    }
}
