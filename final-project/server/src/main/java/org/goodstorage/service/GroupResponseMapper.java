package org.goodstorage.service;

import org.goodstorage.domain.Group;

import java.math.BigDecimal;

public final class GroupResponseMapper {
    private GroupResponseMapper() {
    }


    public static GroupService.GroupResponse of(Group group, int productsCounts, BigDecimal productsPrice) {
        return GroupService.GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .productsCounts(productsCounts)
                .productsPrice(productsPrice)
                .build();
    }

    public static Group to(GroupService.GroupResponse groupResponse) {
        return Group.builder()
                .id(groupResponse.getId())
                .name(groupResponse.getName())
                .description(groupResponse.getDescription())
                .createdAt(groupResponse.getCreatedAt())
                .updatedAt(groupResponse.getUpdatedAt())
                .build();
    }
}
