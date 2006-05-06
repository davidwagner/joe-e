#!/bin/sh

makeClass () {
  echo Making ${3}Array.java
  sed -e s/char/$1/g -e s/Character/$2/g -e s/Char/$3/g CharArray.java > ${3}Array.java
}

# primitive type, boxed type, capitalized primitive type
makeClass boolean Boolean Boolean
makeClass byte    Byte    Byte
makeClass short   Short   Short
makeClass int     Integer Int
makeClass long    Long    Long
makeClass float   Float   Float
makeClass double  Double  Double

