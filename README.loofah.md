Loofah
=============

Loofah is a SpongeAPI implementation targeting the [Fabric Loader platform](https://fabricmc.net/).

## Project State
Loofah is still newly started and **HEAVILY WORK IN PROGRESS**. At this point the game launches
on both client and the dedicated server but opening worlds on clients crashes and players can't join the dedicated server.
The hope is to eventually have it just as functional as the official implementations.
While also providing some integration and compatibility features to integrate nicely into the rest of the Fabric ecosystem.

[Quilt](https://quiltmc.org/en/) compatibility would also be nice but will only be on the radar for official support until
after getting to the first proper release. Probably later than that.

## Project Structure
Following the style of Upstream, Loofah is implemented on top of (a slightly modified for fabric compatibility) SpongeCommon
and lives mostly in the `loofah` subdirectory of this repo in the `loofah/*` branches.
If you plan on helping thank you very much!! Based on this structure please keep contributions inside the `loofah` subdirectory
unless otherwise is **Absolutely Necessary**. Like when modifying SpongeCommon for Fabric compatibility.
