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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

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
    void testCreate() {
        Good savedMockedGood = RandomUtil.randomGood();

        GoodService.GoodRequest request = new GoodService.GoodRequest();
        request.setName(savedMockedGood.getName());
        request.setDescription(savedMockedGood.getDescription());
        request.setGroupId(savedMockedGood.getGroup().getId());
        request.setQuantity(savedMockedGood.getQuantity());
        request.setPrice(savedMockedGood.getPrice().doubleValue());

        when(goodRepository.existsByName(savedMockedGood.getName())).thenReturn(false);
        when(goodRepository.save(any())).thenReturn(savedMockedGood);
        when(groupService.getGroupById(request.getGroupId())).thenReturn(savedMockedGood.getGroup());

        Good result = goodService.create(request);

        assertEquals(savedMockedGood, result);
    }

}
