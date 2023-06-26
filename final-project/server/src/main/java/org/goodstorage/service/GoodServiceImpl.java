package org.goodstorage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goodstorage.domain.Good;
import org.goodstorage.domain.Group;
import org.goodstorage.exceptions.AlreadyExistException;
import org.goodstorage.exceptions.NotFoundException;
import org.goodstorage.repository.GoodRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class GoodServiceImpl implements GoodService {

    private final GoodRepository goodRepository;
    private final GroupService groupService;

    @Override
    public List<Good> getGoods() {
        return goodRepository.findAll();
    }

    @Override
    public List<Good> getGoodsByGroupId(String groupId) {
        return goodRepository.findAllByGroupId(groupId);
    }

    @Override
    public List<Good> searchGoods(String expression) {
        return goodRepository.searchGoods(expression);
    }

    @Override
    public Good getById(String id) {
        return goodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("good", id));
    }

    @Override
    public Good create(GoodRequest request) {
        if (goodRepository.existsByName(request.getName()))
            throw new AlreadyExistException("Good already exists with name <%s>", request.getName());

        Group group = groupService.getById(request.getGroupId());
        Good goodToSave = Good.builder()
                .name(request.getName())
                .description(request.getDescription())
                .producer(request.getProducer())
                .quantity(request.getQuantity())
                .price(BigDecimal.valueOf(request.getPrice()).setScale(2, RoundingMode.HALF_UP))
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .group(group)
                .build();

        Good savedGroup = goodRepository.save(goodToSave);
        log.info("Created new good <{}> from request <{}>", savedGroup, request);
        return savedGroup;
    }

    @Override
    public Good update(String id, GoodRequest request) {
        if (goodRepository.existsByNameAndIdIsNot(request.getName(), id))
            throw new AlreadyExistException("Good already exists with name <%s>", request.getName());

        Group group = groupService.getById(request.getGroupId());
        Good goodToUpdate = getById(id);
        goodToUpdate.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        goodToUpdate.setName(request.getName());
        goodToUpdate.setDescription(request.getDescription());
        goodToUpdate.setProducer(request.getProducer());
        goodToUpdate.setQuantity(request.getQuantity());
        goodToUpdate.setPrice(BigDecimal.valueOf(request.getPrice()).setScale(2, RoundingMode.HALF_UP));
        goodToUpdate.setGroup(group);

        Good updatedGroup = goodRepository.update(goodToUpdate);
        log.info("Updated good <{}> from request <{}>", updatedGroup, request);
        return updatedGroup;
    }

    @Override
    public Good addQuantity(String id, int quantityToAdd) {
        if (!goodRepository.existsById(id))
            throw new NotFoundException("good", id);

        goodRepository.addQuantity(id, quantityToAdd);
        return getById(id);
    }

    @Override
    public Good writeOffQuantity(String id, int quantityToWriteOff) {
        if (!goodRepository.existsById(id))
            throw new NotFoundException("good", id);

        goodRepository.writeOffQuantity(id, quantityToWriteOff);
        return getById(id);
    }

    @Override
    public void delete(String id) {
        if (!goodRepository.existsById(id))
            throw new NotFoundException("good", id);

        goodRepository.delete(id);
        log.info("Deleted good with id <{}>", id);
    }
}
