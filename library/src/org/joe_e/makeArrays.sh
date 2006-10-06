#!/bin/sh
# Copyright 2005-06 Regents of the University of California.  May be used 
# under the terms of the revised BSD license.  See LICENSING for details.
# Author: Adrian Mettler

makeClass () {
  sed -e s/char/$1/g -e s/Character/$2/g -e s/Char/$3/g \
      -e s/-4016604734433045551L/$4/ CharArray.java > ${3}Array.java
  echo Wrote ${3}Array.java
}

# primitive type, boxed type, capitalized primitive type
makeClass boolean Boolean Boolean -7541507816291995903L
makeClass byte    Byte    Byte    -2523058214080487043L
makeClass short   Short   Short    5184647170956085700L
makeClass int     Integer Int      3554362412868687210L
makeClass long    Long    Long     1915161630626766078L
makeClass float   Float   Float    5430882612581040334L
makeClass double  Double  Double  -9084610698309158874L

