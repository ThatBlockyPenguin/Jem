# Jem
Jem is a Gemini browser I am working on, implemented in java. It is \*VERY\* work-in-progress, and does not yet fully implement the Gemini protocol specification. Experimental downloads are available at the bottom of this page.

## Release 0.1.0!
That's right! I have officially released version 0.1.0 of Jem! It is now capable of loading most capsules, and you can expect plugin support coming soon! Please do send me feedback at: thatblockypenguin@tilde.team

## Versioning:
The first version uploaded to the [official downloads page](gemini://tilde.team/~thatblockypenguin/jem/) was erroneouly labelled as "1.0-SNAPSHOT".
Inbetween that and release 1.0, I will be using semantic versioning as described in AloisMahdal's github issue: [Convention: how to get from 0.0.0 to 1.0.0](https://github.com/semver/semver/issues/363)

(Copied on 15th Apr 2023)
1. First assign version 0.0.0.  
   Nothing has to work or even exist.  
   Although you already may want to set up things like CI, build system or some simple branching scheme.
2. At some point you proceed to 0.0.z.  
   But in this phase it really means "0.0.MAJOR". Ie. things are generally expected to work  
   but anything can break with any upgrade.
3. As you feel a bit more confident, you proceed to 0.y.z.  
   But in this phase it really means "0.MAJOR.MINOR".  
   This is when you first start to apply SemVer principles: you are still in the "privileged" 0.y.z range, but you already start to distinguish between breaking and non-breaking changes.
4. Eventually, you want to get serious so you release the glorious 1.0.0.

Once 1.0.0 is released, standard SemVer will be used.

## Current State
Jem is currently being completely rewritten after 1.0-SNAPSHOT to be more of a framework for smolnet browsers (albeit initially very Gemini-focussed), so in the future it should be easily possible to give it support for Gohper, Finger, or realistically any other protocol. As such, expect *LOTS* of breaking changes.

### NOTE: This rewrite is now mostly complete, and only minor changes and fixes are planned to take place.

## Download Experimental Builds
Note: Jem Requires you to have at least Java 21 installed in order for it to run. I would recommend Eclipse Temurin if you don't have it already.

[Get Jem](https://tilde.team/~thatblockypenguin/jem/downloads)

[Eclipse Temurin Java Download (web)](https://adoptium.net)