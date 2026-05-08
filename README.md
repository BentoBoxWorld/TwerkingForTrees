# TwerkingForTrees

[![Discord](https://img.shields.io/discord/272499714048524288.svg?logo=discord)](https://discord.bentobox.world)
[![Build Status](https://ci.codemc.org/buildStatus/icon?job=BentoBoxWorld/TwerkingForTrees)](https://ci.codemc.org/job/BentoBoxWorld/job/TwerkingForTrees/)

**Tired of staring at saplings, waiting for them to grow?** Drop it low and watch them go! TwerkingForTrees is a [BentoBox](https://github.com/BentoBoxWorld/BentoBox) addon that lets players speed up tree growth by twerking (rapidly crouching) near saplings. Because sometimes nature just needs a little encouragement.

## How It Works

1. Plant a sapling on your island
2. Stand near it and crouch repeatedly (a.k.a. *twerk*)
3. Hit the twerk threshold and your sapling bursts into a full-grown tree, complete with sound effects and particle sparkles

That's it. No bone meal needed. Just vibes.

## Features

- **All tree types supported** — Oak, Birch, Spruce, Jungle, Acacia, Dark Oak, Azalea, Mangrove, Cherry, and Pale Oak
- **Mega tree support** — Place saplings in a 2x2 grid for Dark Oak, Jungle, Spruce, and Pale Oak mega trees, then twerk them into existence
- **Customizable sounds & particles** — Configure the twerk sound, tree growth sounds, and visual effects to your liking
- **Configurable twerk threshold** — Set how many twerks are required before growth kicks in (default: 4)
- **Adjustable range** — Control how far from the player saplings are detected (default: 5 blocks in all directions)
- **Accessibility modes:**
  - **Hold-to-twerk** — Hold the crouch button instead of mashing it repeatedly
  - **Sprint-to-grow** — Sprint near saplings instead of crouching, for players who prefer a different groove

## Installation

1. Install [BentoBox](https://github.com/BentoBoxWorld/BentoBox) on your Spigot/Paper server
2. Drop the `TwerkingForTrees` JAR into the `plugins/BentoBox/addons/` folder
3. Restart your server
4. Configure to taste in `plugins/BentoBox/addons/TwerkingForTrees/config.yml`

## Configuration

All settings live in `config.yml`. Here are the highlights:

| Setting | Default | Description |
|---|---|---|
| `minimum-twerks` | `4` | Twerks required to trigger growth |
| `range` | `5` | Block radius to scan for saplings |
| `hold-for-twerk` | `false` | Hold crouch instead of toggling |
| `sprint-to-grow` | `false` | Sprint near saplings to grow them |
| `sounds.enabled` | `true` | Play sounds on twerk & growth |
| `effects.enabled` | `true` | Show particle effects |

Sounds and effects are fully customizable — see the config file for all options.

## Compatibility

Works with all major BentoBox gamemodes:

- BSkyBlock
- AcidIsland
- SkyGrid
- CaveBlock

**Requires:** Java 21, Paper 1.21.3+, BentoBox 3.14.0+

## Permissions

Players need the `[gamemode].twerkingfortrees` permission to use the addon (e.g., `bskyblock.twerkingfortrees`). This is granted by default.

## Links

- [Wiki](https://github.com/BentoBoxWorld/TwerkingForTrees/wiki)
- [Discord](https://discord.bentobox.world)
- [Issue Tracker](https://github.com/BentoBoxWorld/TwerkingForTrees/issues)

---

*If you enjoy this addon, consider sponsoring the project. Every twerk counts.*
