#!/usr/bin/env bash

set -euo pipefail

script_dir="$(cd -- "$(dirname -- "$0")" && pwd)"
project_root="$(cd "$script_dir/.." && pwd)"

cd "$project_root"
mariadb -u root -p < database/setup.sql
