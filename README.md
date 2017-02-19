# Ponderosa
The Brains behind my install of Homeseer

HomeSeer scripts are written in VB.Net. While I won't debate the merits of that language,
it's notable that it's impossible to write tests for these scripts.

So, I'm going to have HomeSeer call to a local webserver to calculate dim levels, audio/video settings and a slew of other
things.

At this point, HomeSeer will still be the master and know the current state of everything. The brains will just be stateless
and calculate actions based on information passed to it.

## RS-232 Connections
Ubuntu already has the Prolific drivers installed. So we just need to configure a few things.

### Permissions
Add jetty and homeseer to the dialout group 
```
sudo adduser jetty dialout
sudo adduser homeseer dialout
```    
    
### Set stty properties
Each time the cable is connected we have to reset the baud rate to 19200. There's a script in /scripts to do just that

We want to run the script when homeseer boots, so append `sudo /etc/wait-and-set-stty.sh` to `/etc/rc.local`

And test it!
```
# Turn on Zone 2
echo -en '\xF0\x00\x00\x7F\x00\x00\x70\x05\x02\x02\x00\x00\xF1\x23\x00\x01\x00\x01\x00\x01\x13\xF7' > /dev/ttyUSB0

# Turn off Zone 2
echo -en '\xF0\x00\x00\x7F\x00\x00\x70\x05\x02\x02\x00\x00\xF1\x23\x00\x00\x00\x01\x00\x01\x12\xF7' > /dev/ttyUSB0
```


## Settings
These are the current settings - they're in the code but it might be easier to see them here as well.

###Zones
Turn on volumes are in the ()'s
1. Family Room (26)
1. Kitchen (26)
1. Outside (60)
1. Master (20)
1. Nursery (planned)

###Sources
1. Family Room TV
1. Chromecast (Family Room)
