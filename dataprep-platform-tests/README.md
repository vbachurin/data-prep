# Talend Data Preparation - Platform tests
![alt text](http://www.talend.com/sites/all/themes/talend_responsive/images/logo.png "Talend")

This folder contains a Gatling project for backend service stress testing.

To test it out, you need to specify a scenario to run:
```
    $mvn -Pperformance-tests gatling:execute -Dgatling.simulationClass=dataprep.Scenario2
```
(look in classes available in src/test/scala/dataprep for valid scenario names).