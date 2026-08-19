import { Link } from 'react-router-dom'
import { useAuth } from 'react-oidc-context'
import { Button } from '@/components/ui/Button'

export function Header() {
  const auth = useAuth()

  const displayName =
    (auth.user?.profile?.name as string | undefined) ??
    (auth.user?.profile?.preferred_username as string | undefined)

  return (
    <header className="flex h-14 items-center justify-between border-b border-slate-200 bg-white px-6">
      <Link to="/" className="font-semibold text-slate-900">
        VMI
      </Link>

      <div className="flex items-center gap-3">
        {auth.isLoading ? (
          <span className="text-sm text-slate-400">กำลังตรวจสอบสิทธิ์...</span>
        ) : auth.isAuthenticated ? (
          <>
            <span className="text-sm text-slate-600">{displayName}</span>
            <Button variant="secondary" onClick={() => void auth.signoutRedirect()}>
              ออกจากระบบ
            </Button>
          </>
        ) : (
          <Button onClick={() => void auth.signinRedirect()}>เข้าสู่ระบบ</Button>
        )}
      </div>
    </header>
  )
}
