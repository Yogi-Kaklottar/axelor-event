package com.axelor.app.event.service;

import com.axelor.app.AppSettings;
import com.axelor.data.Importer;
import com.axelor.data.csv.CSVImporter;
import com.axelor.db.JpaSupport;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.commons.io.IOUtils;

public class ImportCsvServiceImpl extends JpaSupport implements ImportCsvService {
  public void setCsvImport(String fileName) {
    try {
      File configdata = getfileconfig(fileName);
      Importer importer = new CSVImporter(configdata.getAbsolutePath(), getDataCsvFile());
      importer.run();
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public File getfileconfig(String fileName) throws IOException {
    File config = File.createTempFile(fileName, "xml");
    InputStream inputStream = this.getClass().getResourceAsStream("/data-demo/input-config1.xml");
    FileOutputStream outputStream = new FileOutputStream(config);
    IOUtils.copy(inputStream, outputStream);

    return config;
  }

  public String getDataCsvFile() throws IOException {
    URL url = this.getClass().getResource(AppSettings.get().get("file.upload.dir").toString());

    return url.getPath();
  }
}
