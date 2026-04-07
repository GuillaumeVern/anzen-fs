package com.losvernos.anzenfs;

import java.util.List;
import java.lang.String;

public interface DAO<T> {

  public List<T> getAll();

  public void save(T elementToSave);

  public void update(T elementToUpdate, String[] params);

  public void delete(T elementToDelete);
}
