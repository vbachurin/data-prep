![alt text](http://www.talend.com/sites/all/themes/talend_responsive/images/logo.png "Talend") 
#Data Preparation Release Notes 

*Version 1.0 M6*

# Feature highlight

* Undo / Redo for preparation steps (also supports CTRL+Z, CTRL+Y).
* Cache to speeds up new preparation creation, undo redo.
* Lots of bug fixes in date pattern detection.
* UI improvements.

To consult the full list of issues fixed in this version, go to https://jira.talendforge.org/issues/?jql=project%20%3D%20TDP%20AND%20fixVersion%20%3D%20%221.0.m6%22%20ORDER%20BY%20issuetype%20DESC%2C%20priority%20DESC.

# New Features

## UI

* Undo / redo for preparations: you can now undo / redo steps in preparations (https://jira.talendforge.org/browse/TDP-88).
* Data Set creation time was replaced by "created n seconds/hours/... ago" instead of a date time (https://jira.talendforge.org/browse/TDP-308).
* Favorite flag: you can add dataset(s) to favorites (https://jira.talendforge.org/browse/TDP-85).
* Web UI now asks for a preparation name when you close a new preparation (https://jira.talendforge.org/browse/TDP-148).

## New actions

* extract parts of a date (day / month / year). Available as "Extract Date tokens..." on a date column (https://jira.talendforge.org/browse/TDP-165).
* substring on a column's text (https://jira.talendforge.org/browse/TDP-160).
* renaming a column now propose the old column name as default value (https://jira.talendforge.org/browse/TDP-309).
* compute time since: adds a new column that counts time between now and the selected column (https://jira.talendforge.org/browse/TDP-168).
* length of values: adds a new column that holds the number of characters in selected column (https://jira.talendforge.org/browse/TDP-162).

## General

* Cache over HDFS speeds up operations on dataset (speed boosts mainly are on preparation opening and undo/redo operations) (https://jira.talendforge.org/browse/TDP-272).
* Docker image size was reduced (from nnnn to nnnn) (https://jira.talendforge.org/browse/TDP-287).

# Bug fix

## UI

* Progress bar when uploading now fixed to top (https://jira.talendforge.org/browse/TDP-266).
* Missing filter on valid records (https://jira.talendforge.org/browse/TDP-295).
* Bar chart now uses integer values when displaying occurrence count https://jira.talendforge.org/browse/TDP-353).

## General

* Missing date actions is case of unexpected type / semantic domain detections. (https://jira.talendforge.org/browse/TDP-296)
* Many fixes relative to date (incorrect date pattern detections).
* More robust behavior in case of column name clash (https://jira.talendforge.org/browse/TDP-305).
* Fix issue when a preparation including steps with many parameters could not be opened (https://jira.talendforge.org/browse/TDP-280).
* Support non-english characters in preparation names (https://jira.talendforge.org/browse/TDP-336).
  
# Developer notes (API changes)

* Transformation REST API: "transform/" and "export/{format}" operation were merged into one "transform/{format}" operation. (https://jira.talendforge.org/browse/TDP-151)

