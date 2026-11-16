# Modernization log

- Inspected clean `master` at `34c5815`; all original commits credit Kléber de Oliveira Andrade. Preserved the existing MIT license and screenshot.
- Confirmed the original Java sources fail to compile: `Criatura` was deleted while `Zumbi` and `MundoBuilder` still reference it.
- Adopted Java 21 LTS and pinned Maven/JUnit/plugins using official Maven Central metadata. No runtime dependencies are needed.
- Started with characterization tests for floor/wall movement, three-hit defeat and map borders before changing rules. Moving the original sources into Maven layout is a mechanical migration.
- Red/green: boundary access reproduced `ArrayIndexOutOfBoundsException`; fixed out-of-bounds coordinates as walls. Four characterization tests passed before the first commit.
- Removed machine-specific NetBeans metadata and generated build markers after migrating the source layout.
- Added test-first scenarios for seeded generation, connected caves, bounded spawning, turn order, stacked damage, zombie directions, terminal defeat, EOF, quit and CLI input. Observed missing-API compilation failures before implementation.
- Separated `domain`, `generation` and `io`; retained the original entry point. Replaced global randomness with injectable `RandomGenerator`; a single seeded `Random` replays generation and turns.
- Preserved survival rules, default dimensions, lives, zombie count, symbols, waiting on unknown input and overlap rules. Added explicit quit, clean EOF and defeat messages.
- Replaced unbounded spawn retries with sampling existing floor cells (with replacement, preserving overlaps). Invalid generation now fails immediately.
- Ran 17 JUnit tests successfully, including connectivity across 30 seeds. Dryer found no candidates at similarity 0.8. Crapper identified one CRAP 11 method; extracted walk validation to bring every method below 10.
