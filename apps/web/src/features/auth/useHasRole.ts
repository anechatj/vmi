import { useAuth } from 'react-oidc-context'

interface RealmAccessClaim {
  realm_access?: { roles?: string[] }
}

// UI-level check เท่านั้น สำหรับซ่อน/โชว์ปุ่ม — apps/policy-api ต้อง enforce สิทธิ์จริงเสมอ ห้ามพึ่งฝั่ง client อย่างเดียว
export function useHasRole(role: string): boolean {
  const auth = useAuth()
  const claims = auth.user?.profile as RealmAccessClaim | undefined
  return claims?.realm_access?.roles?.includes(role) ?? false
}
