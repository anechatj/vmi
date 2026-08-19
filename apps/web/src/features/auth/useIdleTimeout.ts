import { useEffect, useRef } from 'react'
import { useAuth } from 'react-oidc-context'

// ตั้งไว้ 1 นาทีเพื่อทดสอบจับเวลาได้ง่าย — ปรับเป็นค่าจริง (เช่น 15-30 นาที) ทีหลัง
const IDLE_TIMEOUT_MS = 60_000

const ACTIVITY_EVENTS = ['mousemove', 'mousedown', 'keydown', 'scroll', 'touchstart'] as const

// เตะออกไปหน้า login เมื่อไม่มีกิจกรรม (เมาส์/คีย์บอร์ด) เกิน IDLE_TIMEOUT_MS
// ทำงานเฉพาะตอน authenticated เท่านั้น — token refresh (automaticSilentRenew) ต่ออายุ
// session ให้เองอยู่แล้วตราบใดที่ tab เปิดอยู่ ไม่เกี่ยวกับกิจกรรมจริงของ user เลย
export function useIdleTimeout() {
  const auth = useAuth()
  const timeoutRef = useRef<ReturnType<typeof setTimeout> | undefined>(undefined)

  useEffect(() => {
    if (!auth.isAuthenticated) return

    function resetTimer() {
      if (timeoutRef.current) clearTimeout(timeoutRef.current)
      timeoutRef.current = setTimeout(() => {
        void auth.signoutRedirect()
      }, IDLE_TIMEOUT_MS)
    }

    resetTimer()
    for (const event of ACTIVITY_EVENTS) {
      window.addEventListener(event, resetTimer)
    }

    return () => {
      if (timeoutRef.current) clearTimeout(timeoutRef.current)
      for (const event of ACTIVITY_EVENTS) {
        window.removeEventListener(event, resetTimer)
      }
    }
  }, [auth.isAuthenticated, auth.signoutRedirect])
}
