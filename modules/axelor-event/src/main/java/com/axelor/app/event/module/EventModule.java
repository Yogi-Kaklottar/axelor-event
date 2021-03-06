package com.axelor.app.event.module;

import com.axelor.app.AxelorModule;
import com.axelor.app.event.db.repo.EventRegistrationEventRepository;
import com.axelor.app.event.service.EventRegistrationService;
import com.axelor.app.event.service.EventRegistrationServiceImpl;
import com.axelor.app.event.service.EventService;
import com.axelor.app.event.service.EventServiceImpl;
import com.axelor.app.event.service.ImportCsvService;
import com.axelor.app.event.service.ImportCsvServiceImpl;
import com.axelor.event.db.repo.EventRegistarationRepository;

public class EventModule extends AxelorModule {
  @Override
  protected void configure() {
    bind(EventService.class).to(EventServiceImpl.class);
    bind(EventRegistrationService.class).to(EventRegistrationServiceImpl.class);
    bind(ImportCsvService.class).to(ImportCsvServiceImpl.class);
    bind(EventRegistarationRepository.class).to(EventRegistrationEventRepository.class);
    super.configure();
  }
}
