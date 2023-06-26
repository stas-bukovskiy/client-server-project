package org.goodstorage.service;

import lombok.Data;
import org.goodstorage.domain.Group;

import java.util.List;

public interface GroupService {
    List<Group> getGroups();

    List<Group> searchGroups(String expression);

    Group getById(String id);

    Group create(GroupRequest request);

    Group update(String id, GroupRequest request);

    void delete(String id);


    @Data
    class GroupRequest {
        private String name;
        private String description;
    }
}
