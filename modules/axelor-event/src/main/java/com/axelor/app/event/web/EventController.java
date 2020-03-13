package com.axelor.app.event.web;

import com.axelor.app.AppSettings;
import com.axelor.app.event.service.EventService;
import com.axelor.app.event.service.ImportCsvService;
import com.axelor.event.db.Discount;
import com.axelor.event.db.Event;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import java.time.LocalDate;
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

  public void compareEventRegistrationDate(ActionRequest request, ActionResponse response) {
    try {
      Event event = request.getContext().asType(Event.class);
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
      String fileType = metaFileMap.get("fileType").toString();
      String uploadDir = AppSettings.get().get("file.upload.dir");
      String fileName = metaFileMap.get("filePath").toString();

      if (fileType.equals("text/csv")) {
        importCsvService.setCsvImport(fileName);
      } else {
        response.setFlash("your file ae not valid type");
      }
    } catch (Exception e) {
      System.err.println("exception");
    }
  }
}
