#!/bin/sh

# You will need to fill out these variables with: 
# The full path to your eclipse plugins directory (the location of the file
#   org.eclipse.equinox.launcher.<someversion>.jar)
# The full path to your taming database
# the full path to your Joe-E library (in the format of a classpath entry,
#   e.g. a JAR file)
# before you run the command line verifier

ECLIPSE_PLUGINS_DIR=""
TAMING_DIR=""
LIBRARY_CLASSPATH=""

CLASSPATH=$LIBRARY_CLASSPATH:$CLASSPATH

#LAUNCHER=`ls $ECLIPSE_PLUGINS_DIR | grep -i org.eclipse.equinox.launcher.*\.jar`
java -cp $ECLIPSE_PLUGINS_DIR \
     -jar $ECLIPSE_PLUGINS_DIR/org.eclipse.equinox.launcher.*.jar \
     -application org.joe_e.Main \
     -taming $TAMING_DIR -classpath $CLASSPATH "$@"

