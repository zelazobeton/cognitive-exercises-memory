#!/bin/sh

# Run the main container command.
exec java -Dspring.profiles.active=bootstrap -Djava.security.egd=file:/dev/./urandom -jar ./cognitive-exercises-memory.jar
