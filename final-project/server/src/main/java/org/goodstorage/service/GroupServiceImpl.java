package org.goodstorage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goodstorage.domain.Group;
import org.goodstorage.exceptions.AlreadyExistException;
import org.goodstorage.exceptions.NotFoundException;
import org.goodstorage.repository.GroupRepository;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    @Override
    public List<Group> getGroups() {
        return groupRepository.findAll();
    }

    @Override
    public List<Group> searchGroups(String expression) {
        return groupRepository.searchGroups(expression);
    }

    @Override
    public Group getById(String id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("group", id));
    }

    @Override
    public Group create(GroupRequest request) {
        if (groupRepository.existsByName(request.getName()))
            throw new AlreadyExistException("Group already exists with name <%s>", request.getName());

        Group groupToSave = Group.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();

        Group createdGroup = groupRepository.save(groupToSave);
        log.info("Create new group <{}> from request <{}>", createdGroup, request);
        return createdGroup;
    }

    @Override
    public Group update(String id, GroupRequest request) {
        if (groupRepository.existsByNameAndIdIsNot(request.getName(), id))
            throw new AlreadyExistException("Group already exists with name <%s>", request.getName());

        Group groupToUpdate = getById(id);
        groupToUpdate.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        groupToUpdate.setName(request.getName());
        groupToUpdate.setDescription(request.getDescription());

        Group updatedGroup = groupRepository.update(groupToUpdate);
        log.info("Updated group <{}> from request <{}>", updatedGroup, request);
        return updatedGroup;

    }

    @Override
    public void delete(String id) {
        if (!groupRepository.existsById(id))
            throw new NotFoundException("group", id);

        groupRepository.delete(id);
        log.info("Deleted group with id <{}>", id);
    }
}
