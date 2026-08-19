import type { ButtonHTMLAttributes } from 'react'
import { cn } from '@/lib/utils'

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary'
}

export function Button({ variant = 'primary', className, ...props }: ButtonProps) {
  return (
    <button
      className={cn(
        'rounded-md px-4 py-2 text-sm font-medium transition-colors focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2',
        variant === 'primary' &&
          'bg-slate-900 text-white hover:bg-slate-700 focus-visible:outline-slate-900',
        variant === 'secondary' &&
          'bg-slate-100 text-slate-900 hover:bg-slate-200 focus-visible:outline-slate-400',
        className,
      )}
      {...props}
    />
  )
}
