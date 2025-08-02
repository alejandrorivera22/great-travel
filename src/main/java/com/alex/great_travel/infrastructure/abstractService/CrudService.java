package com.alex.great_travel.infrastructure.abstractService;

public interface CrudService<RQ, RS, ID> {

    RS create(RQ rq);
    RS read(ID id);
    RS update(RQ rq, ID id);
    void delete(ID id);


}
