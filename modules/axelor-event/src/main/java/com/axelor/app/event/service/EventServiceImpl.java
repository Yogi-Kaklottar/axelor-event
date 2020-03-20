package com.axelor.app.event.service;

import com.axelor.apps.message.db.EmailAccount;
import com.axelor.apps.message.db.EmailAddress;
import com.axelor.apps.message.db.Message;
import com.axelor.apps.message.db.repo.EmailAccountRepository;
import com.axelor.apps.message.db.repo.MessageRepository;
import com.axelor.apps.message.service.MessageService;
import com.axelor.event.db.Discount;
import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistaration;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventServiceImpl implements EventService {
  @Inject MessageService messageService;

  @Override
  public Discount calculateAmount(Event event, Discount discount) {
    try {
      discount.setDiscountAmount(
          (event
              .getEventFees()
              .multiply(discount.getDiscountPercentage())
              .divide(new BigDecimal(100))));
    } catch (Exception e) {
    }
    return discount;
  }

  @Override
  public Event sendEmail(Event event, String model) {
    try {
      int eventId = event.getId().intValue();
      List<EmailAddress> emailAddressList = new ArrayList<EmailAddress>();
      List<EventRegistaration> eventRegList = new ArrayList<EventRegistaration>();
      for (EventRegistaration eventRegistaration : event.getEventRegistrationList()) {
        if (eventRegistaration.getEmail() != null) {
          EmailAddress emailAddress = new EmailAddress(eventRegistaration.getEmail());
          if (eventRegistaration.getIsEmailSend() != true) {
            emailAddressList.add(emailAddress);
            eventRegistaration.setIsEmailSend(true);
          }
        }
        eventRegList.add(eventRegistaration);
      }
      event.setEventRegistrationList(eventRegList);
      EmailAccount emailAccount =
          Beans.get(EmailAccountRepository.class).all().filter("self.isDefault=true").fetchOne();
      if (emailAccount != null) {

        Message message =
            messageService.createMessage(
                model,
                eventId,
                "Event Registration",
                "EventRegistration Sucessfully",
                null,
                null,
                emailAddressList,
                null,
                null,
                null,
                null,
                MessageRepository.MEDIA_TYPE_EMAIL,
                emailAccount);
        messageService.sendByEmail(message);
      }
    } catch (Exception e) {

    }
    return event;
  }

  public boolean cheackValidRegistrationDate(Event event) {
    try {
      if (event.getEventRegistrationList().size() > 0) {
        for (EventRegistaration eventRegistarationObject : event.getEventRegistrationList()) {
          LocalDate registrationDate = eventRegistarationObject.getRegistrationDate().toLocalDate();
          if (!(registrationDate.isBefore(event.getRegistrationOpen()))
              && !(registrationDate.isAfter(event.getRegistrationClose()))) {
            return true;
          }
        }
      }
    } catch (Exception e) {
    }
    return false;
  }
}
