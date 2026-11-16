#!/usr/bin/env python3
"""Validate release versions and prepare verified artifacts without network access."""
import argparse
import hashlib
from pathlib import Path
import re
import shutil
import subprocess
import zipfile

ROOT = Path(__file__).resolve().parents[1]
VERSION = re.compile(r'v((?:0|[1-9][0-9]*)\.(?:0|[1-9][0-9]*)\.(?:0|[1-9][0-9]*)(?:-[0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*)?)')


def release_version(tag):
    match = VERSION.fullmatch(tag)
    if not match:
        raise ValueError('Use vMAJOR.MINOR.PATCH or vMAJOR.MINOR.PATCH-prerelease')
    version = match.group(1)
    prerelease = version.partition('-')[2]
    if any(part.isdigit() and len(part) > 1 and part.startswith('0') for part in prerelease.split('.')):
        raise ValueError('Numeric prerelease identifiers must not have leading zeros')
    return version


def validate_jar(jar, version, license_path):
    with zipfile.ZipFile(jar) as archive:
        if archive.testzip() is not None:
            raise ValueError('JAR integrity check failed')
        manifest = archive.read('META-INF/MANIFEST.MF').decode('utf-8').replace('\r\n ', '')
        attributes = dict(line.split(': ', 1) for line in manifest.splitlines() if ': ' in line)
        if attributes.get('Main-Class') != 'br.unisal.aula.Main':
            raise ValueError('JAR must contain the executable game entry point')
        if attributes.get('Implementation-Version') != version:
            raise ValueError('JAR version does not match the release tag')
        if archive.read('META-INF/LICENSE') != license_path.read_bytes():
            raise ValueError('JAR must embed the original MIT license')


def history_url(root, tag, commit, repository):
    previous = subprocess.run(['git', 'describe', '--tags', '--match', 'v[0-9]*', '--abbrev=0', commit + '^'],
                              cwd=root, text=True, capture_output=True)
    if previous.returncode == 0:
        return f'https://github.com/{repository}/compare/{previous.stdout.strip()}...{tag}'
    return f'https://github.com/{repository}/commits/{tag}'


def prepare(root, tag, commit, repository, output, changes_url=None):
    version = release_version(tag)
    if not re.fullmatch(r'[0-9a-f]{40}', commit):
        raise ValueError('Release commit must be a full Git SHA')
    if not re.fullmatch(r'[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+', repository):
        raise ValueError('Repository must use owner/name format')
    if output.exists():
        raise ValueError('Output directory already exists; choose a fresh directory')
    jar = root / 'target' / f'roguelike-java-{version}.jar'
    validate_jar(jar, version, root / 'LICENSE')
    digest = hashlib.sha256(jar.read_bytes()).hexdigest()
    template = (root / 'docs/release-notes-template.md').read_text(encoding='utf-8')
    notes = template.format(tag=tag, version=version, commit=commit, repository=repository,
                            jar=jar.name, sha256=digest,
                            changes_url=changes_url or history_url(root, tag, commit, repository))
    output.mkdir(parents=True)
    shutil.copy2(jar, output / jar.name)
    (output / (jar.name + '.sha256')).write_text(f'{digest}  {jar.name}\n', encoding='utf-8')
    (output / 'release-notes.md').write_text(notes, encoding='utf-8')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    sub = parser.add_subparsers(dest='command', required=True)
    version = sub.add_parser('version')
    version.add_argument('tag')
    stage = sub.add_parser('prepare')
    stage.add_argument('--tag', required=True)
    stage.add_argument('--commit', required=True)
    stage.add_argument('--repository', required=True)
    stage.add_argument('--output', type=Path, default=ROOT / 'target/release')
    args = parser.parse_args()
    try:
        if args.command == 'version':
            print(release_version(args.tag))
        else:
            prepare(ROOT, args.tag, args.commit, args.repository, args.output)
    except (ValueError, OSError, KeyError, zipfile.BadZipFile) as error:
        parser.error(str(error))


if __name__ == '__main__':
    main()
