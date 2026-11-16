#!/usr/bin/env bash
# Publish only verified artifacts; preserve published releases and resume drafts.
set -euo pipefail
: "${RELEASE_TAG:?RELEASE_TAG is required}"
: "${RELEASE_VERSION:?RELEASE_VERSION is required}"
: "${GITHUB_SHA:?GITHUB_SHA is required}"
: "${GH_REPO:?GH_REPO is required}"

cd "${1:?Pass the prepared artifact directory}"
jar="roguelike-java-${RELEASE_VERSION}.jar"
test -s "$jar"
test -s "$jar.sha256"
test -s release-notes.md
remote_commit=$(gh api "repos/$GH_REPO/commits/$RELEASE_TAG" --jq .sha)
if [[ "$remote_commit" != "$GITHUB_SHA" ]]; then
  echo "Tag no longer points to the verified commit" >&2
  exit 1
fi
prerelease=false
if [[ "$RELEASE_VERSION" == *-* ]]; then prerelease=true; fi
if existing=$(gh release view "$RELEASE_TAG" --json isDraft --jq .isDraft 2>/dev/null); then
  if [[ "$existing" == false ]]; then
    echo "Release $RELEASE_TAG is already published; preserving it."
    exit 0
  fi
else
  gh release create "$RELEASE_TAG" --verify-tag --draft \
    --title "Roguelike Java $RELEASE_TAG" --notes-file release-notes.md --prerelease="$prerelease"
fi
gh release upload "$RELEASE_TAG" "$jar" "$jar.sha256" --clobber
gh release edit "$RELEASE_TAG" --notes-file release-notes.md --prerelease="$prerelease" --draft=false
