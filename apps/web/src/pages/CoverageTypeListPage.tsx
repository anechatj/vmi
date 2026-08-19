import { useCoverageTypes } from '@/features/coverage-type/api/useCoverageTypes'
import { CoverageTypeTable } from '@/features/coverage-type/components/CoverageTypeTable'

export function CoverageTypeListPage() {
  const { data, isLoading, isError, error } = useCoverageTypes()

  if (isLoading) {
    return <div className="p-8 text-slate-500">กำลังโหลดข้อมูล...</div>
  }

  if (isError) {
    return (
      <div className="p-8 text-red-600">
        โหลดข้อมูลไม่สำเร็จ: {error instanceof Error ? error.message : 'unknown error'}
      </div>
    )
  }

  return (
    <div className="mx-auto max-w-4xl px-6 py-10">
      <h1 className="mb-4 text-xl font-semibold text-slate-900">ประเภทความคุ้มครอง</h1>
      <CoverageTypeTable coverageTypes={data ?? []} />
    </div>
  )
}
