#!/bin/bash

fileName="ttyUSB0"
device="/dev/$fileName"

# Set permissions during boot
if [ -e ${device} ]
then
  stty -F /dev/ttyUSB0 19200 raw
fi

# And then set them again any time the port is created
inotifywait -m /dev/ --format '%f' -e create |
  if [[ "$file" == ttyUSB* ]]; then
    stty -F /dev/$file 19200 raw
  fi
