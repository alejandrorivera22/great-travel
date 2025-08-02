package com.alex.great_travel.infrastructure.abstractService;

import com.alex.great_travel.api.models.response.FlyResponse;

import java.util.Set;

public interface FlyService extends CatalogService<FlyResponse>{
    Set<FlyResponse> readByOriginDestiny(String origin, String destiny);
}
