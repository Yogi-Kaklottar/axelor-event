package com.axelor.app.event.service;

import com.axelor.data.ImportTask;
import com.axelor.data.csv.CSVImporter;
import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistaration;
import com.axelor.event.db.repo.EventRepository;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.google.common.io.Files;
import com.google.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;

public class ImportCsvServiceImpl implements ImportCsvService {
  @Inject EventRegistrationService eventRegistrationService;

  public void setCsvImport(MetaFile metaFile, Integer eventId) {
    try {
      File configdata = getConfigFile();
      CSVImporter csvImporter = new CSVImporter(configdata.getAbsolutePath());
      Map<String, Object> mapData = new HashMap<>();
      Long e_id = eventId.longValue();
      mapData.put("event", e_id);
      csvImporter.setContext(mapData);
      csvImporter.run(
          new ImportTask() {
            @Override
            public void configure() throws IOException {
              input("eventRegistration", getCsvData(metaFile));
            }
          });
    } catch (Exception e) {

    }
  }

  public File getCsvData(MetaFile dataFile) {
    File csvFile = null;
    try {
      File tempDir = Files.createTempDir();
      csvFile = new File(tempDir, "eventRegistration.csv");
      Files.copy(MetaFiles.getPath(dataFile).toFile(), csvFile);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return csvFile;
  }

  public File getConfigFile() {
    File configFile = null;
    try {
      configFile = File.createTempFile("input-config1", ".xml");
      InputStream inputStream =
          this.getClass().getResourceAsStream("/data-import/input-config1.xml");
      FileOutputStream fileOutPutStream = new FileOutputStream(configFile);
      IOUtils.copy(inputStream, fileOutPutStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return configFile;
  }

  public Object csvImportValidation(Object bean, Map<String, Object> values) {
    assert bean instanceof EventRegistaration;
    EventRegistaration eventRegistration = (EventRegistaration) bean;
    Event event =
        Beans.get(EventRepository.class).find(Long.parseLong(values.get("event").toString()));
    try {
      if (event != null) {
        LocalDate registrationDate = eventRegistration.getRegistrationDate().toLocalDate();
        if (!(registrationDate.isBefore(event.getRegistrationOpen()))
            && !(registrationDate.isAfter(event.getRegistrationClose()))) {
          eventRegistration =
              eventRegistrationService.calculateEventRegistrationAmount(eventRegistration, event);
          if (event.getCapacity() < event.getTotalEntry() + 1) {
            System.err.println(
                "Event Capacity Not Available. so not save:" + eventRegistration.getName());
            return null;
          }
          event.setTotalEntry(event.getTotalEntry() + 1);
          eventRegistration.setEvent(event);

        } else {
          System.err.println(
              "Registration Date are Not valid. so not save:" + eventRegistration.getName());
          return null;
        }
      }
    } catch (Exception e) {

    }

    return eventRegistration;
  }
}
