package com.axelor.app.event.web;

import com.axelor.app.event.service.EventRegistrationService;
import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistaration;
import com.axelor.event.db.repo.EventRepository;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;

public class EventRegistrationContraller {
  @Inject EventRegistrationService eventRegistrationService;

  public void calculateTotalAmount(ActionRequest request, ActionResponse response) {
    try {
      Event event = request.getContext().asType(Event.class);
      event = eventRegistrationService.setNetAmountAndTotal(event);
      response.setValue("amountCollect", event.getAmountCollect());
      response.setValue("totalEntry", event.getTotalEntry());
      response.setValue("totalDiscount", event.getTotalDiscount());
    } catch (Exception e) {

    }
  }

  public void validateRegisterDate(ActionRequest request, ActionResponse response) {
    try {
      EventRegistaration eventRegistaration = request.getContext().asType(EventRegistaration.class);
      Event event = eventRegistaration.getEvent();
      if (event == null) {
        event = request.getContext().getParent().asType(Event.class);
        if (event == null) {
          response.setError("please check Event");
        }
      }
      LocalDate registrationDate = eventRegistaration.getRegistrationDate().toLocalDate();
      if (!(registrationDate.isBefore(event.getRegistrationOpen()))
          && !(registrationDate.isAfter(event.getRegistrationClose()))) {
        eventRegistaration =
            eventRegistrationService.calculateEventRegistrationAmount(eventRegistaration, event);
        response.setValue("amount", eventRegistaration.getAmount());
      } else {
        response.setValue("registrationDate", null);
        response.setValue("amount", BigDecimal.ZERO);
        response.setNotify("registration date are not valid");
      }
    } catch (Exception e) {

    }
  }

  @Transactional
  public void netTotalSaveEventRegistration(ActionRequest request, ActionResponse response) {
    EventRegistaration eventRegistaration = request.getContext().asType(EventRegistaration.class);
    Event event = eventRegistaration.getEvent();
    event = eventRegistrationService.setNetAmountAndTotal(event);
    Beans.get(EventRepository.class).save(event);
  }

  public void checkCapacityOfEvent(ActionRequest request, ActionResponse response) {
    try {
      EventRegistaration eventRegistaration = request.getContext().asType(EventRegistaration.class);
      Event event = eventRegistaration.getEvent();
      Integer eventRegistrationCount = event.getEventRegistrationList().size();
      if (eventRegistaration.getId() == null) {
        if (event.getCapacity() <= eventRegistrationCount) {
          response.setError("Not More Than Capacity:" + event.getCapacity());
        }
      }
      if (eventRegistaration.getRegistrationDate() == null) {
        response.setError("Please fill up Registration date");
      }
    } catch (Exception e) {

    }
  }
}
