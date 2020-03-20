package com.axelor.app.event.service;

import com.axelor.meta.db.MetaFile;

public interface ImportCsvService {
  public void setCsvImport(MetaFile metafile, Integer eventId);
}
