# Migration from the NetBeans project

## Build and launch

The old `RoquelikeProject/` Ant/NetBeans project targeted Java 8 and contained private machine paths and generated build markers. It also failed to compile after deletion of `Criatura.java`.

Open the repository root `pom.xml` in your IDE, select JDK 21 and build with `./mvnw clean verify` (`./mvnw.cmd clean verify` on Windows). The old directory and IDE metadata have been removed. Sources now follow Maven's `src/main/java` and `src/test/java` layout. The output is `target/roguelike-java-1.0.0.jar`; run it with `java -jar`.

`br.unisal.aula.Main` remains the application entry point. The game remains a Portuguese, line-based ASCII survival game with the original default dimensions, three lives, 20 zombies and WASD controls.

## Source API changes

This classroom application did not expose a versioned library API. Imports for `Celula`, `Entidade`, `Jogador`, `Mundo`, `Personagem`, `Ponto2D` and `Zumbi` now use `br.unisal.aula.domain`. `MundoBuilder` moved to `br.unisal.aula.generation`; console input/rendering moved to `br.unisal.aula.io`.

- `Zumbi` extends the existing `Personagem` instead of the missing `Criatura`.
- `Jogador` no longer creates a scanner or reads `System.in`. Use `Mundo.atualizar(String)` for turns and `ConsoleGame` for interactive sessions.
- `Mundo.desenhar()` is replaced by `ConsoleRenderer.render(Mundo)`, which returns a string.
- `Jogo.criarMundo()` returns the initialized world. `Jogo.executar()` still starts the console game.
- `MundoBuilder` operations are public and support an injected `RandomGenerator`. Zombies also support injected randomness. A shared seeded `Random` reproduces both generation and turns.
- Map boundary checks treat outside coordinates as blocked. Invalid generation requests throw immediately instead of crashing later or looping forever.

Random choices now use bounded `nextInt` instead of floating-point modulo arithmetic. Spawning directly samples the list of floor cells, preserving permitted overlaps. This changes the old biased distribution while preserving gameplay and preventing endless retries.

## Input and end states

Unknown/empty input and blocked moves still consume turns. Zombies still move after the player and deal contact damage after moving. Death ends survival; no victory mechanic has been added. Commands now also support `Q`, graceful EOF and `--seed`/`--help` arguments. `Fim de jogo!` explicitly reports defeat.

The existing MIT license remains unchanged. The old screenshot remains at its original path, now linked relatively from the README.

## Default branch rename

The default branch is now `main`. For an existing clone with a local `master` branch, update the tracking configuration after saving any local work:

```sh
git branch -m master main
git fetch origin
git branch --set-upstream-to=origin/main main
git remote set-head origin -a
```

Fresh clones select `main` automatically. CI push triggers and the README badge follow `main`.
