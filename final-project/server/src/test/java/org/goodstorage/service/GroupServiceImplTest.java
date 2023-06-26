package org.goodstorage.service;

import org.goodstorage.domain.Group;
import org.goodstorage.exceptions.AlreadyExistException;
import org.goodstorage.exceptions.NotFoundException;
import org.goodstorage.repository.GroupRepository;
import org.goodstorage.util.RandomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
    private GroupRepository groupRepository;
    private GroupServiceImpl groupService;

    @BeforeEach
    void setUp() {
        groupService = new GroupServiceImpl(groupRepository);
    }

    @Test
    void getGroups_ShouldReturnAllGroups() {
        List<Group> expectedGroups = Arrays.asList(RandomUtil.radndomGroup(), RandomUtil.radndomGroup());
        when(groupRepository.findAll()).thenReturn(expectedGroups);

        List<Group> actualGroups = groupService.getGroups();

        assertEquals(expectedGroups, actualGroups);
        verify(groupRepository).findAll();
    }


    @Test
    void getById_ExistingId_ShouldReturnGroup() {
        Group expectedGroup = RandomUtil.radndomGroup();
        String groupId = expectedGroup.getId();
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(expectedGroup));


        Group actualGroup = groupService.getById(groupId);

        assertEquals(expectedGroup, actualGroup);
        verify(groupRepository).findById(groupId);
    }

    @Test
    void getById_NonExistingId_ShouldThrowNotFoundException() {
        String groupId = UUID.randomUUID().toString();
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> groupService.getById(groupId));
        verify(groupRepository).findById(groupId);
    }

    @Test
    void create_NonExistingGroupName_ShouldCreateGroup() {
        GroupService.GroupRequest request = new GroupService.GroupRequest();
        request.setName(RandomUtil.randomString(10));
        request.setDescription(RandomUtil.randomString(100));
        when(groupRepository.existsByName(request.getName())).thenReturn(false);

        Group savedGroup = RandomUtil.radndomGroup();
        savedGroup.setName(request.getName());
        savedGroup.setDescription(request.getDescription());
        when(groupRepository.save(any(Group.class))).thenReturn(savedGroup);

        Group createdGroup = groupService.create(request);

        assertNotNull(createdGroup);
        assertEquals(savedGroup, createdGroup);
        verify(groupRepository).existsByName(request.getName());
        verify(groupRepository).save(any(Group.class));
    }

    @Test
    void create_ExistingGroupName_ShouldThrowAlreadyExistException() {
        GroupService.GroupRequest request = new GroupService.GroupRequest();
        request.setName(RandomUtil.randomString(10));
        request.setDescription(RandomUtil.randomString(100));
        when(groupRepository.existsByName(request.getName())).thenReturn(true);

        assertThrows(AlreadyExistException.class, () -> groupService.create(request));
        verify(groupRepository).existsByName(request.getName());
        verify(groupRepository, never()).save(any(Group.class));
    }

    @Test
    void update_ExistingGroupIdAndExistingGroupName_ShouldThrowAlreadyExistException() {
        String groupId = UUID.randomUUID().toString();
        GroupService.GroupRequest request = new GroupService.GroupRequest();
        request.setName(RandomUtil.randomString(10));
        request.setDescription(RandomUtil.randomString(100));
        when(groupRepository.existsByNameAndIdIsNot(request.getName(), groupId)).thenReturn(true);

        assertThrows(AlreadyExistException.class, () -> groupService.update(groupId, request));
        verify(groupRepository).existsByNameAndIdIsNot(request.getName(), groupId);
        verify(groupRepository, never()).findById(groupId);
        verify(groupRepository, never()).update(any(Group.class));
    }

    @Test
    void delete_ExistingGroupId_ShouldDeleteGroup() {
        String groupId = UUID.randomUUID().toString();
        when(groupRepository.existsById(groupId)).thenReturn(true);

        groupService.delete(groupId);

        verify(groupRepository).existsById(groupId);
        verify(groupRepository).delete(groupId);
    }

    @Test
    void delete_NonExistingGroupId_ShouldThrowNotFoundException() {
        String groupId = UUID.randomUUID().toString();
        when(groupRepository.existsById(groupId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> groupService.delete(groupId));
        verify(groupRepository).existsById(groupId);
        verify(groupRepository, never()).delete(groupId);
    }
}
