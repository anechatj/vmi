export interface CoverageType {
  id: string
  code: string
  name: string
  description: string | null
  sortOrder: number
  active: boolean
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}
