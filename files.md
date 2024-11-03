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

## Document Requirements:
All documents *must* begin with a title, in the "String" standard method format. <br>
This is used to get the titles for lists of files.

---

## Text Documents:

File Extension: .rtd (Revision Text Document)

- ### Title:
  - String
- ### Content:
  - String

---

#### Standard Methods:

Strings:

- Strings are represented as:
  - long length
    - This is the length of the string
  - char[] characters
    - These are the characters in the string. There are {length} characters