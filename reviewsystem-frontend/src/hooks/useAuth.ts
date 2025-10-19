import { useState, useEffect, useCallback } from 'react';
import { AuthResponse, LoginRequest, RegisterRequest, UserResponse, UserRole } from '../types/api';
import api, { tokenManager } from '../services/api';

interface AuthState {
    isAuthenticated: boolean;
    user: AuthResponse | null;
    userProfile: UserResponse | null;
    isLoading: boolean;
    error: string | null;
}

interface AuthActions {
    login: (credentials: LoginRequest) => Promise<void>;
    register: (userData: RegisterRequest) => Promise<void>;
    logout: () => void;
    refreshUserProfile: () => Promise<void>;
    clearError: () => void;
    checkAuthStatus: () => boolean;
    hasRole: (requiredRole: UserRole) => boolean;
    hasAnyRole: (roles: UserRole[]) => boolean;
}

export const useAuth = (): AuthState & AuthActions => {
    const [authState, setAuthState] = useState<AuthState>({
        isAuthenticated: false,
        user: null,
        userProfile: null,
        isLoading: true,
        error: null
    });

    // Check if user is authenticated on hook initialization
    useEffect(() => {
        checkInitialAuth();
    }, []);

    const checkInitialAuth = async () => {
        setAuthState(prev => ({ ...prev, isLoading: true }));

        try {
            // Check if we have a valid token
            const hasValidToken = tokenManager.isTokenValid();

            if (hasValidToken) {
                // Try to get current user profile to verify token is still valid
                const userProfile = await api.users.getCurrentUser();

                // Get stored user data from localStorage
                const storedUser = localStorage.getItem('current_user');
                const user = storedUser ? JSON.parse(storedUser) : null;

                setAuthState(prev => ({
                    ...prev,
                    isAuthenticated: true,
                    user,
                    userProfile,
                    isLoading: false,
                    error: null
                }));
            } else {
                // No valid token
                setAuthState(prev => ({
                    ...prev,
                    isAuthenticated: false,
                    user: null,
                    userProfile: null,
                    isLoading: false,
                    error: null
                }));
            }
        } catch (error) {
            // Token might be expired or invalid
            logout();
            setAuthState(prev => ({
                ...prev,
                isAuthenticated: false,
                user: null,
                userProfile: null,
                isLoading: false,
                error: null
            }));
        }
    };

    const login = async (credentials: LoginRequest): Promise<void> => {
        setAuthState(prev => ({ ...prev, isLoading: true, error: null }));

        try {
            const authResponse = await api.auth.login(credentials);

            // Store user data in localStorage
            localStorage.setItem('current_user', JSON.stringify(authResponse));

            // Get full user profile
            const userProfile = await api.users.getCurrentUser();

            setAuthState(prev => ({
                ...prev,
                isAuthenticated: true,
                user: authResponse,
                userProfile,
                isLoading: false,
                error: null
            }));
        } catch (error: any) {
            setAuthState(prev => ({
                ...prev,
                isAuthenticated: false,
                user: null,
                userProfile: null,
                isLoading: false,
                error: error.message || 'Login failed'
            }));
            throw error;
        }
    };

    const register = async (userData: RegisterRequest): Promise<void> => {
        setAuthState(prev => ({ ...prev, isLoading: true, error: null }));

        try {
            const authResponse = await api.auth.register(userData);

            // Store user data in localStorage
            localStorage.setItem('current_user', JSON.stringify(authResponse));

            // Get full user profile
            const userProfile = await api.users.getCurrentUser();

            setAuthState(prev => ({
                ...prev,
                isAuthenticated: true,
                user: authResponse,
                userProfile,
                isLoading: false,
                error: null
            }));
        } catch (error: any) {
            setAuthState(prev => ({
                ...prev,
                isAuthenticated: false,
                user: null,
                userProfile: null,
                isLoading: false,
                error: error.message || 'Registration failed'
            }));
            throw error;
        }
    };

    const logout = useCallback(() => {
        // Clear tokens and user data
        api.auth.logout();
        localStorage.removeItem('current_user');

        setAuthState({
            isAuthenticated: false,
            user: null,
            userProfile: null,
            isLoading: false,
            error: null
        });
    }, []);

    const refreshUserProfile = async (): Promise<void> => {
        if (!authState.isAuthenticated) return;

        try {
            const userProfile = await api.users.getCurrentUser();
            setAuthState(prev => ({
                ...prev,
                userProfile,
                error: null
            }));
        } catch (error: any) {
            setAuthState(prev => ({
                ...prev,
                error: error.message || 'Failed to refresh user profile'
            }));
        }
    };

    const clearError = useCallback(() => {
        setAuthState(prev => ({ ...prev, error: null }));
    }, []);

    const checkAuthStatus = useCallback((): boolean => {
        return authState.isAuthenticated && tokenManager.isTokenValid();
    }, [authState.isAuthenticated]);

    const hasRole = useCallback((requiredRole: UserRole): boolean => {
        if (!authState.user) return false;
        return authState.user.role === requiredRole;
    }, [authState.user]);

    const hasAnyRole = useCallback((roles: UserRole[]): boolean => {
        if (!authState.user) return false;
        return roles.includes(authState.user.role as UserRole);
    }, [authState.user]);

    return {
        // State
        isAuthenticated: authState.isAuthenticated,
        user: authState.user,
        userProfile: authState.userProfile,
        isLoading: authState.isLoading,
        error: authState.error,

        // Actions
        login,
        register,
        logout,
        refreshUserProfile,
        clearError,
        checkAuthStatus,
        hasRole,
        hasAnyRole
    };
};
