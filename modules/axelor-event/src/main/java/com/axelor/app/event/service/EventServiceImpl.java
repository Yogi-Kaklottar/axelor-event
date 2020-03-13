package com.axelor.app.event.service;

import com.axelor.event.db.Discount;
import com.axelor.event.db.Event;
import java.math.BigDecimal;

public class EventServiceImpl implements EventService {

  @Override
  public Discount calculateAmount(Event event, Discount discount) {
    discount.setDiscountAmount(
        (event
            .getEventFees()
            .multiply(discount.getDiscountPercentage())
            .divide(new BigDecimal(100))));
    System.err.println(discount);
    return discount;
  }
}
