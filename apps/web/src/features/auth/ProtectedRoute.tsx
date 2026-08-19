import { useEffect, type ReactNode } from 'react'
import { useAuth } from 'react-oidc-context'

export function ProtectedRoute({ children }: { children: ReactNode }) {
  const auth = useAuth()
  const { isLoading, isAuthenticated, signinRedirect } = auth

  // ห้ามเรียก signinRedirect() ตรงๆ ใน render body — มันไป setState ของ AuthProvider
  // ระหว่างที่ ProtectedRoute กำลัง render อยู่ (React จะ warn "Cannot update a component
  // while rendering a different component") ต้องย้ายเข้า useEffect ให้รันหลัง render เสร็จ
  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      void signinRedirect()
    }
  }, [isLoading, isAuthenticated, signinRedirect])

  if (isLoading || !isAuthenticated) {
    return <div className="p-8 text-center text-slate-500">กำลังตรวจสอบสิทธิ์...</div>
  }

  return <>{children}</>
}
