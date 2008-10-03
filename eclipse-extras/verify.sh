#! /bin/sh

# you will need to fill out these variables with: 
# the full path to your eclipse plugins directory
# The full path to your taming database
# the full path to your joe-e library
# before you run the command line verifier

ECLIPSE_PLUGINS_DIR="/Applications/eclipse/plugins/"
TAMING_DIR="/Users/akshay/School/Research/Joe-e/taming/"
LIBRARY_JAR="/Users/akshay/Downloads/library-2.1.0.jar"

LAUNCHER=`ls $ECLIPSE_PLUGINS_DIR | grep -i org.eclipse.equinox.launcher.*\.jar`
CMD="java -cp $ECLIPSE_PLUGINS_DIR -jar $ECLIPSE_PLUGINS_DIR/$LAUNCHER -application org.joe_e.Main --taming $TAMING_DIR --library $LIBRARY_JAR $@"
$CMD
