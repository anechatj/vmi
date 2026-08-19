import { useRef } from 'react'
import { useVirtualizer } from '@tanstack/react-virtual'
import type { CoverageType } from '@/features/coverage-type/types'

export function CoverageTypeTable({ coverageTypes }: { coverageTypes: CoverageType[] }) {
  const parentRef = useRef<HTMLDivElement>(null)

  const virtualizer = useVirtualizer({
    count: coverageTypes.length,
    getScrollElement: () => parentRef.current,
    estimateSize: () => 44,
  })

  return (
    <div ref={parentRef} className="h-[480px] overflow-auto rounded-md border border-slate-200">
      <div style={{ height: virtualizer.getTotalSize(), position: 'relative' }}>
        {virtualizer.getVirtualItems().map((row) => {
          const coverageType = coverageTypes[row.index]
          if (!coverageType) return null
          return (
            <div
              key={coverageType.id}
              className="absolute left-0 top-0 flex w-full items-center gap-4 border-b border-slate-100 px-4 text-sm"
              style={{ height: row.size, transform: `translateY(${row.start}px)` }}
            >
              <span className="w-24 font-medium text-slate-900">{coverageType.code}</span>
              <span className="flex-1 text-slate-600">{coverageType.name}</span>
              <span className="text-slate-500">{coverageType.active ? 'active' : 'inactive'}</span>
            </div>
          )
        })}
      </div>
    </div>
  )
}
