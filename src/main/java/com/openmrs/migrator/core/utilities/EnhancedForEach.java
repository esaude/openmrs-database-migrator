package com.openmrs.migrator.core.utilities;

@FunctionalInterface
public interface EnhancedForEach {

  <T> void forEach(T t) throws Exception;
}
