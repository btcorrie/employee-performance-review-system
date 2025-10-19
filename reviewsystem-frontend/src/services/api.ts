import axios, { AxiosInstance, AxiosResponse } from 'axios';
import {
    AuthResponse,
    LoginRequest,
    RegisterRequest,
    UserResponse,
    UserCreateRequest,
    UserUpdateRequest,
    UserPerformanceUpdateRequest,
    OrganizationResponse,
    OrganizationCreateRequest,
    OrganizationUpdateRequest,
    DepartmentResponse,
    DepartmentCreateRequest,
    DepartmentUpdateRequest,
    PaginatedResponse,
    PaginationParams,
    ApiError
} from '../types/api';

// Base API configuration
const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance with default configuration
const apiClient: AxiosInstance = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: 10000, // 10 second timeout
});

// ===== TOKEN MANAGEMENT =====
const TOKEN_KEY = 'auth_token';

export const tokenManager = {
    getToken: (): string | null => {
        return localStorage.getItem(TOKEN_KEY);
    },

    setToken: (token: string): void => {
        localStorage.setItem(TOKEN_KEY, token);
    },

    removeToken: (): void => {
        localStorage.removeItem(TOKEN_KEY);
    },

    isTokenValid: (): boolean => {
        const token = tokenManager.getToken();
        if (!token) return false;

        try {
            // Basic JWT expiration check (decode payload)
            const payload = JSON.parse(atob(token.split('.')[1]));
            const currentTime = Date.now() / 1000;
            return payload.exp > currentTime;
        } catch {
            return false;
        }
    }
};

