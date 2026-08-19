import { useQuery } from '@tanstack/react-query'
import { useAuth } from 'react-oidc-context'
import type { CoverageType, Page } from '@/features/coverage-type/types'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8081'

async function fetchCoverageTypes(accessToken: string): Promise<CoverageType[]> {
  const res = await fetch(`${API_BASE_URL}/api/v1/master/coverage-types?size=100`, {
    headers: { Authorization: `Bearer ${accessToken}` },
  })
  if (!res.ok) {
    throw new Error(`Failed to fetch coverage types: ${res.status}`)
  }
  const page = (await res.json()) as Page<CoverageType>
  return page.content
}

export function useCoverageTypes() {
  const auth = useAuth()
  const accessToken = auth.user?.access_token

  return useQuery({
    queryKey: ['coverage-types'],
    queryFn: () => fetchCoverageTypes(accessToken as string),
    enabled: Boolean(accessToken),
  })
}
