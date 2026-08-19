import { useRef } from 'react'
import { useVirtualizer } from '@tanstack/react-virtual'
import type { Policy } from '@/features/policy/types'

// ใช้ list virtualization เพราะรายการกรมธรรม์อาจมีหลักพัน/หมื่นแถว
// render เฉพาะแถวที่อยู่ใน viewport เท่านั้น
export function PolicyTable({ policies }: { policies: Policy[] }) {
  const parentRef = useRef<HTMLDivElement>(null)

  const virtualizer = useVirtualizer({
    count: policies.length,
    getScrollElement: () => parentRef.current,
    estimateSize: () => 44,
  })

  return (
    <div ref={parentRef} className="h-[480px] overflow-auto rounded-md border border-slate-200">
      <div style={{ height: virtualizer.getTotalSize(), position: 'relative' }}>
        {virtualizer.getVirtualItems().map((row) => {
          const policy = policies[row.index]
          if (!policy) return null
          return (
            <div
              key={policy.id}
              className="absolute left-0 top-0 flex w-full items-center gap-4 border-b border-slate-100 px-4 text-sm"
              style={{ height: row.size, transform: `translateY(${row.start}px)` }}
            >
              <span className="w-32 font-medium text-slate-900">{policy.policyNumber}</span>
              <span className="flex-1 text-slate-600">{policy.insuredName}</span>
              <span className="text-slate-500">{policy.status}</span>
            </div>
          )
        })}
      </div>
    </div>
  )
}
