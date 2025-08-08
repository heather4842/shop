package com.shop.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemDto {
    private String id;
    private String itemNm;
    private Integer Price;
    private String itemDetail;
    private String sellstatCd;

    private LocalDateTime regTime;
    private LocalDateTime updateTime;
}
