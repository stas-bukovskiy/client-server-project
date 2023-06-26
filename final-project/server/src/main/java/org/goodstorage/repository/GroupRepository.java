package org.goodstorage.repository;

import org.goodstorage.domain.Group;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {
    List<Group> findAll();

    List<Group> searchGroups(String expression);

    Optional<Group> findById(String id);

    boolean existsByName(String name);

    boolean existsByNameAndIdIsNot(String name, String id);

    Group save(Group groupToSave);

    boolean existsById(String id);

    void delete(String id);

    Group update(Group groupToUpdate);
}
