# Modernization log

- Inspected clean `master` at `34c5815`; all original commits credit Kléber de Oliveira Andrade. Preserved the existing MIT license and screenshot.
- Confirmed the original Java sources fail to compile: `Criatura` was deleted while `Zumbi` and `MundoBuilder` still reference it.
- Adopted Java 21 LTS and pinned Maven/JUnit/plugins using official Maven Central metadata. No runtime dependencies are needed.
- Started with characterization tests for floor/wall movement, three-hit defeat and map borders before changing rules. Moving the original sources into Maven layout is a mechanical migration.
