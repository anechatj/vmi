import { createBrowserRouter } from 'react-router-dom'
import { HomePage } from '@/pages/HomePage'
import { PolicyListPage } from '@/pages/PolicyListPage'
import { CallbackPage } from '@/pages/CallbackPage'
import { ProtectedRoute } from '@/features/auth/ProtectedRoute'

export const router = createBrowserRouter([
  { path: '/', element: <HomePage /> },
  { path: '/callback', element: <CallbackPage /> },
  {
    path: '/policies',
    element: (
      <ProtectedRoute>
        <PolicyListPage />
      </ProtectedRoute>
    ),
  },
])
