import { http, HttpResponse } from 'msw'
import type { Policy } from '@/features/policy/types'

const mockPolicies: Policy[] = [
  { id: '1', policyNumber: 'VMI-0001', insuredName: 'สมชาย ใจดี', premium: 12000, status: 'active' },
  { id: '2', policyNumber: 'VMI-0002', insuredName: 'สมหญิง มีสุข', premium: 9500, status: 'draft' },
]

export const handlers = [
  http.get('/api/policies', () => HttpResponse.json(mockPolicies)),
]
