![alt text](http://www.talend.com/sites/all/themes/talend_responsive/images/logo.png "Talend") 
#Data Preparation Release Notes 

*Version 1.0 M7*

# Feature highlight

* In cell edition
* Live preview _before_ a function is applied into the recipe
* Remote datasets (hdfs and http) supported
* Dataset sampling enabled to work on a smaller part of the dataset
* Improved data visualization on numbers

To consult the full list of issues fixed in this version, go to https://jira.talendforge.org/secure/ReleaseNote.jspa?projectId=11631&version=18505.

# New Features

## UI

* Improved data visualization on numbers (https://jira.talendforge.org/browse/TDP-278).
* Live preview _before_ a function is applied into the recipe (https://jira.talendforge.org/browse/TDP-382).
* Improved filters on number (https://jira.talendforge.org/browse/TDP-208).
* Datasets sort (https://jira.talendforge.org/browse/TDP-103)

## New actions

* Compute time since action (https://jira.talendforge.org/browse/TDP-168)
* Delete rows with invalid values action (https://jira.talendforge.org/browse/TDP-174)
* Cell edition (https://jira.talendforge.org/browse/TDP-84)
* Allow to adapt all dates to the selected pattern (https://jira.talendforge.org/browse/TDP-402)

## General

* Change column semantic type (https://jira.talendforge.org/browse/TDP-279)
* Remote dataset (HDFS & HTTP) support (https://jira.talendforge.org/browse/TDP-316)
* Dataset sampling (https://jira.talendforge.org/browse/TDP-318)

## Bugs fixed
* Dataset view - Type displayed on column header not correct (https://jira.talendforge.org/browse/TDP-273)
* Actions label should be displayed in alphabetical order (https://jira.talendforge.org/browse/TDP-307)
* [Guess Schema] Date type display string type after import xls from with guess schema (https://jira.talendforge.org/browse/TDP-332)
* Preparations menu - labels truncated but no tooltip (https://jira.talendforge.org/browse/TDP-344)
* Preview error with tdp id (https://jira.talendforge.org/browse/TDP-352)
* Items menu header not in the same line on Firefox and IE (https://jira.talendforge.org/browse/TDP-359)
* Date Actions for Text column (https://jira.talendforge.org/browse/TDP-368)
* Compute Length function creates a column but type is Date instead of Integer (https://jira.talendforge.org/browse/TDP-370)
* Stats Tabs are visible before selecting any column (https://jira.talendforge.org/browse/TDP-371)
* Error importing XLS file (https://jira.talendforge.org/browse/TDP-375)
* Import dataset from local machine does not work (https://jira.talendforge.org/browse/TDP-376)
* Stats - Should not trim + truncated (https://jira.talendforge.org/browse/TDP-377)
* Bad Data displayed after action "Find and group similar text..." (https://jira.talendforge.org/browse/TDP-380)
* UI of the filter badge (https://jira.talendforge.org/browse/TDP-385)
* Increase width of cell edition box (https://jira.talendforge.org/browse/TDP-404)
* After update a dataset, it does not open (https://jira.talendforge.org/browse/TDP-405)
* Changing dataset sample size does not update statistics (DQ bar) (https://jira.talendforge.org/browse/TDP-413)
* Compute time since works only once (https://jira.talendforge.org/browse/TDP-416)
* missing invalid value in statistics when a new invalid value is manually added (https://jira.talendforge.org/browse/TDP-421)
* Import DataSet - dataset is not open anymore (https://jira.talendforge.org/browse/TDP-422)
* TDP-335 regression (https://jira.talendforge.org/browse/TDP-432)
* Preview on an action change some non related values in other columns (https://jira.talendforge.org/browse/TDP-439)
* No more Onboarding (https://jira.talendforge.org/browse/TDP-440)
* If clicking an action too fast, preview keep displayed in recipe steps (https://jira.talendforge.org/browse/TDP-441)
* Domain "unknown" on type selection (https://jira.talendforge.org/browse/TDP-454)
* Change column type - Action list and Quality bar are not refreshed (https://jira.talendforge.org/browse/TDP-458)
* Brush extent outside the rangeLimits (https://jira.talendforge.org/browse/TDP-460)
* Numeric Filter badge doesnt have background (https://jira.talendforge.org/browse/TDP-463)


