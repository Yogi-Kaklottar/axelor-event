package com.axelor.app.event.service;

import com.axelor.event.db.Discount;
import com.axelor.event.db.Event;

public interface EventService {
  public Discount calculateAmount(Event event, Discount discount);

  public boolean cheackValidRegistrationDate(Event event);

  public Event sendEmail(Event event, String model);
}
