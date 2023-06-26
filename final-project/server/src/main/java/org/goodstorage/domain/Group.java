package org.goodstorage.domain;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Group {
    private String id;
    private String name;
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
