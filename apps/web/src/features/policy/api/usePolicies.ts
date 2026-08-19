import { useQuery } from '@tanstack/react-query'
import type { Policy } from '@/features/policy/types'

// TODO: แทนที่ fetch ตรงนี้ด้วย typed client จาก packages/api-client
// เมื่อ apps/policy-api scaffold เสร็จและ generate client แล้ว (ดู scripts/generate-api-client.sh)
async function fetchPolicies(): Promise<Policy[]> {
  const res = await fetch('/api/policies')
  if (!res.ok) {
    throw new Error(`Failed to fetch policies: ${res.status}`)
  }
  return res.json() as Promise<Policy[]>
}

export function usePolicies() {
  return useQuery({
    queryKey: ['policies'],
    queryFn: fetchPolicies,
  })
}
