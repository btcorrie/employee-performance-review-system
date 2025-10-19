import React, { useState } from 'react';
import { EyeIcon, EyeSlashIcon } from '@heroicons/react/24/outline';
import { LoginRequest } from '../../types/api';
import { validateLoginForm, getFieldError, hasFieldError } from '../../utils/validation';
import { useAuth } from '../../hooks/useAuth';

interface LoginFormProps {
    onSuccess?: () => void;
    onSwitchToRegister?: () => void;
}

export const LoginForm: React.FC<LoginFormProps> = ({ onSuccess, onSwitchToRegister }) => {
    const [formData, setFormData] = useState<LoginRequest>({
        username: '',
        password: ''
    });

    const [showPassword, setShowPassword] = useState(false);
    const [validationErrors, setValidationErrors] = useState<any[]>([]);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const { login, error: authError, clearError } = useAuth();

    const handleInputChange = (field: keyof LoginRequest, value: string) => {
        setFormData(prev => ({ ...prev, [field]: value }));

        // Clear validation errors for this field when user starts typing
        setValidationErrors(prev => prev.filter(err => err.field !== field));

        // Clear auth errors when user makes changes
        if (authError) {
            clearError();
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        // Validate form
        const validation = validateLoginForm(formData.username, formData.password);

        if (!validation.isValid) {
            setValidationErrors(validation.errors);
            return;
        }

        setIsSubmitting(true);
        setValidationErrors([]);

        try {
            await login(formData);
            onSuccess?.();
        } catch (error) {
            // Error is handled by the useAuth hook and displayed via authError
        } finally {
            setIsSubmitting(false);
        }
    };

    const togglePasswordVisibility = () => {
        setShowPassword(!showPassword);
    };

    return (
        <div className="w-full max-w-md mx-auto">
            <div className="bg-white shadow-lg rounded-lg p-8">
                <div className="text-center mb-8">
                    <h2 className="text-2xl font-bold text-gray-900 mb-2">Welcome Back</h2>
                    <p className="text-gray-600">Sign in to your account</p>
                </div>

                <form onSubmit={handleSubmit} className="space-y-6">
                    {/* Username Field */}
                    <div>
                        <label htmlFor="username" className="block text-sm font-medium text-gray-700 mb-2">
                            Username
                        </label>
                        <input
                            id="username"
                            type="text"
                            value={formData.username}
                            onChange={(e) => handleInputChange('username', e.target.value)}
                            className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                                hasFieldError(validationErrors, 'username')
                                    ? 'border-red-300 focus:border-red-500'
                                    : 'border-gray-300 focus:border-blue-500'
                            }`}
                            placeholder="Enter your username"
                            disabled={isSubmitting}
                        />
                        {hasFieldError(validationErrors, 'username') && (
                            <p className="mt-1 text-sm text-red-600">
                                {getFieldError(validationErrors, 'username')}
                            </p>
                        )}
                    </div>

                    {/* Password Field */}
                    <div>
                        <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-2">
                            Password
                        </label>
                        <div className="relative">
                            <input
                                id="password"
                                type={showPassword ? 'text' : 'password'}
                                value={formData.password}
                                onChange={(e) => handleInputChange('password', e.target.value)}
                                className={`w-full px-3 py-2 pr-10 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                                    hasFieldError(validationErrors, 'password')
                                        ? 'border-red-300 focus:border-red-500'
                                        : 'border-gray-300 focus:border-blue-500'
                                }`}
                                placeholder="Enter your password"
                                disabled={isSubmitting}
                            />
                            <button
                                type="button"
                                onClick={togglePasswordVisibility}
                                className="absolute inset-y-0 right-0 pr-3 flex items-center"
                                disabled={isSubmitting}
                            >
                                {showPassword ? (
                                    <EyeSlashIcon className="h-5 w-5 text-gray-400 hover:text-gray-600" />
                                ) : (
                                    <EyeIcon className="h-5 w-5 text-gray-400 hover:text-gray-600" />
                                )}
                            </button>
                        </div>
                        {hasFieldError(validationErrors, 'password') && (
                            <p className="mt-1 text-sm text-red-600">
                                {getFieldError(validationErrors, 'password')}
                            </p>
                        )}
                    </div>

                    {/* Auth Error Display */}
                    {authError && (
                        <div className="bg-red-50 border border-red-200 rounded-md p-3">
                            <p className="text-sm text-red-600">{authError}</p>
                        </div>
                    )}

                    {/* Submit Button */}
                    <button
                        type="submit"
                        disabled={isSubmitting}
                        className="w-full bg-blue-500 hover:bg-blue-600 disabled:bg-blue-300 text-white font-semibold py-2 px-4 rounded-md transition duration-200 flex items-center justify-center"
                    >
                        {isSubmitting ? (
                            <>
                                <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                </svg>
                                Signing In...
                            </>
                        ) : (
                            'Sign In'
                        )}
                    </button>
                </form>

                {/* Switch to Register */}
                {onSwitchToRegister && (
                    <div className="mt-6 text-center">
                        <p className="text-sm text-gray-600">
                            Don't have an account?{' '}
                            <button
                                type="button"
                                onClick={onSwitchToRegister}
                                className="text-blue-500 hover:text-blue-600 font-medium"
                                disabled={isSubmitting}
                            >
                                Sign up here
                            </button>
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
};
