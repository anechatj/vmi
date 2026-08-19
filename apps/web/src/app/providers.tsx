import type { ReactNode } from 'react'
import { QueryClientProvider } from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import { AuthProvider } from 'react-oidc-context'
import { queryClient } from '@/lib/queryClient'

// TODO: เปิด PKCE (pkce.code.challenge.method: S256) บน client `vmi-web` ใน Keycloak
// ก่อน deploy จริง — ดู docs/runbooks/keycloak-setup.md
const oidcConfig = {
  authority: 'http://localhost:8080/realms/vmi',
  client_id: 'vmi-web',
  redirect_uri: `${window.location.origin}/callback`,
  scope: 'openid profile email',
  onSigninCallback: () => {
    window.history.replaceState({}, document.title, window.location.pathname)
  },
}

export function AppProviders({ children }: { children: ReactNode }) {
  return (
    <AuthProvider {...oidcConfig}>
      <QueryClientProvider client={queryClient}>
        {children}
        <ReactQueryDevtools initialIsOpen={false} />
      </QueryClientProvider>
    </AuthProvider>
  )
}
