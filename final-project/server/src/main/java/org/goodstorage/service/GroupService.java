package org.goodstorage.service;

import lombok.Builder;
import lombok.Data;
import org.goodstorage.domain.Group;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface GroupService {
    List<GroupResponse> getGroupResponses();

    List<GroupResponse> searchGroupResponses(String expression);

    GroupResponse getGroupResponseById(String id);

    Group getGroupById(String id);

    GroupResponse create(GroupRequest request);

    GroupResponse update(String id, GroupRequest request);

    void delete(String id);


    @Data
    class GroupRequest {
        private String name;
        private String description;
    }

    @Data
    @Builder
    class GroupResponse {
        private String id;
        private String name;
        private String description;
        private int productsCounts;
        private BigDecimal productsPrice;
        private Timestamp createdAt;
        private Timestamp updatedAt;
    }
}
