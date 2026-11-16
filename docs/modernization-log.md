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
- Added pinned GitHub Actions for Linux/Windows, monthly dependency update configuration, a file-based CLI replay and a Java source-launcher example. Parsed workflow YAML and checked all relative documentation links.
- Verified the Maven Wrapper from an empty temporary cache, including its SHA-256 distribution check, using Homebrew OpenJDK 21.0.13.0.3 on macOS ARM64.
- Exercised the packaged JAR: help, invalid arguments/status 2, EOF, repeatable seed-42 replay and a complete waiting session ending in defeat. Ran the tiny domain arena: defeat after three turns.
- Updated and independently read back GitHub About for `kleberandrade/roguelike-java`: “A turn-based Java ASCII survival roguelike with procedural caves, wandering zombies, and seeded replays.” Confirmed the default branch is `master`.
- Replaced the README with English setup, controls, mechanics, runnable examples, API/package layout, migration notes and explicit limitations. Preserved the historical screenshot link and MIT license; retained Wrapper ASF headers and included Apache 2.0 licensing separately. Package the game license into the JAR without duplicating its source file.
- First full mutation pass: 85 killed, 36 survived and 12 uncovered sites. Added behavioral checks for zero/overkill damage, asymmetric destination collisions, exact walk length/directions, independent border validation, floor-edge spawning, starting game rules and visible zombie rendering. All 28 tests passed. The surviving mutants supplied the observed failures for this test-strengthening pass.

## Reproduction

```sh
./mvnw --batch-mode --no-transfer-progress clean verify
python3 scripts/smoke.py
java --class-path target/classes examples/SurvivalExample.java
crapper --use-existing-coverage --threshold 9.99 src/main/java
dryer --threshold 0.8 src/main/java
mutator --use-existing-coverage --max-workers 2 --test-command './mvnw --batch-mode --no-transfer-progress test' src/main/java
```

The local run used a Maven artifact cache and wrapper cache under `/private/tmp` because Maven was not installed and the default macOS Java launcher had no configured runtime. `JAVA_HOME`/`PATH` pointed to the existing Homebrew JDK 21. All source edits and commits are confined to this repository. No push, package publication or deployment was performed.

Version/support references: [Java support roadmap](https://www.oracle.com/java/technologies/java-se-support-roadmap.html), [Maven Central metadata](https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/maven-metadata.xml), [JUnit metadata](https://repo.maven.apache.org/maven2/org/junit/jupiter/junit-jupiter/maven-metadata.xml), [Actions releases](https://github.com/actions/setup-java/releases).

## Final validation and limits

- Final clean Java 21 build: **29 JUnit tests passed**, no failures or errors. Packaged CLI smoke checks passed for help, invalid arguments, EOF, deterministic replay and an actual session ending in defeat. The standalone source example printed `Defeat after 3 turns.`
- Added independent vertical collision and same-column/different-row contact checks after mutation analysis exposed those gaps. Coordinate comparisons also cover null and foreign types.
- Crapper, using the final JaCoCo report: maximum CRAP **6**, all methods below 10. Dryer at threshold **0.8**: no duplicate candidates.
- Cumulative mutator snapshot: **133 sites, 119 killed, 5 survived, 9 uncovered**. All executed gameplay-rule mutations were killed. Mutator exits with status 3 because five equivalent mutations remain: four change the coordinate hash formula while preserving equality/hash consistency; one reads the height from column 1 instead of column 0 in the rectangular world. No artificial exact-hash tests or ragged maps were added to force a score.
- The nine uncovered mutation sites are the legacy coordinate diagnostic `toString` (five), the process launcher `Main.main` (three) and the retained convenience launcher `Jogo.executar` (one). The packaged smoke checks exercise the real process launcher, outside JaCoCo's unit-test JVM. Mutation discovery does not establish exhaustive coverage of Java language constructs or constructors.
- Final JaCoCo unit-test report: 1,040/1,105 instructions, 192/205 lines and 104/107 branches covered. These are local measured results, not badge claims. The remaining gaps include process-bound I/O wrappers and retained diagnostic/accessor methods.
- Fresh Maven Wrapper download and SHA-256 validation succeeded using a temporary cache. The JAR includes the unchanged MIT license under `META-INF/LICENSE`.
- JAR SHA-256 matched across a clean rebuild: `1c40659c2f2f45a27e2ee9fab1db24f21e2833eb5c203190d76fefbc0d391b41`. This is a local reproducibility check, not a claim of binary equivalence across operating systems or JDK vendors.
- Parsed both YAML configuration files, checked relative documentation links and ran `git diff --check` successfully. CI targets Linux/Windows but has not run remotely; local execution used macOS ARM64 and Homebrew OpenJDK 21.0.13.0.3.
- GitHub About was updated and independently read back from `kleberandrade/roguelike-java`: **A turn-based Java ASCII survival roguelike with procedural caves, wandering zombies, and seeded replays.** Default branch confirmed as `master`.
- No victory mechanic was invented: the original game remains survival until defeat or voluntary exit. Classroom roadmap ideas remain documented as unimplemented.
- Original MIT copyright and screenshot remain intact. Maven Wrapper ASF headers are preserved, with the Apache 2.0 license included separately. No runtime third-party libraries were added.
- Changes are committed locally in ordered build, domain/I/O, CI/example, test and documentation units. Push remains pending explicit authorization; no deployment or package publication was attempted.

## Authorized publication and branch migration

- The user subsequently authorized pushing this repository and changing `master` to `main`, with CI monitoring until success. The earlier no-push notes describe the initial local modernization.
- Confirmed the remote still pointed to the original `34c5815`, with no incoming commits and no existing `main` branch.
- Updated the CI push trigger and badge to `main`, and documented how existing clones can update their tracking branch. Publication uses a normal fast-forward push; no force push or package deployment is involved.

## Dependabot review and integration — 2026-11-16

- Reviewed [PR #1](https://github.com/kleberandrade/roguelike-java/pull/1): Maven Resources Plugin 3.3.1 → 3.5.0, a single pinned build-plugin update. Verified that the existing packaged smoke check still compares `META-INF/LICENSE` with the canonical MIT license.
- Reviewed [PR #2](https://github.com/kleberandrade/roguelike-java/pull/2): Maven Wrapper distribution 3.9.16 → 3.10.1, including the updated SHA-256. Validated a fresh wrapper download and Java 21 execution. Consulted the [Maven release notes](https://maven.apache.org/docs/3.10.1/release-notes.html), including stricter POM validation and Resolver/classpath changes; the project's explicit plugin versions and test-only JUnit dependency passed the real build.
- Review verdict: both approved for integration; no critical, major or minor implementation findings and no author clarification needed. The updates keep versions and the distribution checksum pinned; no gameplay code, runtime dependency or CI permissions changed.
- Both exact reviewed heads passed Linux/Windows PR checks and independent local `clean verify` runs with 29 tests and packaged smoke checks. The source example also passed with the new wrapper and after combining both upgrades. Merged through GitHub with SHA preconditions: #1 as `8568af8`, #2 as `2b03f7d`.
- Updated the README's Maven badge and requirement text to 3.10.1. The earlier validation entries describe their original toolchain; new CI runs on `main` validate the combined upgrades.
