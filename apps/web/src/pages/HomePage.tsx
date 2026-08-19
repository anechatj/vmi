import { Link } from 'react-router-dom'
import { Button } from '@/components/ui/Button'

export function HomePage() {
  return (
    <div className="mx-auto max-w-2xl px-6 py-16 text-center">
      <h1 className="text-2xl font-semibold text-slate-900">VMI</h1>
      <p className="mt-2 text-slate-600">ระบบบันทึกกรมธรรม์ภาคสมัครใจ</p>
      <Link to="/coverage-types" className="mt-6 inline-block">
        <Button>ไปหน้ารายการประเภทความคุ้มครอง</Button>
      </Link>
    </div>
  )
}
