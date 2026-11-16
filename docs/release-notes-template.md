# Roguelike Java {tag}

A turn-based Java ASCII survival roguelike with procedural caves, wandering zombies and reproducible seeded sessions. The Portuguese console interface and original survival rules are preserved.

## Changes

[View the commits included in this release]({changes_url}).

Built from commit [`{commit}`](https://github.com/{repository}/commit/{commit}).

## Download and play

Download **`{jar}`** from the assets below. Java 21 is required; no external runtime libraries are needed.

```sh
java -jar {jar}
java -jar {jar} --seed 42
```

Enter `W`, `A`, `S` or `D`, then press Enter to move. Enter `Q` to quit. Empty or unknown commands wait for one turn. Survive with three lives while 20 zombies wander the cave; there is no victory condition or save/load system.

## Validation

The release pipeline builds with the pinned Maven Wrapper and Java 21 on Linux and Windows. It runs JUnit tests, release tooling tests, packaged JAR smoke checks and the executable domain example before publishing. The JAR manifest version, entry point and embedded MIT license are checked against the release tag.

## Integrity and license

SHA-256 of `{jar}`:

```text
{sha256}
```

A matching `{jar}.sha256` file is attached. The game is licensed under MIT, copyright 2019 Kléber de Oliveira Andrade. Maven Wrapper scripts retain their upstream Apache 2.0 notices in the source distribution.
