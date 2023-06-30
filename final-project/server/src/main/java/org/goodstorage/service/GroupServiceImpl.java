package org.goodstorage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goodstorage.domain.Group;
import org.goodstorage.exceptions.AlreadyExistException;
import org.goodstorage.exceptions.NotFoundException;
import org.goodstorage.repository.GoodRepository;
import org.goodstorage.repository.GroupRepository;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GoodRepository goodRepository;

    @Override
    public List<GroupResponse> getGroupResponses() {
        return groupRepository.findAll().stream()
                .map(group -> GroupResponseMapper.of(
                        group,
                        goodRepository.countProductsByGroupId(group.getId()),
                        goodRepository.sumProductsPriceByGroupId(group.getId())))
                .toList();
    }

    @Override
    public List<GroupResponse> searchGroupResponses(String expression) {
        return groupRepository.searchGroups(expression).stream()
                .map(group -> GroupResponseMapper.of(
                        group,
                        goodRepository.countProductsByGroupId(group.getId()),
                        goodRepository.sumProductsPriceByGroupId(group.getId())))
                .toList();
    }

    @Override
    public GroupResponse getGroupResponseById(String id) {
        return groupRepository.findById(id)
                .map(group -> GroupResponseMapper.of(
                        group,
                        goodRepository.countProductsByGroupId(group.getId()),
                        goodRepository.sumProductsPriceByGroupId(group.getId())))
                .orElseThrow(() -> new NotFoundException("group", id));
    }

    @Override
    public Group getGroupById(String id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("group", id));
    }

    @Override
    public GroupResponse create(GroupRequest request) {
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
        return GroupResponseMapper.of(
                createdGroup,
                goodRepository.countProductsByGroupId(createdGroup.getId()),
                goodRepository.sumProductsPriceByGroupId(createdGroup.getId()));
    }

    @Override
    public GroupResponse update(String id, GroupRequest request) {
        if (groupRepository.existsByNameAndIdIsNot(request.getName(), id))
            throw new AlreadyExistException("Group already exists with name <%s>", request.getName());

        Group groupToUpdate = groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("group", id));
        groupToUpdate.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        groupToUpdate.setName(request.getName());
        groupToUpdate.setDescription(request.getDescription());

        Group updatedGroup = groupRepository.update(groupToUpdate);
        log.info("Updated group <{}> from request <{}>", updatedGroup, request);
        return GroupResponseMapper.of(
                updatedGroup,
                goodRepository.countProductsByGroupId(updatedGroup.getId()),
                goodRepository.sumProductsPriceByGroupId(updatedGroup.getId()));
    }

    @Override
    public void delete(String id) {
        if (!groupRepository.existsById(id))
            throw new NotFoundException("group", id);

        groupRepository.delete(id);
        log.info("Deleted group with id <{}>", id);
    }
}
