#!/bin/sh
# Copyright 2005-06 Regents of the University of California.  May be used 
# under the terms of the revised BSD license.  See LICENSING for details.
# Author: Adrian Mettler
#
# This is a script to automatically generate the other primitive-array backed
# Array types given an implementation of CharArray, since they are so similar.
# This prevents unwanted differences between the different classes.
# [Incidentally, char is used because it has distinct forms for its primitive
# type and boxed type, and only exists as a data member in its corresponding
# implementation (unlike int, which is also the type of lengths and indices).]

makeClass () {
  sed -e s/char/$1/g -e s/Character/$2/g -e s/Char/$3/g \
      CharArray.java > ${3}Array.java
  echo Wrote ${3}Array.java
}

# primitive type, boxed type, capitalized primitive type
makeClass boolean Boolean Boolean
makeClass byte    Byte    Byte
makeClass short   Short   Short
makeClass int     Integer Int
makeClass long    Long    Long
makeClass float   Float   Float
makeClass double  Double  Double

