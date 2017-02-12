# nurseHelper
Android application for nurses in long-term care - for reminders and charting

## Purpose
To maintain data related to patient care: medications administered/refused, and 
assessment information.  This application acts as both a medication-reminder 
for the nurse, and is also used for electronic charting.

NurseHelper is one component of a complete and functional electronic long-term 
care facility electronic patient health data and charting package.

## Installation
Import this repository to AndroidStudio; you can then upload a debug version
 of this app to an Android device.  Follow Android directions for creating a 
release-version of this app.

## Instructions
The code is written in java and xml.
See [how to use Android Studio.](http://www.instructables.com/id/How-To-Create-An-Android-App-With-Android-Studio/)

## License
See LICENSE.md

## Some Remaining Tasks
Change nurseHelper to alert nurses to lists of patients/medications that are due 
within some configurable time-period.  Add functionality related to care-plans 
and care "chores", and an alert system similar to what is done for medications.

## Future Plans
Two additional components are needed to create a functional package:

1. Private server with database and cloud functionality - to download changes in
 patient data to individual care-givers android devices (for example, medications).
  Also a package to maintain the server database.

2. XML Schema - generated code, to allow this entire package to be more easily 
customized for individual care facitilities.
