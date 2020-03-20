package com.axelor.app.event.db.repo;

import com.axelor.app.event.service.EventRegistrationService;
import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistaration;
import com.axelor.event.db.repo.EventRegistarationRepository;
import com.axelor.event.db.repo.EventRepository;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.util.List;

public class EventRegistrationEventRepository extends EventRegistarationRepository {
  @Inject EventRegistrationService eventRegistrationService;

  @Transactional
  @Override
  public void remove(EventRegistaration entity) {
    super.remove(entity);
    Event event = entity.getEvent();
    List<EventRegistaration> eventRegistrationList = event.getEventRegistrationList();
    eventRegistrationList.remove(entity);
    event.setEventRegistrationList(eventRegistrationList);
    event = eventRegistrationService.setNetAmountAndTotal(event);
    Beans.get(EventRepository.class).save(event);
  }
}
