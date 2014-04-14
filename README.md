wolfplaysfair
=============

Wolf Plays Fair -- my own wolf submission for the ongoing code-golf challenge. 
It doesn't like cheaters, and it keeps close tabs on the map.

Basically, Wolf Plays Fair checks that Attacks, Moves, and the PRNG are fair. If they aren't, it kills itself.

As well, it keeps track of how many of itself have been created. Any beyond 100 just suicide.

Finally, it uses a private internal "implementation" to obfuscate its internal processes from its "public" persona.

Beyond "Fairness" features, it does something a bit unusual:
1. Avoids Lions.
2. Waits quite a while
3. Begins moving around the map, keeping track of things around itself.

Eventually, I'll use the map data to smartly navigate, and perhaps even collate the maps between all
instances for better map knowledge.
