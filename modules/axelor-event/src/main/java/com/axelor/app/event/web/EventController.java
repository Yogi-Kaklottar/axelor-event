package com.axelor.app.event.web;

import com.axelor.app.event.service.EventService;
import com.axelor.app.event.service.ImportCsvService;
import com.axelor.event.db.Discount;
import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistaration;
import com.axelor.inject.Beans;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.db.repo.MetaFileRepository;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import java.time.LocalDate;
import java.time.Period;
import java.util.Map;

public class EventController {
  @Inject EventService eventService;
  @Inject ImportCsvService importCsvService;

  public void calculateEventDiscount(ActionRequest request, ActionResponse response) {
    try {
      Discount discount = request.getContext().asType(Discount.class);
      Event event = request.getContext().getParent().asType(Event.class);
      System.out.println(discount);
      discount = eventService.calculateAmount(event, discount);
      response.setValue("discountAmount", discount.getDiscountAmount());
    } catch (Exception e) {
      // TODO: handle exception
    }
  }

  public void calculateEventDiscountDays(ActionRequest request, ActionResponse response) {
    Discount discount = request.getContext().asType(Discount.class);
    Event event = request.getContext().getParent().asType(Event.class);
    Period period = Period.between(event.getRegistrationOpen(), event.getRegistrationClose());
    if (discount.getBeforeDays() > period.getDays()) {
      response.setValue("beforeDays", 0);
      response.setError(
          "Don’t allows to put days which exceed duration between open and	close registration dates");
    }
  }

  public void compareEventRegistrationDate(ActionRequest request, ActionResponse response) {
    try {
      Event event = request.getContext().asType(Event.class);
      if (event.getStartDate()==null) {    	  
          response.setError("Please Fill start Date Up Date");          
        }
      LocalDate startDate = event.getStartDate().toLocalDate();
      LocalDate endDate = event.getEndDate().toLocalDate();
      
      if (startDate.isAfter(endDate)) {
        response.setNotify("starting end ending date invalide");
        response.setValue("endDate", null);
      }
      if (event.getRegistrationOpen().isAfter(event.getRegistrationClose())) {
        response.setNotify("regisstration date are invalide");
        response.setValue("registrationClose", null);
      }
    } catch (Exception e) {

    }
  }

  public void csvFileUpload(ActionRequest request, ActionResponse response) {

    try {
      Map<String, Object> metaFileMap = (Map<String, Object>) request.getContext().get("metaFile");
      Integer eventId = (Integer) request.getContext().get("_id");
      String fileType = metaFileMap.get("fileType").toString();
      if (fileType.equals("text/csv")) {
        MetaFile metaFile =
            Beans.get(MetaFileRepository.class)
                .find(Long.parseLong(metaFileMap.get("id").toString()));
        Beans.get(ImportCsvService.class).setCsvImport(metaFile, eventId);

      } else {
        response.setFlash("your file are not valid type");
      }
    } catch (Exception e) {
      System.err.println("exception");
    }
  }

  public void compareEventRegistrationDateAndStartDate(
      ActionRequest request, ActionResponse response) {
    try {
      Event event = request.getContext().asType(Event.class);
      LocalDate startDate = event.getStartDate().toLocalDate();
      LocalDate registrationCloseDate = event.getRegistrationClose();
      if (startDate.isBefore(registrationCloseDate)) {
        response.setNotify("event starting and registration closing date invalid");
        response.setValue("registrationClose", null);
      }

    } catch (Exception e) {

    }
  }

  public void sendEmail(ActionRequest request, ActionResponse response) {
    try {
      Event event = request.getContext().asType(Event.class);
      String model = request.getModel();
      event = eventService.sendEmail(event, model);
      response.setValue("eventRegistrationList", event.getEventRegistrationList());
      response.setFlash("email send");
    } catch (Exception e) {

    }
  }

  public void checkAllRegistrationDateValidOrNot(ActionRequest request, ActionResponse response) {
    try {
      Event event = request.getContext().asType(Event.class);
      for (EventRegistaration eventRegistaration : event.getEventRegistrationList()) {
        if (eventRegistaration.getRegistrationDate() == null) {
          response.setError("please fill up registration date");
        }
      }
      if (event.getEventRegistrationList().size() > 0) {
        if (!eventService.cheackValidRegistrationDate(event)) {
          response.setError("please Remove InValid Registration Date Remove");
        }
      }
      Integer registarationCounter = event.getEventRegistrationList().size();
      if (event.getCapacity() < registarationCounter) {
        response.setError("Event capacity not more thane:" + event.getCapacity());
      }

    } catch (Exception e) {

    }
  }
}
