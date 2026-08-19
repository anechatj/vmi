import { usePolicies } from '@/features/policy/api/usePolicies'
import { PolicyTable } from '@/features/policy/components/PolicyTable'

export function PolicyListPage() {
  const { data, isLoading, isError, error } = usePolicies()

  if (isLoading) {
    return <div className="p-8 text-slate-500">กำลังโหลดข้อมูล...</div>
  }

  if (isError) {
    return (
      <div className="p-8 text-red-600">
        โหลดข้อมูลไม่สำเร็จ: {error instanceof Error ? error.message : 'unknown error'}
        <p className="mt-1 text-sm text-slate-500">
          (คาดหวังผลนี้อยู่ — apps/policy-api ยังไม่ scaffold)
        </p>
      </div>
    )
  }

  return (
    <div className="mx-auto max-w-4xl px-6 py-10">
      <h1 className="mb-4 text-xl font-semibold text-slate-900">รายการกรมธรรม์</h1>
      <PolicyTable policies={data ?? []} />
    </div>
  )
}
