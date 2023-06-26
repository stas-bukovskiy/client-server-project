package org.goodstorage.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
public class Good {
    private String id;
    private String name;
    private String description;
    private String producer;
    private int quantity;
    private BigDecimal price;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Group group;
}
