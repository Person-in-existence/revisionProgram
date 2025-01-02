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

## Timetable:

The timetable is stored in a file named "timetable". It is stored in the following way:

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
All documents *must* begin with a title, in the "String" standard method format. <br>
This is used to get the titles for lists of files. <br>
Documents must also have a date - the date they were last revised. This is stored in string format, as "yyyy-MM-dd" <br>
This allows for revision based on when topics were last revised

---

## Text Documents:

File Extension: .rtd (Revision Text Document)

- ### Header:
  - String title
  - String date (See document requirements)
- ### Content:
  - String

---

## Fact Documents

File Extension: .rfd (Revision Fact Document)

- ### Header:
  -   String title
  -   String date (See Document Requirements)
- ### Content:
  - int num_facts
    - The number of facts in the document
  - num_facts times:
    - String question
      - The question of the fact. Stored in the standard String format
    - String answer
      - The answer to the fact. Stored in the standard String format

## Standard Methods:

Strings:

- Strings are represented as:
  - long length
    - This is the length of the string
  - char[] characters
    - These are the characters in the string. There are {length} characters