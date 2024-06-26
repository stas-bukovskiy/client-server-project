package edu.clientserver.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class Good {
    private Long id;
    private String name;
    private Integer quantity;
    private BigDecimal price;
    private Group group;
}
