import React, { useState } from 'react';
import { CheckCircleIcon, XCircleIcon, UserCircleIcon } from '@heroicons/react/24/solid';
import { LoginForm } from './components/auth/LoginForm';
import { RegisterForm } from './components/auth/RegisterForm';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { useAuth } from './hooks/useAuth';
import api from './services/api';
import './App.css';

type AuthMode = 'login' | 'register' | 'dashboard';

interface ConnectionStatus {
    auth: boolean;
    users: boolean;
    organizations: boolean;
    departments: boolean;
}

function App() {
    const [authMode, setAuthMode] = useState<AuthMode>('login');
    const [connectionStatus, setConnectionStatus] = useState<ConnectionStatus>({
        auth: false,
        users: false,
        organizations: false,
        departments: false
    });
    const [isTestingConnection, setIsTestingConnection] = useState(false);
    const [testResults, setTestResults] = useState<Record<string, string>>({});

    const { isAuthenticated, user, logout, isLoading } = useAuth();

    const testApiConnections = async () => {
        setIsTestingConnection(true);

        try {
            const results = await api.health.checkAllEndpoints();
            setTestResults(results);

            setConnectionStatus({
                auth: results.auth !== 'Failed',
                users: results.users !== 'Failed',
                organizations: results.organizations !== 'Failed',
                departments: results.departments !== 'Failed'
            });
        } catch (error) {
            console.error('Failed to test API connections:', error);
            setConnectionStatus({
                auth: false,
                users: false,
                organizations: false,
                departments: false
            });
        } finally {
            setIsTestingConnection(false);
        }
    };

    const handleAuthSuccess = () => {
        setAuthMode('dashboard');
    };

    const handleLogout = () => {
        logout();
        setAuthMode('login');
    };

    const StatusIcon = ({ isConnected }: { isConnected: boolean }) => {
        return isConnected ? (
            <CheckCircleIcon className="h-5 w-5 text-green-500" />
        ) : (
            <XCircleIcon className="h-5 w-5 text-red-500" />
        );
    };

    const allConnected = Object.values(connectionStatus).every(status => status);

    // Show loading while determining auth state
    if (isLoading) {
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

    // If authenticated, show dashboard
    if (isAuthenticated && user) {
        return (
            <ProtectedRoute>
                <div className="min-h-screen bg-gray-100">
                    {/* Header */}
                    <header className="bg-white shadow-sm border-b">
                        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                            <div className="flex justify-between items-center h-16">
                                <div>
                                    <h1 className="text-xl font-semibold text-gray-900">Employee Review System</h1>
                                </div>
                                <div className="flex items-center space-x-4">
                                    <div className="flex items-center space-x-2">
                                        <UserCircleIcon className="h-6 w-6 text-gray-400" />
                                        <span className="text-sm text-gray-700">
                                            {user.username} ({user.role})
                                        </span>
                                    </div>
                                    <button
                                        onClick={handleLogout}
                                        className="bg-red-500 hover:bg-red-600 text-white text-sm px-3 py-1 rounded-md transition duration-200"
                                    >
                                        Logout
                                    </button>
                                </div>
                            </div>
                        </div>
                    </header>

                    {/* Main Content */}
                    <main className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
                        <div className="bg-white shadow-lg rounded-lg p-8">
                            <div className="text-center mb-8">
                                <h2 className="text-2xl font-bold text-gray-900 mb-2">
                                    Welcome, {user.username}!
                                </h2>
                                <p className="text-gray-600">
                                    You are successfully logged in with {user.role} permissions.
                                </p>
                            </div>

                            {/* API Connection Status */}
                            <div className="mb-8">
                                <div className="flex items-center justify-between mb-4">
                                    <h3 className="text-lg font-semibold text-gray-800">API Connection Status</h3>
                                    <button
                                        onClick={testApiConnections}
                                        disabled={isTestingConnection}
                                        className="px-3 py-1 text-sm bg-blue-500 hover:bg-blue-600 text-white rounded-md disabled:opacity-50"
                                    >
                                        {isTestingConnection ? 'Testing...' : 'Test Connections'}
                                    </button>
                                </div>

                                <div className="grid grid-cols-2 gap-4">
                                    <div className="flex items-center justify-between p-3 bg-gray-50 rounded-md">
                                        <span className="font-medium">Authentication API</span>
                                        <div className="flex items-center space-x-2">
                                            <StatusIcon isConnected={connectionStatus.auth} />
                                            <span className="text-sm text-gray-600">{testResults.auth || 'Not tested'}</span>
                                        </div>
                                    </div>

                                    <div className="flex items-center justify-between p-3 bg-gray-50 rounded-md">
                                        <span className="font-medium">Users API</span>
                                        <div className="flex items-center space-x-2">
                                            <StatusIcon isConnected={connectionStatus.users} />
                                            <span className="text-sm text-gray-600">{testResults.users || 'Not tested'}</span>
                                        </div>
                                    </div>

                                    <div className="flex items-center justify-between p-3 bg-gray-50 rounded-md">
                                        <span className="font-medium">Organizations API</span>
                                        <div className="flex items-center space-x-2">
                                            <StatusIcon isConnected={connectionStatus.organizations} />
                                            <span className="text-sm text-gray-600">{testResults.organizations || 'Not tested'}</span>
                                        </div>
                                    </div>

                                    <div className="flex items-center justify-between p-3 bg-gray-50 rounded-md">
                                        <span className="font-medium">Departments API</span>
                                        <div className="flex items-center space-x-2">
                                            <StatusIcon isConnected={connectionStatus.departments} />
                                            <span className="text-sm text-gray-600">{testResults.departments || 'Not tested'}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            {/* User Info Section */}
                            <div className="bg-blue-50 border border-blue-200 rounded-md p-4 mb-8">
                                <h3 className="text-lg font-semibold text-blue-900 mb-2">Your Account Information</h3>
                                <div className="grid grid-cols-2 gap-4 text-sm">
                                    <div>
                                        <span className="font-medium text-blue-800">User ID:</span>
                                        <span className="ml-2 text-blue-700">{user.id}</span>
                                    </div>
                                    <div>
                                        <span className="font-medium text-blue-800">Username:</span>
                                        <span className="ml-2 text-blue-700">{user.username}</span>
                                    </div>
                                    <div>
                                        <span className="font-medium text-blue-800">Email:</span>
                                        <span className="ml-2 text-blue-700">{user.email}</span>
                                    </div>
                                    <div>
                                        <span className="font-medium text-blue-800">Role:</span>
                                        <span className="ml-2 text-blue-700">{user.role}</span>
                                    </div>
                                </div>
                            </div>

                            {/* Success Message */}
                            <div className="bg-green-50 border border-green-200 rounded-md p-4">
                                <div className="flex items-center">
                                    <CheckCircleIcon className="h-6 w-6 text-green-500 mr-3" />
                                    <div>
                                        <p className="text-green-800 font-medium">🎉 Authentication Complete!</p>
                                        <p className="text-green-700 text-sm mt-1">
                                            Priority 2 is complete. Ready for Priority 3: Layout Components & Navigation.
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </main>
                </div>
            </ProtectedRoute>
        );
    }

    // Not authenticated - show login/register forms
    return (
        <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
            {authMode === 'login' && (
                <LoginForm
                    onSuccess={handleAuthSuccess}
                    onSwitchToRegister={() => setAuthMode('register')}
                />
            )}

            {authMode === 'register' && (
                <RegisterForm
                    onSuccess={handleAuthSuccess}
                    onSwitchToLogin={() => setAuthMode('login')}
                />
            )}

            {/* API Connection Status for Unauthenticated Users */}
            {!allConnected && (
                <div className="fixed bottom-4 right-4">
                    <div className="bg-yellow-50 border border-yellow-200 rounded-md p-3 shadow-lg">
                        <div className="flex items-center">
                            <XCircleIcon className="h-5 w-5 text-yellow-500 mr-2" />
                            <div>
                                <p className="text-yellow-800 text-sm font-medium">Backend Not Connected</p>
                                <button
                                    onClick={testApiConnections}
                                    disabled={isTestingConnection}
                                    className="text-yellow-700 text-xs underline mt-1"
                                >
                                    {isTestingConnection ? 'Testing...' : 'Test Connection'}
                                </button>
                            </div>
                        </div>
                    </div>
                )}
        </div>
    );
}

export default App;