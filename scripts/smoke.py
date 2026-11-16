#!/usr/bin/env python3
"""Exercise the packaged application; requires a completed Maven package."""
from pathlib import Path
import subprocess
import zipfile

ROOT = Path(__file__).resolve().parents[1]
JAR = ROOT / "target/roguelike-java-1.0.0.jar"


def run(*args, commands="", status=0):
    result = subprocess.run(
        ["java", "-jar", str(JAR), *args], input=commands, text=True,
        encoding="utf-8", capture_output=True, timeout=30, cwd=ROOT,
    )
    assert result.returncode == status, (result.returncode, result.stderr)
    assert not result.stderr, result.stderr
    return result.stdout


with zipfile.ZipFile(JAR) as archive:
    assert archive.read("META-INF/LICENSE") == (ROOT / "LICENSE").read_bytes()

assert "--seed" in run("--help")
assert "Invalid arguments" in run("--seed", "invalid", status=2)
assert "Partida encerrada." in run("--seed", "42")
commands = (ROOT / "examples/replay.txt").read_text(encoding="utf-8")
first = run("--seed", "42", commands=commands)
assert first == run("--seed", "42", commands=commands)
assert "[A/W/S/D]: " in first and "Partida encerrada." in first
assert "Fim de jogo!" in run("--seed", "42", commands="\n" * 10000)
print("Packaged JAR: help, invalid arguments, EOF, seeded replay and defeat passed.")
