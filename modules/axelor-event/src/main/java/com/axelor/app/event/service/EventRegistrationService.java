package com.axelor.app.event.service;

import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistaration;

public interface EventRegistrationService {
  public EventRegistaration calculateEventRegistrationAmount(
      EventRegistaration eventRegistaration, Event event);

  public Event setNetAmountAndTotal(Event event);
}
