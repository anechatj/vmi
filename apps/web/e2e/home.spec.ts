import { test, expect } from '@playwright/test'

test('home page shows the VMI heading and links to the policy list', async ({ page }) => {
  await page.goto('/')

  await expect(page.getByRole('heading', { name: 'VMI' })).toBeVisible()

  await page.getByRole('link', { name: 'ไปหน้ารายการกรมธรรม์' }).click()
  await expect(page).toHaveURL(/\/policies|\/realms\/vmi/)
})
