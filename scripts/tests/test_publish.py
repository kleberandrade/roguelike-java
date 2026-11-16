"""Exercise the publication state machine with a fake GitHub CLI, never the network."""
import json
import os
import shutil
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest

SCRIPT = Path(__file__).resolve().parents[1] / 'publish-release.sh'
FAKE = '''import json, os, sys
from pathlib import Path
args = sys.argv[1:]
with Path(os.environ['FAKE_GH_LOG']).open('a') as output:
    output.write(json.dumps(args) + '\\n')
if args[0] == 'api':
    sys.stdout.buffer.write((os.environ.get('FAKE_TAG_SHA', os.environ['GITHUB_SHA']) + '\\n').encode())
elif args[:2] == ['release', 'view']:
    state = os.environ['FAKE_RELEASE_STATE']
    if state == 'missing':
        sys.exit(1)
    sys.stdout.buffer.write((state + '\\n').encode())
elif args[:2] == ['release', 'upload'] and os.environ.get('FAKE_UPLOAD_FAIL'):
    sys.exit(1)
'''


class PublishTests(unittest.TestCase):
    def publish(self, state='missing', tag='v2.3.4', **extra):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            executable = root / 'gh'
            executable.write_bytes(b'#!/usr/bin/env bash\nexec "$FAKE_PYTHON" "$FAKE_GH_SCRIPT" "$@"\n')
            executable.chmod(0o755)
            fake = root / 'fake.py'
            fake.write_text(FAKE)
            version = tag[1:]
            for name in [f'roguelike-java-{version}.jar', f'roguelike-java-{version}.jar.sha256', 'release-notes.md']:
                (root / name).write_text('fixture')
            log = root / 'calls.jsonl'
            env = dict(os.environ, PATH=str(root) + os.pathsep + os.environ['PATH'],
                       RELEASE_TAG=tag, RELEASE_VERSION=version, GH_REPO='owner/repo', GITHUB_SHA='a' * 40,
                       FAKE_PYTHON=sys.executable.replace(chr(92), "/"), FAKE_GH_SCRIPT=str(fake), FAKE_GH_LOG=str(log), FAKE_RELEASE_STATE=state)
            env.update(extra)
            bash = shutil.which('bash')
            if os.name == 'nt':
                bash = str(Path(shutil.which('git')).parents[1] / 'bin/bash.exe')
            result = subprocess.run([bash, '-x', SCRIPT.as_posix(), root.as_posix()], env=env, text=True, capture_output=True)
            calls = [json.loads(line) for line in log.read_text().splitlines()] if log.exists() else []
            return result, calls

    def test_new_release_is_draft_until_both_assets_are_uploaded(self):
        result, calls = self.publish()
        self.assertEqual(0, result.returncode, result.stdout + result.stderr)
        operations = [call[1] for call in calls if call[0] == 'release']
        self.assertEqual(['view', 'create', 'upload', 'edit'], operations)
        create = next(call for call in calls if call[:2] == ['release', 'create'])
        upload = next(call for call in calls if call[:2] == ['release', 'upload'])
        edit = next(call for call in calls if call[:2] == ['release', 'edit'])
        self.assertIn('--verify-tag', create)
        self.assertIn('--draft', create)
        self.assertIn('roguelike-java-2.3.4.jar', upload)
        self.assertIn('roguelike-java-2.3.4.jar.sha256', upload)
        self.assertIn('--draft=false', edit)

    def test_existing_draft_resumes_without_creating_another_release(self):
        result, calls = self.publish(state='true')
        self.assertEqual(0, result.returncode, result.stdout + result.stderr)
        self.assertEqual(['view', 'upload', 'edit'], [c[1] for c in calls if c[0] == 'release'])

    def test_published_release_is_never_overwritten(self):
        result, calls = self.publish(state='false')
        self.assertEqual(0, result.returncode, result.stdout + result.stderr)
        self.assertEqual(['view'], [c[1] for c in calls if c[0] == 'release'])

    def test_upload_failure_keeps_the_release_unpublished(self):
        result, calls = self.publish(FAKE_UPLOAD_FAIL='1')
        self.assertNotEqual(0, result.returncode)
        self.assertIn('upload', [c[1] for c in calls if c[0] == 'release'])
        self.assertNotIn('edit', [c[1] for c in calls if c[0] == 'release'])

    def test_tag_must_still_point_to_the_built_commit(self):
        result, calls = self.publish(FAKE_TAG_SHA='b' * 40)
        self.assertNotEqual(0, result.returncode)
        self.assertTrue(calls, result.stdout + result.stderr)
        self.assertEqual('api', calls[0][0])
        self.assertFalse(any(c[0] == 'release' for c in calls))

    def test_prerelease_tags_are_marked_as_prereleases(self):
        result, calls = self.publish(tag='v2.3.4-rc.1')
        self.assertEqual(0, result.returncode, result.stdout + result.stderr)
        edit = next(call for call in calls if call[:2] == ['release', 'edit'])
        self.assertIn('--prerelease=true', edit)


if __name__ == '__main__':
    unittest.main()
