package com.axelor.app.event.web;

import com.axelor.app.event.service.EventRegistrationService;
import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistaration;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
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
      // System.out.println("hello problem:" + e.fillInStackTrace());
    }
  }

  public void validateRegisterDate(ActionRequest request, ActionResponse response) {
    try {
      EventRegistaration eventRegistaration = request.getContext().asType(EventRegistaration.class);
      Event event = eventRegistaration.getEvent();
      Integer registore = event.getEventRegistrationList().size();
      if (event.getCapacity() <= registore) {
        response.setError("Event capacity not more thane:" + event.getCapacity());
      }
      LocalDate registrationDate = eventRegistaration.getRegistrationDate().toLocalDate();
      if (!(registrationDate.isBefore(event.getRegistrationOpen()))
          && !(registrationDate.isAfter(event.getRegistrationClose()))) {
        eventRegistaration =
            eventRegistrationService.calculateEventRegistrationAmount(eventRegistaration, event);
        response.setValue("amount", eventRegistaration.getAmount());
      } else {
        response.setNotify("registration date not approved");
        response.setValue("registrationDate", null);
      }
    } catch (Exception e) {

    }
  }

  public void checkCapacityOfEvent(ActionRequest request, ActionResponse response) {
    EventRegistaration eventRegistaration = request.getContext().asType(EventRegistaration.class);
    Event event = eventRegistaration.getEvent();
    Integer registore = event.getEventRegistrationList().size();
    if (event.getCapacity() <= registore) {
      response.setNotify("Event capacity not more thane:" + event.getCapacity());
      response.setValue("event", null);
    }
  }
}
