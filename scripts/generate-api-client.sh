#!/usr/bin/env bash
set -euo pipefail

# สร้าง TypeScript client จาก OpenAPI spec ของ policy-api
# ต้องมี apps/policy-api รันอยู่ก่อน (ดู docs/runbooks/local-setup.md)

API_URL="${POLICY_API_URL:-http://localhost:8081}/v3/api-docs"
OUTPUT_DIR="packages/api-client/generated"

echo "Fetching OpenAPI spec from ${API_URL}..."
mkdir -p "${OUTPUT_DIR}"

npx --yes @openapitools/openapi-generator-cli generate \
  -i "${API_URL}" \
  -g typescript-fetch \
  -o "${OUTPUT_DIR}"

echo "Generated TypeScript client at ${OUTPUT_DIR}"
