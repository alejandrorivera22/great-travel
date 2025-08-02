package com.alex.great_travel.infrastructure.abstractService;

import com.alex.great_travel.api.models.response.HotelResponse;

import java.util.Set;

public interface HotelService extends CatalogService<HotelResponse> {
    Set<HotelResponse> readByRatingGreaterThan(Integer rating);
}
