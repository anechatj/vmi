import { Navigate } from 'react-router-dom'
import { useAuth } from 'react-oidc-context'

// หน้าที่ Keycloak redirect กลับมาหลัง login สำเร็จ (ดู providers.tsx: redirect_uri)
export function CallbackPage() {
  const auth = useAuth()

  if (auth.isLoading) {
    return <div className="p-8 text-center text-slate-500">กำลังเข้าสู่ระบบ...</div>
  }

  return <Navigate to="/coverage-types" replace />
}
