import { createBrowserRouter } from 'react-router-dom'
import { AppLayout } from '@/components/layout/AppLayout'
import { HomePage } from '@/pages/HomePage'
import { CoverageTypeListPage } from '@/pages/CoverageTypeListPage'
import { CallbackPage } from '@/pages/CallbackPage'
import { ProtectedRoute } from '@/features/auth/ProtectedRoute'

export const router = createBrowserRouter([
  {
    element: <AppLayout />,
    children: [
      {
        path: '/',
        element: (
          <ProtectedRoute>
            <HomePage />
          </ProtectedRoute>
        ),
      },
      { path: '/callback', element: <CallbackPage /> },
      {
        path: '/coverage-types',
        element: (
          <ProtectedRoute>
            <CoverageTypeListPage />
          </ProtectedRoute>
        ),
      },
    ],
  },
])
