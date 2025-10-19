import { UserRole } from '../types/api';

// Authentication related constants
export const AUTH_CONFIG = {
    TOKEN_KEY: 'auth_token',
    USER_KEY: 'current_user',
    REMEMBER_ME_KEY: 'remember_me',
    SESSION_TIMEOUT: 24 * 60 * 60 * 1000, // 24 hours in milliseconds
} as const;

// User role display names and descriptions
export const ROLE_DISPLAY = {
    [UserRole.EMPLOYEE]: {
        name: 'Employee',
        description: 'Can view own profile and performance data',
        color: 'bg-blue-100 text-blue-800'
    },
    [UserRole.MANAGER]: {
        name: 'Manager',
        description: 'Can manage direct reports and create reviews',
        color: 'bg-green-100 text-green-800'
    },
    [UserRole.HR_ADMIN]: {
        name: 'HR Admin',
        description: 'Can view all users and manage departments',
        color: 'bg-purple-100 text-purple-800'
    },
    [UserRole.SYSTEM_ADMIN]: {
        name: 'System Admin',
        description: 'Full system access and user management',
        color: 'bg-red-100 text-red-800'
    }
} as const;

// Form validation rules
export const VALIDATION_RULES = {
    username: {
        minLength: 3,
        maxLength: 50,
        pattern: /^[a-zA-Z0-9_-]+$/,
        message: 'Username must be 3-50 characters and contain only letters, numbers, underscore, or dash'
    },
    password: {
        minLength: 6,
        maxLength: 100,
        message: 'Password must be at least 6 characters long'
    },
    email: {
        pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
        message: 'Please enter a valid email address'
    },
    name: {
        minLength: 2,
        maxLength: 100,
        pattern: /^[a-zA-Z\s'-]+$/,
        message: 'Name must be 2-100 characters and contain only letters, spaces, apostrophes, or hyphens'
    }
} as const;

// Available roles for registration (can be filtered based on permissions)
export const AVAILABLE_ROLES = [
    { value: UserRole.EMPLOYEE, label: ROLE_DISPLAY[UserRole.EMPLOYEE].name },
    { value: UserRole.MANAGER, label: ROLE_DISPLAY[UserRole.MANAGER].name },
    { value: UserRole.HR_ADMIN, label: ROLE_DISPLAY[UserRole.HR_ADMIN].name },
    { value: UserRole.SYSTEM_ADMIN, label: ROLE_DISPLAY[UserRole.SYSTEM_ADMIN].name }
] as const;
