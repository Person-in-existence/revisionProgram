File storage protocols:
=======================

---

This document describes the file storage systems used by this program.

---

## Settings:

Settings are stored in key-value pairs. The key and value are separated by an "=". Example:

> dark_mode=true

The key must be in all lowercase, with no spaces (underscores only).

Settings have no file extension, but are stored in the file "settings"

---

## Document creation suggestions:

Only today and yesterday are used in creation suggestions - more than that is unhelpful.
The created/ignored documents need to be remembered, so that we dont prompt the user to create a document twice.

This is stored in a file called "documentprompts", with no file extension

- String date
  - This is the date of the first day, when the document was created. This means that we can see if the document is outdated
- int numActivities
  - The number of activities which have **not** been done on this day
  - numActivities times:
    - String Activity name
      - The name of the activity
    - int subjectIndex
      - The index of the subject this belongs to.
- int numActivities2
  - The number of activities on the previous day which have not been done (note that there is no date before this one)
  - numActivities2 times:
    - String activity name
      - The name of the activity
    - int subjectIndex
      - The index of the subject it belongs to

---

## Timetable:

The timetable is stored in a file named "timetable". It is stored in the following way:

- ### Day data
  - String start_date
    - The date the timetable starts on, in a "yyyy-MM-dd"
- ### Activity Types
  - int numActivities
    - This is the number of activities in the list
  - activities
    - This is a series of strings. There are numActivities strings. Strings are stored as defined below.

- ### Timetable
  - int numDays
    - The number of days in the timetable
  - numDays times:
    - String dayName
      - The name of the day
    - int numActivities
      - The number of activities in this day
    - numActivities times:
      - String activity name
        - The name of the activity
      - int activityIndex
        - This is the index in activities that the activity type is. 
Note that this can be equal to the length of activities - in this case, it should be represented as "none"


---

## Document Requirements:
All documents must begin with the subject, in the string format. If they have no subject, Main.strings.getString("timetableNoActivitySelected") should be used. <br>
All documents must then have the title, in the "String" standard method format. <br>
This is used to get the titles for lists of files. <br>
Documents must also have a date - the date they were last revised. This is stored in string format, as "yyyy-MM-dd" <br>
This allows for revision based on when topics were last revised.<br>
They then have another date in the same format, which is scheduled for when revision next needs to happen.

---

## Text Documents:

File Extension: .rtd (Revision Text Document)

- ### Header:
  -String subject 
  - String title
    - String date (See document requirements)
    - String nextDate
      - The date the document should next be revised on
- ### Content:
  - String

---

## Fact Documents

File Extension: .rfd (Revision Fact Document)

- ### Header:
  - String subject 
  -   String title
  -   String date (See Document Requirements)
  - String nextDate
    - The date the document should next be revised on
- ### Content:
  - int num_facts
    - The number of facts in the document
  - num_facts times:
    - String question
      - The question of the fact. Stored in the standard String format
    - String answer
      - The answer to the fact. Stored in the standard String format

## Blank Documents

File Extension: .rbd  (Revision Blank Document)

- ### Header:
  - String subject
  - String title
  - String date
  - String nextDate
- ### Content:
  - int numBlankCards
    - The number of blank cards in the document
  - numBlankCards times:
    - String blankString
      - The text in the blank
    - int numBlanks
      - The number of blanked sections in the card
    - numBlanks times:
      - int startIndex
        - The start index of the blank
      - int endIndex
        - The end index of the blank

## Standard Methods:

Strings:

- Strings are represented as:
  - long length
    - This is the length of the string
  - char[] characters
    - These are the characters in the string. There are {length} characters