// ===== REQUEST INTERCEPTOR =====
// Automatically add JWT token to requests
apiClient.interceptors.request.use(
    (config) => {
        const token = tokenManager.getToken();
        if (token && tokenManager.isTokenValid()) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// ===== RESPONSE INTERCEPTOR =====
// Handle common response scenarios
apiClient.interceptors.response.use(
    (response: AxiosResponse) => {
        return response;
    },
    (error) => {
        // Handle 401 Unauthorized - remove invalid token
        if (error.response?.status === 401) {
            tokenManager.removeToken();
            // Optionally redirect to login page
            window.location.href = '/login';
        }

        // Format error for consistent handling
        const apiError: ApiError = {
            error: error.response?.data?.error || 'Request Failed',
            message: error.response?.data?.message || error.message,
            status: error.response?.status || 500,
            timestamp: error.response?.data?.timestamp || new Date().toISOString(),
            validationErrors: error.response?.data?.validationErrors
        };

        return Promise.reject(apiError);
    }
);

// ===== AUTHENTICATION API =====
export const authApi = {
    login: async (credentials: LoginRequest): Promise<AuthResponse> => {
        const response = await apiClient.post<AuthResponse>('/auth/login', credentials);

        // Automatically store token on successful login
        if (response.data.token) {
            tokenManager.setToken(response.data.token);
        }

        return response.data;
    },

    register: async (userData: RegisterRequest): Promise<AuthResponse> => {
        const response = await apiClient.post<AuthResponse>('/auth/register', userData);

        // Automatically store token on successful registration
        if (response.data.token) {
            tokenManager.setToken(response.data.token);
        }

        return response.data;
    },

    logout: (): void => {
        tokenManager.removeToken();
    },

    testConnection: async (): Promise<string> => {
        const response = await apiClient.get<string>('/auth/test');
        return response.data;
    }
};

// ===== USER API =====
export const userApi = {
    // Get current user profile
    getCurrentUser: async (): Promise<UserResponse> => {
        const response = await apiClient.get<UserResponse>('/users/me');
        return response.data;
    },

    // Get user by ID (with permission checks)
    getUserById: async (id: number): Promise<UserResponse> => {
        const response = await apiClient.get<UserResponse>(`/users/${id}`);
        return response.data;
    },

    // Get all users (admin only) with pagination
    getAllUsers: async (params?: PaginationParams): Promise<PaginatedResponse<UserResponse>> => {
        const response = await apiClient.get<PaginatedResponse<UserResponse>>('/users', { params });
        return response.data;
    },

    // Create new user (admin only)
    createUser: async (userData: UserCreateRequest): Promise<UserResponse> => {
        const response = await apiClient.post<UserResponse>('/users', userData);
        return response.data;
    },

    // Update user (admin or own profile)
    updateUser: async (id: number, userData: UserUpdateRequest): Promise<UserResponse> => {
        const response = await apiClient.put<UserResponse>(`/users/${id}`, userData);
        return response.data;
    },

    // Update own profile
    updateOwnProfile: async (userData: UserUpdateRequest): Promise<UserResponse> => {
        const response = await apiClient.put<UserResponse>('/users/me', userData);
        return response.data;
    },

    // Update user performance (managers for direct reports)
    updateUserPerformance: async (id: number, performanceData: UserPerformanceUpdateRequest): Promise<UserResponse> => {
        const response = await apiClient.put<UserResponse>(`/users/${id}/performance`, performanceData);
        return response.data;
    },

    // Get users in my department (managers)
    getMyDepartmentUsers: async (): Promise<UserResponse[]> => {
        const response = await apiClient.get<UserResponse[]>('/users/my-department');
        return response.data;
    },

    // Get direct reports (managers)
    getMyDirectReports: async (): Promise<UserResponse[]> => {
        const response = await apiClient.get<UserResponse[]>('/users/my-reports');
        return response.data;
    },

    // Get team performance summary (managers)
    getTeamPerformance: async (): Promise<UserResponse[]> => {
        const response = await apiClient.get<UserResponse[]>('/users/team-performance');
        return response.data;
    },

    // Deactivate user (system admin)
    deactivateUser: async (id: number): Promise<void> => {
        await apiClient.patch(`/users/${id}/deactivate`);
    },

    // Delete user (system admin)
    deleteUser: async (id: number): Promise<void> => {
        await apiClient.delete(`/users/${id}`);
    },

    // Test endpoint
    testConnection: async (): Promise<string> => {
        const response = await apiClient.get<string>('/users/test');
        return response.data;
    }
};

// ===== ORGANIZATION API =====
export const organizationApi = {
    // Get all organizations with pagination
    getAll: async (params?: PaginationParams): Promise<PaginatedResponse<OrganizationResponse>> => {
        const response = await apiClient.get<PaginatedResponse<OrganizationResponse>>('/organizations', { params });
        return response.data;
    },

    // Get organization by ID
    getById: async (id: number): Promise<OrganizationResponse> => {
        const response = await apiClient.get<OrganizationResponse>(`/organizations/${id}`);
        return response.data;
    },

    // Get active organizations only
    getActive: async (): Promise<OrganizationResponse[]> => {
        const response = await apiClient.get<OrganizationResponse[]>('/organizations/active');
        return response.data;
    },

    // Create organization
    create: async (orgData: OrganizationCreateRequest): Promise<OrganizationResponse> => {
        const response = await apiClient.post<OrganizationResponse>('/organizations', orgData);
        return response.data;
    },

    // Update organization
    update: async (id: number, orgData: OrganizationUpdateRequest): Promise<OrganizationResponse> => {
        const response = await apiClient.put<OrganizationResponse>(`/organizations/${id}`, orgData);
        return response.data;
    },

    // Deactivate organization
    deactivate: async (id: number): Promise<void> => {
        await apiClient.patch(`/organizations/${id}/deactivate`);
    },

    // Delete organization
    delete: async (id: number): Promise<void> => {
        await apiClient.delete(`/organizations/${id}`);
    },

    // Search organizations by name
    search: async (name: string): Promise<OrganizationResponse[]> => {
        const response = await apiClient.get<OrganizationResponse[]>('/organizations/search', {
            params: { name }
        });
        return response.data;
    },

    // Test endpoint
    testConnection: async (): Promise<string> => {
        const response = await apiClient.get<string>('/organizations/test');
        return response.data;
    }
};

// ===== DEPARTMENT API =====
export const departmentApi = {
    // Get all departments with pagination
    getAll: async (params?: PaginationParams): Promise<PaginatedResponse<DepartmentResponse>> => {
        const response = await apiClient.get<PaginatedResponse<DepartmentResponse>>('/departments', { params });
        return response.data;
    },

    // Get department by ID
    getById: async (id: number): Promise<DepartmentResponse> => {
        const response = await apiClient.get<DepartmentResponse>(`/departments/${id}`);
        return response.data;
    },

    // Get departments by organization
    getByOrganization: async (organizationId: number): Promise<DepartmentResponse[]> => {
        const response = await apiClient.get<DepartmentResponse[]>(`/departments/organization/${organizationId}`);
        return response.data;
    },

    // Get active departments only
    getActive: async (): Promise<DepartmentResponse[]> => {
        const response = await apiClient.get<DepartmentResponse[]>('/departments/active');
        return response.data;
    },

    // Create department
    create: async (deptData: DepartmentCreateRequest): Promise<DepartmentResponse> => {
        const response = await apiClient.post<DepartmentResponse>('/departments', deptData);
        return response.data;
    },

    // Update department
    update: async (id: number, deptData: DepartmentUpdateRequest): Promise<DepartmentResponse> => {
        const response = await apiClient.put<DepartmentResponse>(`/departments/${id}`, deptData);
        return response.data;
    },

    // Remove manager from department
    removeManager: async (id: number): Promise<DepartmentResponse> => {
        const response = await apiClient.patch<DepartmentResponse>(`/departments/${id}/remove-manager`);
        return response.data;
    },

    // Deactivate department
    deactivate: async (id: number): Promise<void> => {
        await apiClient.patch(`/departments/${id}/deactivate`);
    },

    // Delete department
    delete: async (id: number): Promise<void> => {
        await apiClient.delete(`/departments/${id}`);
    },

    // Search departments by name
    search: async (name: string): Promise<DepartmentResponse[]> => {
        const response = await apiClient.get<DepartmentResponse[]>('/departments/search', {
            params: { name }
        });
        return response.data;
    },

    // Test endpoint
    testConnection: async (): Promise<string> => {
        const response = await apiClient.get<string>('/departments/test');
        return response.data;
    }
};

// ===== HEALTH CHECK API =====
export const healthApi = {
    checkAllEndpoints: async (): Promise<Record<string, string>> => {
        const results: Record<string, string> = {};

        try {
            results.auth = await authApi.testConnection();
        } catch (error) {
            results.auth = 'Failed';
        }

        try {
            results.users = await userApi.testConnection();
        } catch (error) {
            results.users = 'Failed';
        }

        try {
            results.organizations = await organizationApi.testConnection();
        } catch (error) {
            results.organizations = 'Failed';
        }

        try {
            results.departments = await departmentApi.testConnection();
        } catch (error) {
            results.departments = 'Failed';
        }

        return results;
    }
};

// Export the configured axios instance for custom requests
export { apiClient };

// Export everything as default object for convenience
export default {
    auth: authApi,
    users: userApi,
    organizations: organizationApi,
    departments: departmentApi,
    health: healthApi,
    tokenManager
};
