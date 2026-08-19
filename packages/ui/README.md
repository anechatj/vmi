# packages/ui (optional)

Shared React components ระหว่าง apps ในอนาคต

## Status
ยังไม่สร้าง — ตอนนี้มี `apps/web` เป็น app เดียว ยังไม่มีความจำเป็นต้อง extract shared component

**กฎ**: แตก component มาไว้ที่นี่เมื่อ pattern เดียวกันถูกใช้ซ้ำอย่างน้อย 2 จุดใน `apps/web` เท่านั้น (หลีกเลี่ยง premature abstraction)
