#!/bin/sh
# Wraps jar file
java -jar dist/campfireclient-0.1-dev.jar ${1+"$@"}
