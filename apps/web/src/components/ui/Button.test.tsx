import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it, vi } from 'vitest'
import { Button } from '@/components/ui/Button'

describe('Button', () => {
  it('renders children and responds to click', async () => {
    const onClick = vi.fn()
    render(<Button onClick={onClick}>บันทึก</Button>)

    const button = screen.getByRole('button', { name: 'บันทึก' })
    await userEvent.click(button)

    expect(onClick).toHaveBeenCalledOnce()
  })

  it('applies secondary variant styles', () => {
    render(<Button variant="secondary">ยกเลิก</Button>)
    expect(screen.getByRole('button', { name: 'ยกเลิก' })).toHaveClass('bg-slate-100')
  })
})
