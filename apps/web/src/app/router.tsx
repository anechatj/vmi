import { createBrowserRouter } from 'react-router-dom'
import { HomePage } from '@/pages/HomePage'
import { CoverageTypeListPage } from '@/pages/CoverageTypeListPage'
import { CallbackPage } from '@/pages/CallbackPage'
import { ProtectedRoute } from '@/features/auth/ProtectedRoute'

export const router = createBrowserRouter([
  { path: '/', element: <HomePage /> },
  { path: '/callback', element: <CallbackPage /> },
  {
    path: '/coverage-types',
    element: (
      <ProtectedRoute>
        <CoverageTypeListPage />
      </ProtectedRoute>
    ),
  },
])
