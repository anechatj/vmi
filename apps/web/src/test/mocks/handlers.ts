import { http, HttpResponse } from 'msw'
import type { CoverageType, Page } from '@/features/coverage-type/types'

const mockCoverageTypes: CoverageType[] = [
  { id: '1', code: 'CMI', name: 'พ.ร.บ.', description: null, sortOrder: 1, active: true },
  { id: '2', code: 'VOL1', name: 'ชั้น 1', description: null, sortOrder: 2, active: true },
]

const mockCoverageTypePage: Page<CoverageType> = {
  content: mockCoverageTypes,
  totalElements: mockCoverageTypes.length,
  totalPages: 1,
  number: 0,
  size: 100,
}

export const handlers = [
  http.get('http://localhost:8081/api/v1/master/coverage-types', () =>
    HttpResponse.json(mockCoverageTypePage),
  ),
]
