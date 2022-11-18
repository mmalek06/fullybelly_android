package com.fullybelly.services.local.interfaces;

import com.fullybelly.models.api.BookedOrder;

public interface BookedOrderCacheService {

    void put(BookedOrder order);

    BookedOrder pop();

}
