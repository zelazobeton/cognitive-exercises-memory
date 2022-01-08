#!/bin/sh

# Run the main container command.
exec java -Djava.security.egd=file:/dev/./urandom -jar ./cognitive-exercises-memory.jar

