package org.goodstorage.service;

import lombok.Data;
import org.goodstorage.domain.Good;

import java.util.List;

public interface GoodService {
    List<Good> getGoods();

    List<Good> getGoodsByGroupId(String groupId);

    List<Good> searchGoods(String expression);

    Good getById(String id);

    Good create(GoodRequest request);

    Good update(String id, GoodRequest request);

    void delete(String id);

    Good addQuantity(String id, int dQuantity);

    Good writeOffQuantity(String id, int dQuantity);


    @Data
    class GoodRequest {
        public String description;
        private String name;
        private String producer;
        private Integer quantity;
        private Double price;
        private String groupId;
    }
}
