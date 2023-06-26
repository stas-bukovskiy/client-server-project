package org.goodstorage.repository;

import org.goodstorage.domain.Good;

import java.util.List;
import java.util.Optional;

public interface GoodRepository {
    List<Good> findAll();

    List<Good> findAllByGroupId(String groupId);

    List<Good> searchGoods(String expression);

    Optional<Good> findById(String id);

    boolean existsByName(String name);

    boolean existsById(String id);

    boolean existsByNameAndIdIsNot(String name, String id);

    Good save(Good goodToSave);

    Good update(Good goodToUpdate);

    void delete(String id);

    void addQuantity(String id, int quantityToAdd);

    void writeOffQuantity(String id, int quantityToWriteOff);
}
