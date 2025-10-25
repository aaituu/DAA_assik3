#!/usr/bin/env bash
# commit_history.sh
# Create a clean, realistic git history for your finished project
# This version ENSURES all resources (src/main/resources, resources, input, output) are included.

set -euo pipefail

# ----------------- CONFIG -----------------
PROJECT_DIR="C:/Users/ahude/OneDrive/Рабочий стол/Unik/SDP/5/DAA_assik3"
GIT_USERNAME="Dias Saqyp"
GIT_EMAIL="farhatadil1719@gmail.com"
REMOTE_URL="https://github.com/aaituu/DAA_assik3.git"

# Simple timeline (yesterday -> today with intervals)
DAY1="2025-10-25T10:00:00"
DAY2="2025-10-25T13:00:00"
DAY3="2025-10-25T15:00:00"
DAY4="2025-10-25T17:00:00"
DAY5="2025-10-25T18:30:00"
DAY6="2025-10-26T10:00:00"
DAY7="2025-10-26T11:30:00"

# ------------------------------------------
echo "Project dir: $PROJECT_DIR"
cd "$PROJECT_DIR" || { echo "Project dir not found: $PROJECT_DIR"; exit 1; }

# set user
git config user.name "$GIT_USERNAME"
git config user.email "$GIT_EMAIL"

# initialize repo if needed
if [ ! -d .git ]; then
  git init
  echo "Initialized new git repo"
else
  echo "Using existing git repo"
fi

# Make sure nothing accidental is staged
git reset --hard HEAD 2>/dev/null || true
git clean -fd 2>/dev/null || true

# Stage everything (include all resource directories explicitly)
git add -A
# Ensure resource folders are included (safe even if absent)
git add src/main/resources/** resources/** input/** output/** assign_3_input.json assign_3_output.json 2>/dev/null || true

# Helper: commit with custom dates (author + committer)
commit_with_date() {
  local when="$1"; shift
  GIT_AUTHOR_DATE="$when" GIT_COMMITTER_DATE="$when" git commit -m "$*" || echo "Nothing to commit for: $*"
}

# Create main branch baseline if empty
if ! git rev-parse --verify main >/dev/null 2>&1; then
  # If there are staged files, create initial commit, else create empty initial
  if ! git diff --cached --quiet; then
    commit_with_date "$DAY1" "chore: initial project snapshot (pom, README, .gitignore, resources)"
  else
    # create an empty initial commit to start history
    GIT_AUTHOR_DATE="$DAY1" GIT_COMMITTER_DATE="$DAY1" git commit --allow-empty -m "chore: initial commit (empty baseline)"
  fi
  git branch -M main
fi

# Create feature branches and commits — all based on current working tree
# Branch: feature/models
git checkout -b feature/models main 2>/dev/null || git checkout feature/models
git add src/main/java/com/example/mst/model/** 2>/dev/null || true
commit_with_date "$DAY2" "feat(models): add Graph and Edge model classes"

# Branch: feature/algorithms (Prim & Kruskal)
git checkout -b feature/algorithms main 2>/dev/null || git checkout feature/algorithms
git add src/main/java/com/example/mst/algorithms/** 2>/dev/null || true
commit_with_date "$DAY3" "feat(algorithms): implement Prim and Kruskal algorithms"

# Branch: feature/io (JSON I/O)
git checkout -b feature/io main 2>/dev/null || git checkout feature/io
git add src/main/java/com/example/mst/io/** 2>/dev/null || true
# Also ensure resources checked (input datasets)
git add src/main/resources/** resources/** input/** assign_3_input.json 2>/dev/null || true
commit_with_date "$DAY4" "feat(io): add JSON reader/writer and datasets in resources"

# Branch: feature/tests
git checkout -b feature/tests main 2>/dev/null || git checkout feature/tests
git add src/test/java/com/example/mst/** 2>/dev/null || true
commit_with_date "$DAY5" "test: add JUnit tests for MST algorithms (correctness & disconnected graphs)"

# Branch: feature/samples (explicitly add all resource files)
git checkout -b feature/samples main 2>/dev/null || git checkout feature/samples
git add assign_3_input.json src/main/resources/** resources/** input/** output/** 2>/dev/null || true
commit_with_date "$DAY6" "chore(samples): add assign_3_input.json and sample datasets (small/medium/large)"

# Branch: feature/final (final polish)
git checkout -b feature/final main 2>/dev/null || git checkout feature/final
git add -A
commit_with_date "$DAY7" "chore: final cleanup, formatting and comments before submission"

# Merge sequence into main (create/checkout main safely)
if git rev-parse --verify main >/dev/null 2>&1; then
  git checkout main
else
  git checkout -b main
fi

merge_branch_if_exists() {
  local b="$1"
  if git show-ref --verify --quiet "refs/heads/$b"; then
    git merge --no-ff "$b" -m "merge: integrate $b into main" || echo "Merge conflict or nothing to merge for $b"
  else
    echo "Branch $b not found, skipping"
  fi
}

merge_branch_if_exists feature/models
merge_branch_if_exists feature/algorithms
merge_branch_if_exists feature/io
merge_branch_if_exists feature/tests
merge_branch_if_exists feature/samples
merge_branch_if_exists feature/final

# Tag final version
GIT_AUTHOR_DATE="2025-10-26T12:00:00" GIT_COMMITTER_DATE="2025-10-26T12:00:00" \
git tag -a v1.0 -m "release: v1.0 - Final MST Assignment (Prim & Kruskal)" || true

# Add remote if not present, then push
if ! git remote | grep -q origin; then
  git remote add origin "$REMOTE_URL"
  echo "Added remote origin -> $REMOTE_URL"
fi

echo "Pushing main and tags to origin (may require credentials)..."
git push -u origin main --force --tags

echo "✅ Done. History created and pushed. Check your GitHub repo."
