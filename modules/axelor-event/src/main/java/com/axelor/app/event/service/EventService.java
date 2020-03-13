package com.axelor.app.event.service;

import com.axelor.event.db.Discount;
import com.axelor.event.db.Event;

public interface EventService {
  public Discount calculateAmount(Event event, Discount discount);
}
