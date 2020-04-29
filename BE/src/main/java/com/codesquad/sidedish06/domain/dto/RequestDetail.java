package com.codesquad.sidedish06.domain.dto;

import com.codesquad.sidedish06.domain.entity.DetailSection;
import com.codesquad.sidedish06.domain.entity.Price;
import com.codesquad.sidedish06.domain.entity.ThumbImage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestDetail {

    private String hash;

    private String top_image;

    private List<ThumbImage> thumb_images;

    private String product_description;

    private String point;

    private String delivery_info;

    private String delivery_fee;

    private List<Price> prices;

    private List<DetailSection> detail_section;
}