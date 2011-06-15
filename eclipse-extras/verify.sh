#!/bin/sh

# You will need to fill out these variables with: 
# *  The full path to your eclipse plugins directory (the location of the file
#      org.eclipse.equinox.launcher.<someversion>.jar)
# *  The full path to your taming database
# *  The full path to your Joe-E library (in the format of a classpath entry,
#      e.g. a JAR file)
# before you can run the command-line verifier.
# Escape any spaces with "\ " instead of using quotes.

ECLIPSE_PLUGINS_DIR=~/proj/eclipse/plugins
TAMING_DIR=~/proj/joe-e/commandline-2.2.3/taming-20100421
LIBRARY_CLASSPATH=~/proj/joe-e/commandline-2.2.3/library-2.2.3.jar

# The remaining lines should be OK as is.  Note that the CLASSPATH
# environment variable is used to specify the location of additional
# libraries needed to compile the source code being verified.
CLASSPATH=$LIBRARY_CLASSPATH:$CLASSPATH
LAUNCHER_PATH=`echo "$ECLIPSE_PLUGINS_DIR"/org.eclipse.equinox.launcher_*.jar`

echo java -cp "$ECLIPSE_PLUGINS_DIR" \
     -jar "$LAUNCHER_PATH" \
     -application org.joe_e.Main \
     -taming "$TAMING_DIR" -classpath "$CLASSPATH" "$@"

java -cp "$ECLIPSE_PLUGINS_DIR" \
     -jar "$LAUNCHER_PATH" \
     -application org.joe_e.Main \
     -taming "$TAMING_DIR" -classpath "$CLASSPATH" "$@"

