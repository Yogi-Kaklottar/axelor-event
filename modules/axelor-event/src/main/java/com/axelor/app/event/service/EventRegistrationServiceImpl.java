package com.axelor.app.event.service;

import com.axelor.event.db.Discount;
import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistaration;
import java.math.BigDecimal;
import java.time.Period;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EventRegistrationServiceImpl implements EventRegistrationService {

  public EventRegistaration calculateEventRegistrationAmount(
      EventRegistaration eventRegistaration, Event event) {
    try {
      Period period =
          Period.between(
              event.getRegistrationClose(), eventRegistaration.getRegistrationDate().toLocalDate());
      int n = Math.abs(period.getDays());
      eventRegistaration.setAmount(BigDecimal.ZERO);
      List<Discount> discountList = this.sortDiscountList(event.getDiscountList());
      for (Discount discount : discountList) {
        if (discount.getBeforeDays().intValue() <= n) {
          eventRegistaration.setAmount(event.getEventFees().subtract(discount.getDiscountAmount()));
        }
      }
      if (eventRegistaration.getAmount() == BigDecimal.ZERO) {
        eventRegistaration.setAmount(event.getEventFees());
      }

    } catch (Exception e) {

    }
    return eventRegistaration;
  }

  @Override
  public Event setNetAmountAndTotal(Event event) {
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

    }
    return event;
  }

  public List<Discount> sortDiscountList(List<Discount> discountList) {
    Collections.sort(
        discountList,
        new Comparator<Discount>() {
          @Override
          public int compare(Discount o1, Discount o2) {
            if (o1.getBeforeDays() < o2.getBeforeDays()) {
              return -1;
            } else {
              return 1;
            }
          }
        });
    return discountList;
  }
}
