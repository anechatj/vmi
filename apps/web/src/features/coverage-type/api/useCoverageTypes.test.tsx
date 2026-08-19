import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { renderHook, waitFor } from '@testing-library/react'
import type { ReactNode } from 'react'
import { describe, expect, it, vi } from 'vitest'
import { useCoverageTypes } from './useCoverageTypes'

vi.mock('react-oidc-context', () => ({
  useAuth: () => ({ user: { access_token: 'fake-token' } }),
}))

function createWrapper() {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  })
  return ({ children }: { children: ReactNode }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  )
}

describe('useCoverageTypes', () => {
  it('fetches coverage types via MSW-mocked API', async () => {
    const { result } = renderHook(() => useCoverageTypes(), { wrapper: createWrapper() })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))

    expect(result.current.data).toHaveLength(2)
    expect(result.current.data?.[0]?.code).toBe('CMI')
  })
})
