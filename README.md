# Ponderosa
The Brains behind my install of Homeseer

HomeSeer scripts are written in VB.Net. While I won't debate the merits of that language,
it's notable that it's impossible to write tests for these scripts.

So, I'm going to have HomeSeer call to a local webserver to calculate dim levels, audio/video settings and a slew of other
things.

At this point, HomeSeer will still be the master and know the current state of everything. The brains will just be stateless
and calculate actions based on information passed to it.


### TODOs
* Get HomeSeer to actually ask Ponderosa about the light levels


## How does it work?
Magic.

###Hardware
I've got a Homeseer SEL, which is basically just a Ubuntu 14.04 box. Attached are a couple of USB modules:
* Z-Wave SmartStick+ to control the Z-Wave network. As of now, this is just lights. I plan on buying a few of those
cheap Aeon Remotes to be able to easily send events which can control the audio system.
* A USB->RS232 adapter. The RS-232 adapter is connected to a Russound CAA-66 system which allows me
to control audio throughout the house. This will definitely be expanded and upgraded, but it's a great start.

###Software
The box is shouldRun a program called Homeseer. It's tolerable and does its job. You can script things w/ it, but the problem
is it's an ancient version of VB.NET. Which precludes writing tests or generally doing anything useful.

So I've got another web-app shouldRun on port 8080 to be the brains behind the project. This is written in Kotlin and shouldRun
on a small Java framework called Spark.
* To control lights, HomeSeer sends a web request to this service to ask what the light level should be
* To control audio, you hit a React webapp that generates a very simple control page. When you make changes on the
web page it POSTS to the service which writes out bytes to /dev/ttyUSB0 and in turn controls the Russound CAA-66!

Amazing!


## Developing

Trying to figure this out now.
* First time: run `cd src/main/react && npm install`
* You'll need to fill out `src/main/resources/twilio.properties`. See the sample file for what's needed.
* To deploy you'll need some ssh love in `./unforntunately`.
* React runs on :3000, currently hitting 192.168.1.5:8080. Should hit either localhost:4567 or a mock.
* Ponderosa Service runs fine, but needs the serial device to be mocked out.

### IDEA Errors
If you see something about an AbstractMethodError in KotlinCoreEnvironment but the build works from the command line, delete
this line in `.idea/modules/ponderosa_[main|test].iml`
```xml
<option name="additionalArguments" value="-Xplugin /home/tomasz/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-annotation-processing/1.1.0-rc-91/5a3a018b3d9948d50cfe1af741d10f016edcc12c/kotlin-annotation-processing-1.1.0-rc-91.jar,/home/tomasz/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-noarg/1.1.0-rc-91/cce5206f28940c7751ca131caeedc591cbc44af9/kotlin-noarg-1.1.0-rc-91.jar,/home/tomasz/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-allopen/1.1.0-rc-91/ea929388f4abd559e160aea641b11cb7e45c2cc0/kotlin-allopen-1.1.0-rc-91.jar -P plugin:org.jetbrains.kotlin.noarg:annotation=javax.persistence.Entity,plugin:org.jetbrains.kotlin.allopen:annotation=org.springframework.stereotype.Component,plugin:org.jetbrains.kotlin.allopen:annotation=org.springframework.transaction.annotation.Transactional,plugin:org.jetbrains.kotlin.allopen:annotation=org.springframework.scheduling.annotation.Async,plugin:org.jetbrains.kotlin.allopen:annotation=org.springframework.cache.annotation.Cacheable" />
```

## Twilio
We use Twilio to notify us about motion events. The keys are defined in twilio.properties.
Refer to `src/main/resources/twilio.properties.sample` for an example of what is needed.


## Lighting
We calculate light levels on a range is adjusted from sunrise and sunset data.

### Sunrise & SunsetData
I leached data for Lafayette from [sunrise-sunset.org](http://sunrise-sunset.org/) until the end of 2050. Those files are
available on [Github](https://github.com/holmes/sunrise-data). They're currently checked out in `/opt/sunrise-data/`. That path needs
to be passed to `Ponderosa` during initialization.


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
