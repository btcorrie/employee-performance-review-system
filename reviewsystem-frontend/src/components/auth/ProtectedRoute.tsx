import React from 'react';
import { UserRole } from '../../types/api';
import { useAuth } from '../../hooks/useAuth';

interface ProtectedRouteProps {
    children: React.ReactNode;
    requiredRole?: UserRole;
    requiredRoles?: UserRole[];
    fallback?: React.ReactNode;
    showLoading?: boolean;
}

export const ProtectedRoute = ({
                                   children,
                                   requiredRole,
                                   requiredRoles,
                                   fallback,
                                   showLoading = true
                               }: ProtectedRouteProps): React.ReactElement => {
    const { isAuthenticated, hasRole, hasAnyRole, isLoading, user } = useAuth();

    // Show loading spinner while authentication status is being determined
    if (isLoading && showLoading) {
        return (
            <div className="min-h-screen bg-gray-100 flex items-center justify-center">
                <div className="bg-white shadow-lg rounded-lg p-8 text-center">
                    <svg className="animate-spin h-8 w-8 text-blue-500 mx-auto mb-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    <p className="text-gray-600">Loading...</p>
                </div>
            </div>
        );
    }

    // Not authenticated - show login prompt
    if (!isAuthenticated) {
        if (fallback) {
            return <>{fallback}</>;
        }

        return (
            <div className="min-h-screen bg-gray-100 flex items-center justify-center">
                <div className="bg-white shadow-lg rounded-lg p-8 text-center max-w-md">
                    <div className="mb-6">
                        <svg className="h-16 w-16 text-red-500 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                        </svg>
                        <h2 className="text-2xl font-bold text-gray-900 mb-2">Authentication Required</h2>
                        <p className="text-gray-600">Please log in to access this page.</p>
                    </div>
                    <button
                        onClick={() => window.location.href = '/login'}
                        className="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded-md transition duration-200"
                    >
                        Go to Login
                    </button>
                </div>
            </div>
        );
    }

    // Check specific role requirement
    if (requiredRole && !hasRole(requiredRole)) {
        if (fallback) {
            return <>{fallback}</>;
        }

        return (
            <div className="min-h-screen bg-gray-100 flex items-center justify-center">
                <div className="bg-white shadow-lg rounded-lg p-8 text-center max-w-md">
                    <div className="mb-6">
                        <svg className="h-16 w-16 text-yellow-500 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
                        </svg>
                        <h2 className="text-2xl font-bold text-gray-900 mb-2">Access Denied</h2>
                        <p className="text-gray-600 mb-4">
                            You need <span className="font-semibold">{requiredRole}</span> role to access this page.
                        </p>
                        <p className="text-sm text-gray-500">
                            Current role: <span className="font-medium">{user?.role}</span>
                        </p>
                    </div>
                    <button
                        onClick={() => window.history.back()}
                        className="w-full bg-gray-500 hover:bg-gray-600 text-white font-semibold py-2 px-4 rounded-md transition duration-200"
                    >
                        Go Back
                    </button>
                </div>
            </div>
        );
    }

    // Check multiple roles requirement
    if (requiredRoles && !hasAnyRole(requiredRoles)) {
        if (fallback) {
            return <>{fallback}</>;
        }

        const roleNames = requiredRoles.join(', ');
        return (
            <div className="min-h-screen bg-gray-100 flex items-center justify-center">
                <div className="bg-white shadow-lg rounded-lg p-8 text-center max-w-md">
                    <div className="mb-6">
                        <svg className="h-16 w-16 text-yellow-500 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
                        </svg>
                        <h2 className="text-2xl font-bold text-gray-900 mb-2">Access Denied</h2>
                        <p className="text-gray-600 mb-4">
                            You need one of the following roles: <span className="font-semibold">{roleNames}</span>
                        </p>
                        <p className="text-sm text-gray-500">
                            Current role: <span className="font-medium">{user?.role}</span>
                        </p>
                    </div>
                    <button
                        onClick={() => window.history.back()}
                        className="w-full bg-gray-500 hover:bg-gray-600 text-white font-semibold py-2 px-4 rounded-md transition duration-200"
                    >
                        Go Back
                    </button>
                </div>
            </div>
        );
    }

    // All checks passed - render the protected content
    return <>{children}</>;
};

// Convenience wrapper components for common role combinations
export const EmployeeRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => (
    <ProtectedRoute requiredRole={UserRole.EMPLOYEE}>{children}</ProtectedRoute>
);

export const ManagerRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => (
    <ProtectedRoute requiredRoles={[UserRole.MANAGER, UserRole.HR_ADMIN, UserRole.SYSTEM_ADMIN]}>
        {children}
    </ProtectedRoute>
);

export const HRAdminRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => (
    <ProtectedRoute requiredRoles={[UserRole.HR_ADMIN, UserRole.SYSTEM_ADMIN]}>
        {children}
    </ProtectedRoute>
);

export const SystemAdminRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => (
    <ProtectedRoute requiredRole={UserRole.SYSTEM_ADMIN}>{children}</ProtectedRoute>
);
