# nurseHelper
Android application for nurses in long-term care - for reminders and charting

## Purpose
To maintain data related to patient care: medications administered/refused, and 
assessment information.  This application acts as both a medication-reminder 
for the nurse, and is also used for electronic charting.  Patient data and 
medication lists are maintained centrally on a Firebase database.

## Installation
Import this repository to AndroidStudio; you can then upload a debug version
 of this app to an Android device.  Follow Android directions for creating a 
release-version of this app.

## Instructions
The code is written in java and xml.
See [how to use Android Studio.](http://www.instructables.com/id/How-To-Create-An-Android-App-With-Android-Studio/)

## License
See UNLICENSE.md

## Production ready vs Demo App
This app is supposed to be “production-ready”.  However, b/c NurseHelper depends
on how the end-user (e.g. nursing-home or other long-term care facility) handles
its data, this app is just a “demo”.  If some nursing home believed that it could
help them, then they would have to work with a developer to come up with the final
data design.  Also, the customer could also have input into how the app should best
function for their needs.

## Data storage
As a server, this app uses Firebase database.  For now, the data is kept on both the
Firebase side and the device (NurseHelper's content provider).  There are three reasons for this: 
1. save on Firebase charges for downloading data, so that only changes to Firebase
   data (should) cause it to be downloaded to the nurse’s device.
2. In case the internet is down, nurses can still access patient data.
3. For data safety, in case of data corruption/loss; although Firebase data can be
   backed-up.

## Firebase issues
One issue with Firebase, is it telling the app (on start-up) of “changes”, when 
none had been made.  This resulted in a lot of unnecessary downloads, which increased
the usage download quantity, which will result in higher charges (right now it’s still
free, for testing).  Also, potential customers may need to be convinced that their
patient data will be private on Firebase, or may want to go with an already-established 
private server.

## Libraries
This app uses Picasso and PdfViewer.

## Google play services
This app uses identification and location.  Identification is used for logging in and
for retrieving the nurse's name.  The nurse’s name is necessary for all the resident
transactions.  Location is used to verify that this app is only executed at specified
allowed locations (the registered nursing homes/facilities).  For now, the app returns
‘true’ for all locations, for demo mode.

## Test data
For the “Careplan” activity, the app uses fake pfd files under assets/.  The PdfViewer 
library reads local pdfs, from the SD card.  Later, it may make more sense to just
have the app call Acroread with care plans which are somewhere on-line.  Or the
care plans could be downloaded to each nurse’s devices.  Also the facilities may not 
use pdfs, so this is just one thing to use as a demo.  The resident images are under
assets/ as well, for the demo.  In future versions, the actual images could be taken
from the nurse’s device, or somewhere else of course.

All data provided with this version of the app is "fake", used showing the app's operation
to potential clients (demo mode).

## Accessibility
This functionality is limited, because this app will be used by registered nurses, licensed
practical nurses, and possibly nursing aids.  These employees can’t be hearing or visually
impaired, because otherwise they wouldn’t be qualified to do their job.  For this reason 
the app has very limited content descriptions.  Every term used is obvious for nurses.

## SyncAdapters / Services
The NurseSyncAdapter is used to check daily for changes to the firebase database.  If there
are any, it will write the changes to the content provider, and notify the widget.  The app
also listens for changes to the Firebase database, and starts an AsyncThread to process add
these changes to this app's content provider.

The app also comes with a Service which repeats every 15, 30, or 45 min. (or not at all),
depending on user preference settings.  This service checks the next medication administration
time for each resident, which only involves looking at the local content provider.  If there
are any changes since last time, the widget is notified.  App notifications haven’t been 
implemented, because these would be too numerous/annoying for a nurse using this app 8-12 hrs 
per day, on a regular basis.  Both the widget and the app will display these times next to each 
resident room#, below “Care Plan”.

## Medication Data
The medication data will be familiar to all nurses.  Medications can be either scheduled, or "PRN"
("as needed").  Each medication has both a generic name and trade name.  Also, administration times
(for scheduled meds), can be a list of times, or also expressed as a frequency, or both.  For non-
nurses, accepted frequencies used are:
QD - every day, or once a day
BID - twice a day
TID - three times a day
QID - four times a day
Q 3HR - every 3 hours ... etc.
A few of the medications in the demo are 'fake' (notably for room 208), but this shouldn't be a 
problem, since all the current demo data is fake!

Each time a medication is "given" - as charted by clicking on 'give' for that medication, the 
next-time-to-administer this med is calculated by NurseHelper.  Medications can also be refused
by the resident, in which case the medication is charted as 'refused'.

The nurse can undo a 'give' or 'refuse' check, by un-checking it.  The app will bring up an alert
dialog, and verify that they mean to undo the 'give' or 'refuse'; if they proceed with the undo 
operation, the recently written database changes related to that give or refuse, will be undone.

## Medication Alerts
Medication alerts are shown as a date/time field to the right of the patient picture, and under
"Care Plan", on the app.  On the widget, they're shown to the right of the patient room#.  The
medication alert is the date/time of the medication which is due in the least amount of time, for 
each resident of the facility.  If the medication due time is in the future, the text appears white,
with a blue background; if the med is past-due, the text appears red with a gray background.  As
a design decision, Notifications are not implemented.

## Assessment Data
The Assessment screen will also be familiar to all nurses.


## Possible Future Plans
1. XML Schema - generated code, to allow this entire package to be more easily 
customized for individual care facitilities.  Possibly also some code-generation 
programs to create the Android ContentProvider code, based on the input XML schema
files.

2. Design and implement a voice-to-text version of NurseHelper
