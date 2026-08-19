# apps/web

React 19 + TypeScript frontend สำหรับ VMI

## Stack

| ส่วน | ที่ใช้ |
|---|---|
| Build tool | Vite |
| Routing | React Router v7 |
| Server state | TanStack Query |
| List virtualization | TanStack Virtual |
| Form + validation | React Hook Form + Zod |
| Auth (OIDC) | oidc-client-ts + react-oidc-context (ต่อกับ Keycloak realm `vmi`) |
| Styling | Tailwind CSS v4 |
| Unit/component test | Vitest + React Testing Library + MSW |
| E2E test | Playwright |
| Lint | Oxlint (`react`, `typescript`, `jsx-a11y`, `react-perf` plugins) |
| Format | Prettier |

## โครงสร้าง

```
src/
├─ app/            # entry, router, provider composition
├─ pages/          # route-level components (thin, compose features)
├─ features/       # vertical slice ต่อ domain (policy, auth, ...)
├─ components/ui/  # shared presentational component
├─ hooks/          # generic reusable hooks
├─ lib/            # api client instance, query client, utils
└─ test/           # vitest setup + MSW mock handlers
e2e/                # Playwright tests
```

## คำสั่งที่ใช้บ่อย

```bash
npm run dev          # dev server ที่ localhost:5173
npm run build         # typecheck + production build
npm run test          # unit/component test (Vitest)
npm run test:e2e     # E2E test (Playwright — ต้องมี dev server หรือปล่อยให้ config auto start)
npm run lint          # Oxlint
npm run format        # Prettier write
```

## Auth

ต่อกับ Keycloak realm `vmi`, client `vmi-web` (public client, Authorization Code Flow) — ดู [docs/runbooks/keycloak-setup.md](../../docs/runbooks/keycloak-setup.md) สำหรับ test user

⚠️ TODO ก่อน deploy จริง: เปิด PKCE (`pkce.code.challenge.method: S256`) บน client `vmi-web` ใน Keycloak — ตอนนี้ยังไม่ได้ enforce แม้ library ฝั่ง client จะส่ง code_challenge มาให้อยู่แล้วก็ตาม

## API

`usePolicies()` (`src/features/policy/api/`) ยังชี้ไป `/api/policies` แบบ placeholder fetch — จะเปลี่ยนเป็น typed client จาก `packages/api-client` เมื่อ `apps/policy-api` scaffold และ generate client เสร็จ (`npm run generate-client` ที่ root)
