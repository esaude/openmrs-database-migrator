# OpenMRS Database Migrator Tool

[![Build Status](https://travis-ci.com/esaude/openmrs-database-migrator.svg?branch=master)](https://travis-ci.com/esaude/openmrs-database-migrator) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/4b76ce9cc36f41abaa078b07182f0a24)](https://app.codacy.com/app/esaude-epts/openmrs-database-migrator?utm_source=github.com&utm_medium=referral&utm_content=esaude/openmrs-database-migrator&utm_campaign=Badge_Grade_Dashboard)

A growing requirement in countries across the world that implement OpenMRS is to 

## Overview

## Dependencies

Java 8 needs to be installed for this application to run.

## Usage

To build an executable JAR file run: 

```
./gradlew build
```

To run the SpringBoot application run: 

```
./gradlew bootrun
```

This app uses the [Spotless](https://github.com/diffplug/spotless/tree/master/plugin-gradle) code formatter to apply the [Google Java Format rules](https://github.com/google/google-java-format) to the code base.

If you find yourself with linting exceptions, to automatically resolve all issues try running 

```
./gradlew spotlessApply
```

### How to run the tool

### Configuration

### Tips to keep in mind

## References

### Technologies used

* Java 8
* Gradle (build tool)
* SpringBoot
* SpringMVC Web technologies
* Thymeleaf Page Layouts
* Pentaho Data Integration (PDI)

### Folder Structure


## Architecture

{Provide diagram here}