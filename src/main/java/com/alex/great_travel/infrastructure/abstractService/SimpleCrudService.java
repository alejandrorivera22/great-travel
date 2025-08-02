package com.alex.great_travel.infrastructure.abstractService;

public interface SimpleCrudService<RQ, RS, ID> {
    RS create(RQ request);

    RS read(ID id);

    void delete(ID id);
}
