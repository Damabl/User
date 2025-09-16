-- Add role column to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS role VARCHAR(50) DEFAULT 'USER';

-- Update existing users with roles
UPDATE users SET role = 'ADMIN' WHERE email = 'john.doe@example.com';
UPDATE users SET role = 'MANAGER' WHERE email = 'jane.smith@example.com';
UPDATE users SET role = 'USER' WHERE email = 'bob.wilson@example.com';

-- Create index on role for faster queries
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- Add comment for role column
COMMENT ON COLUMN users.role IS 'User role: ADMIN, MANAGER, USER';
