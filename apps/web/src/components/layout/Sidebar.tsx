import { NavLink } from 'react-router-dom'
import { cn } from '@/lib/utils'

const navItems = [
  { to: '/', label: 'หน้าแรก' },
  { to: '/coverage-types', label: 'ประเภทความคุ้มครอง' },
]

export function Sidebar() {
  return (
    <nav className="w-56 shrink-0 border-r border-slate-200 bg-slate-50 p-4">
      <ul className="space-y-1">
        {navItems.map((item) => (
          <li key={item.to}>
            <NavLink
              to={item.to}
              end={item.to === '/'}
              className={({ isActive }) =>
                cn(
                  'block rounded-md px-3 py-2 text-sm font-medium text-slate-600 hover:bg-slate-200',
                  isActive && 'bg-slate-900 text-white hover:bg-slate-900',
                )
              }
            >
              {item.label}
            </NavLink>
          </li>
        ))}
      </ul>
    </nav>
  )
}
