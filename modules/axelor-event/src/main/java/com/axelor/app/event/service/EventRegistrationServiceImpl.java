package com.axelor.app.event.service;

import com.axelor.event.db.Discount;
import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistaration;
import java.math.BigDecimal;
import java.time.Period;

public class EventRegistrationServiceImpl implements EventRegistrationService {

  public EventRegistaration calculateEventRegistrationAmount(
      EventRegistaration eventRegistaration, Event event) {
    Period period =
        Period.between(
            event.getRegistrationClose(), eventRegistaration.getRegistrationDate().toLocalDate());
    int n = Math.abs(period.getDays());
    eventRegistaration.setAmount(BigDecimal.ZERO);
    for (Discount discount : event.getDiscountList()) {
      if (discount.getBeforeDays().intValue() >= n) {
        eventRegistaration.setAmount(event.getEventFees().subtract(discount.getDiscountAmount()));
      }
    }
    if (eventRegistaration.getAmount() == BigDecimal.ZERO) {
      eventRegistaration.setAmount(event.getEventFees());
    }
    return eventRegistaration;
  }

  @Override
  public Event setNetAmountAndTotal(Event event) {
    // Event event = eventRegistaration.getEvent();
    try {

      BigDecimal netAmount = BigDecimal.ZERO;
      BigDecimal netDiscount = BigDecimal.ZERO;
      BigDecimal netTotalAmount = BigDecimal.ZERO;
      for (EventRegistaration eventRegistrationData : event.getEventRegistrationList()) {
        netAmount = netAmount.add(eventRegistrationData.getAmount());
        netTotalAmount = netTotalAmount.add(event.getEventFees());
      }
      netDiscount = netTotalAmount.subtract(netAmount);
      int entry = event.getEventRegistrationList().size();
      event.setTotalEntry(entry);
      event.setAmountCollect(netAmount);
      event.setTotalDiscount(netDiscount);

    } catch (Exception e) {
      // System.out.println(e);
    }
    return event;
  }
}
