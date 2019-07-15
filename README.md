# OpenMRS Database Migrator Tool

[![Build Status](https://travis-ci.com/esaude/openmrs-database-migrator.svg?branch=master)](https://travis-ci.com/esaude/openmrs-database-migrator) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/4b76ce9cc36f41abaa078b07182f0a24)](https://app.codacy.com/app/esaude-epts/openmrs-database-migrator?utm_source=github.com&utm_medium=referral&utm_content=esaude/openmrs-database-migrator&utm_campaign=Badge_Grade_Dashboard) [![codecov](https://codecov.io/gh/esaude/openmrs-database-migrator/branch/master/graph/badge.svg)](https://codecov.io/gh/esaude/openmrs-database-migrator)



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

First create the merge database:

```
CREATE DATABASE merge_db CHAR SET uf8;
```

Then load one of the dumps into `merge_db`. After that run the following script:
```
use merge_db;

insert into person_attribute_type
    (name, description, searchable, creator, date_created, retired, uuid) values
    ('Source DB ID', 'ID from source database', false, 1, curdate(), false, '8d793bee-c2cc-11de-8d13-0010c6dffd23');
```

To merge OpenMRS databases run:

```
java -jar migrator.jar run
```

This will run PDI transformations using `ETL_SOURCE_DATABASE` as the input database. The output will be saved in a database called `merge_db`.

### Configuration

The tool uses the following variables that can be configured either through system properties or the PDI `kettle.properties` file:

```
ETL_SOURCE_DATABASE=egpaf
ETL_DATABASE_HOST=127.0.0.1
ETL_DATABASE_PORT=3306
ETL_DATABASE_USER=migrator
ETL_DATABASE_PASSWORD=
```

To specify one of the variables as a system property use the following syntax:

```
java -DETL_DATABASE_USER=otheruser -jar migrator.jar run
```

Note that system properties take precedence over `kettle.properties`.

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