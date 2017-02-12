# homeseer-brains
The Brains behind my install of Homeseer

HomeSeer scripts are written in VB.Net. While I won't debate the merits of that language, 
it's notable that it's impossible to write tests for these scripts.

So, I'm going to have HomeSeer call to a local webserver to calculate dim levels, audio/video settings and a slew of other
things.

At this point, HomeSeer will still be the master and know the current state of everything. The brains will just be stateless 
and calculate actions based on information passed to it.
