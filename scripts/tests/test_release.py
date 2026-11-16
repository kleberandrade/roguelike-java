"""Release artifact behavior, tested entirely with local fixtures."""
import hashlib
from pathlib import Path
import sys
import tempfile
import unittest
from unittest.mock import patch
import contextlib
import io
import subprocess
import zipfile

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import release
from release import prepare, release_version, history_url


class ReleaseTests(unittest.TestCase):
    def test_accepts_stable_and_prerelease_tags(self):
        self.assertEqual('2.3.4', release_version('v2.3.4'))
        self.assertEqual('2.3.4-rc.1', release_version('v2.3.4-rc.1'))
        self.assertEqual('2.3.4-0', release_version('v2.3.4-0'))

    def test_rejects_invalid_versions_and_path_traversal(self):
        for tag in ['1.2.3', 'vnext', 'v1.2', 'v01.2.3', 'v1.2.3/../../secret',
                    'v1.2.3;echo bad', 'v1.2.3-01', 'v1.2.3+metadata']:
            with self.subTest(tag=tag), self.assertRaises(ValueError):
                release_version(tag)

    def fixture(self, root, version='2.3.4', license_text=b'MIT fixture', main='br.unisal.aula.Main'):
        (root / 'target').mkdir()
        (root / 'docs').mkdir()
        (root / 'LICENSE').write_bytes(b'MIT fixture')
        (root / 'docs/release-notes-template.md').write_text(
            '# {tag}\nJava 21\n{jar}\n{sha256}\n{repository}\n{commit}\n{changes_url}\n')
        jar = root / 'target/roguelike-java-2.3.4.jar'
        with zipfile.ZipFile(jar, 'w') as archive:
            archive.writestr('META-INF/MANIFEST.MF',
                             f'Manifest-Version: 1.0\r\nMain-Class: {main}\r\n'
                             f'Implementation-Version: {version}\r\n\r\n')
            archive.writestr('META-INF/LICENSE', license_text)
        return jar

    def test_prepares_exact_jar_checksum_and_english_notes(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            jar = self.fixture(root)
            output = root / 'nested/release'
            prepare(root, 'v2.3.4', 'a' * 40, 'owner/repo', output, 'https://example.com/changes')
            self.assertEqual(jar.read_bytes(), (output / jar.name).read_bytes())
            digest = hashlib.sha256(jar.read_bytes()).hexdigest()
            self.assertEqual(f'{digest}  {jar.name}\n', (output / (jar.name + '.sha256')).read_text())
            notes = (output / 'release-notes.md').read_text()
            for expected in ['# v2.3.4', 'Java 21', digest, jar.name, 'https://example.com/changes']:
                self.assertIn(expected, notes)
            self.assertEqual(3, len(list(output.iterdir())))
            with self.assertRaises(ValueError):
                prepare(root, 'v2.3.4', 'a' * 40, 'owner/repo', output)

    def test_fails_before_output_on_wrong_manifest_or_license(self):
        for version, license_text, main in [('1.0.0', b'MIT fixture', 'br.unisal.aula.Main'),
                                            ('2.3.4', b'wrong license', 'br.unisal.aula.Main'),
                                            ('2.3.4', b'MIT fixture', 'other.Main')]:
            with self.subTest(version=version, license=license_text, main=main), tempfile.TemporaryDirectory() as directory:
                root = Path(directory)
                self.fixture(root, version, license_text, main)
                output = root / 'release'
                with self.assertRaises(ValueError):
                    prepare(root, 'v2.3.4', 'a' * 40, 'owner/repo', output)
                self.assertFalse(output.exists())

    def test_missing_jar_and_invalid_commit_fail(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            with self.assertRaises(ValueError):
                prepare(root, 'v2.3.4', 'not-a-sha', 'owner/repo', root / 'release')
            with self.assertRaises(FileNotFoundError):
                prepare(root, 'v2.3.4', 'a' * 40, 'owner/repo', root / 'release')

    def test_change_links_cover_previous_tag_and_first_release(self):
        with patch('release.subprocess.run', return_value=subprocess.CompletedProcess([], 0, 'v1.0.0\n')):
            self.assertEqual('https://github.com/owner/repo/compare/v1.0.0...v2.3.4',
                             history_url(Path('.'), 'v2.3.4', 'a' * 40, 'owner/repo'))
        with patch('release.subprocess.run', return_value=subprocess.CompletedProcess([], 1, '')):
            self.assertEqual('https://github.com/owner/repo/commits/v2.3.4',
                             history_url(Path('.'), 'v2.3.4', 'a' * 40, 'owner/repo'))

    def test_cli_prints_version_and_reports_invalid_tags(self):
        output = io.StringIO()
        with patch('sys.argv', ['release.py', 'version', 'v2.3.4']), contextlib.redirect_stdout(output):
            release.main()
        self.assertEqual('2.3.4\n', output.getvalue())
        with patch('sys.argv', ['release.py', 'version', 'invalid']), contextlib.redirect_stderr(io.StringIO()):
            with self.assertRaises(SystemExit) as result:
                release.main()
        self.assertEqual(2, result.exception.code)

    def test_cli_prepares_artifacts_in_the_default_output_directory(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            self.fixture(root)
            args = ['release.py', 'prepare', '--tag', 'v2.3.4', '--commit', 'a' * 40, '--repository', 'owner/repo']
            with patch('release.ROOT', root), patch('sys.argv', args):
                release.main()
            self.assertTrue((root / 'target/release/roguelike-java-2.3.4.jar.sha256').exists())

    def test_invalid_repository_is_rejected_before_output(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            with self.assertRaises(ValueError):
                prepare(root, 'v2.3.4', 'a' * 40, '../owner/repo', root / 'release')

    def test_cli_requires_command_and_all_release_metadata(self):
        incomplete = [[], ['prepare', '--commit', 'a' * 40, '--repository', 'owner/repo'],
                      ['prepare', '--tag', 'v2.3.4', '--repository', 'owner/repo'],
                      ['prepare', '--tag', 'v2.3.4', '--commit', 'a' * 40]]
        for arguments in incomplete:
            with self.subTest(arguments=arguments), patch('sys.argv', ['release.py'] + arguments), contextlib.redirect_stderr(io.StringIO()):
                with self.assertRaises(SystemExit) as result:
                    release.main()
                self.assertEqual(2, result.exception.code)


if __name__ == '__main__':
    unittest.main()
