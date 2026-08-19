export interface Policy {
  id: string
  policyNumber: string
  insuredName: string
  premium: number
  status: 'draft' | 'active' | 'expired'
}
