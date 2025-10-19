// TypeScript interfaces matching Spring Boot DTOs
// This file provides type safety for all API calls

// ===== ENUMS =====
export enum UserRole {
    EMPLOYEE = 'EMPLOYEE',
    MANAGER = 'MANAGER',
    HR_ADMIN = 'HR_ADMIN',
    SYSTEM_ADMIN = 'SYSTEM_ADMIN'
}

// ===== AUTH TYPES =====
export interface LoginRequest {
    username: string;
    password: string;
}

export interface RegisterRequest {
    username: string;
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    role: UserRole;
}

export interface AuthResponse {
    token: string;
    type: string;
    id: number;
    username: string;
    email: string;
    role: string;
}

// ===== USER TYPES =====
export interface UserCreateRequest {
    username: string;
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    role: UserRole;
    departmentId?: number;
    managerId?: number;
}

export interface UserUpdateRequest {
    username?: string;
    email?: string;
    firstName?: string;
    lastName?: string;
    role?: UserRole;
    active?: boolean;
    departmentId?: number;
    managerId?: number;
}

export interface UserPerformanceUpdateRequest {
    currentPerformanceRating?: number; // 1-5 scale
    lastReviewNotes?: string;
    lastReviewDate?: string; // ISO date string
    currentGoals?: string;
}

export interface UserResponse {
    id: number;
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    fullName: string;
    role: string;
    active: boolean;
    createdAt: string;
    updatedAt: string;

    // Performance fields
    currentPerformanceRating?: number;
    currentPerformanceRatingText?: string;
    lastReviewNotes?: string;
    lastReviewDate?: string;
    currentGoals?: string;
    hasPerformanceData: boolean;

    // Relationships
    department?: DepartmentSummary;
    manager?: UserSummary;
    directReportsCount: number;
}

export interface UserSummary {
    id: number;
    username: string;
    fullName: string;
    role: string;
}

// ===== ORGANIZATION TYPES =====
export interface OrganizationCreateRequest {
    name: string;
    description?: string;
}

export interface OrganizationUpdateRequest {
    name?: string;
    description?: string;
    active?: boolean;
}

export interface OrganizationResponse {
    id: number;
    name: string;
    description?: string;
    active: boolean;
    createdAt: string;
    updatedAt: string;
    departmentCount: number;
    departments?: DepartmentSummary[];
}

// ===== DEPARTMENT TYPES =====
export interface DepartmentCreateRequest {
    name: string;
    description?: string;
    organizationId: number;
    managerId?: number;
}

export interface DepartmentUpdateRequest {
    name?: string;
    description?: string;
    active?: boolean;
    managerId?: number;
}

export interface DepartmentResponse {
    id: number;
    name: string;
    description?: string;
    active: boolean;
    createdAt: string;
    updatedAt: string;
    organization: OrganizationSummary;
    manager?: UserSummary;
    userCount: number;
    users?: UserSummary[];
}

export interface DepartmentSummary {
    id: number;
    name: string;
    organizationName: string;
}

export interface OrganizationSummary {
    id: number;
    name: string;
}

// ===== API RESPONSE TYPES =====
export interface ApiError {
    error: string;
    message: string;
    status: number;
    timestamp: string;
    validationErrors?: Record<string, string>;
}

export interface PaginatedResponse<T> {
    content: T[];
    pageable: {
        pageNumber: number;
        pageSize: number;
        sort: {
            sorted: boolean;
            unsorted: boolean;
            empty: boolean;
        };
        offset: number;
        paged: boolean;
        unpaged: boolean;
    };
    totalElements: number;
    totalPages: number;
    last: boolean;
    first: boolean;
    numberOfElements: number;
    size: number;
    number: number;
    sort: {
        sorted: boolean;
        unsorted: boolean;
        empty: boolean;
    };
    empty: boolean;
}

// ===== UTILITY TYPES =====
export interface PaginationParams {
    page?: number;
    size?: number;
    sortBy?: string;
    sortDir?: 'asc' | 'desc';
}

// Performance rating helpers
export const PERFORMANCE_RATINGS = {
    1: 'Needs Improvement',
    2: 'Below Expectations',
    3: 'Meets Expectations',
    4: 'Exceeds Expectations',
    5: 'Outstanding'
} as const;

export type PerformanceRating = keyof typeof PERFORMANCE_RATINGS;
