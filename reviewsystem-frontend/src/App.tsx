import React, { useState, useEffect } from 'react';
import { CheckCircleIcon, XCircleIcon } from '@heroicons/react/24/solid';
import api from './services/api';
import './App.css';

interface ConnectionStatus {
    auth: boolean;
    users: boolean;
    organizations: boolean;
    departments: boolean;
}

function App() {
    const [connectionStatus, setConnectionStatus] = useState<ConnectionStatus>({
        auth: false,
        users: false,
        organizations: false,
        departments: false
    });
    const [isLoading, setIsLoading] = useState(true);
    const [testResults, setTestResults] = useState<Record<string, string>>({});

    useEffect(() => {
        testApiConnections();
    }, []);

    const testApiConnections = async () => {
        setIsLoading(true);

        try {
            const results = await api.health.checkAllEndpoints();
            setTestResults(results);

            // Update connection status based on results
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
            setIsLoading(false);
        }
    };

    const StatusIcon = ({ isConnected }: { isConnected: boolean }) => {
        return isConnected ? (
            <CheckCircleIcon className="h-5 w-5 text-green-500" />
        ) : (
            <XCircleIcon className="h-5 w-5 text-red-500" />
        );
    };

    const allConnected = Object.values(connectionStatus).every(status => status);

    return (
        <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
            <div className="bg-white shadow-lg rounded-lg p-8 max-w-lg w-full">
                <div className="text-center mb-8">
                    <h1 className="text-3xl font-bold text-gray-900 mb-4">
                        Employee Review System
                    </h1>
                    <p className="text-gray-600 mb-6">
                        Spring Boot + React + TypeScript + Tailwind CSS
                    </p>
                </div>

                {/* API Connection Status */}
                <div className="mb-8">
                    <div className="flex items-center justify-between mb-4">
                        <h2 className="text-lg font-semibold text-gray-800">API Connection Status</h2>
                        <button
                            onClick={testApiConnections}
                            disabled={isLoading}
                            className="px-3 py-1 text-sm bg-blue-500 hover:bg-blue-600 text-white rounded-md disabled:opacity-50"
                        >
                            {isLoading ? 'Testing...' : 'Refresh'}
                        </button>
                    </div>

                    <div className="space-y-3">
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

                {/* Action Buttons */}
                <div className="space-y-4">
                    <button
                        className="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-3 px-4 rounded-md transition duration-200"
                        disabled={!allConnected}
                    >
                        Login
                    </button>
                    <button
                        className="w-full border border-gray-300 hover:bg-gray-50 text-gray-700 font-semibold py-3 px-4 rounded-md transition duration-200"
                        disabled={!allConnected}
                    >
                        Register
                    </button>

                    {!allConnected && (
                        <p className="text-sm text-red-600 text-center">
                            Please ensure the Spring Boot backend is running on localhost:8080
                        </p>
                    )}
                </div>

                {/* Status Summary */}
                <div className={`mt-6 p-4 rounded-md ${allConnected ? 'bg-green-50 border border-green-200' : 'bg-yellow-50 border border-yellow-200'}`}>
                    <p className={`text-sm ${allConnected ? 'text-green-800' : 'text-yellow-800'}`}>
                        {allConnected ? (
                            <>
                                ✅ All APIs connected successfully!<br/>
                                ✅ Ready to build authentication components
                            </>
                        ) : (
                            <>
                                ⚠️ Some API endpoints are not responding<br/>
                                🔄 Check your Spring Boot backend status
                            </>
                        )}
                    </p>
                </div>
            </div>
        </div>
    );
}

export default App;