import { Outlet } from 'react-router-dom'
import { useAuth } from 'react-oidc-context'
import { Header } from '@/components/layout/Header'
import { Sidebar } from '@/components/layout/Sidebar'
import { useIdleTimeout } from '@/features/auth/useIdleTimeout'

export function AppLayout() {
  const auth = useAuth()
  useIdleTimeout()

  return (
    <div className="flex min-h-screen flex-col">
      <Header />
      <div className="flex flex-1">
        {auth.isAuthenticated && <Sidebar />}
        <main className="flex-1">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
