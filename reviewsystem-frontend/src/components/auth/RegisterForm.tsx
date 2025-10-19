import React, { useState } from 'react';
import { EyeIcon, EyeSlashIcon } from '@heroicons/react/24/outline';
import { RegisterRequest, UserRole } from '../../types/api';
import { validateRegistrationForm, getFieldError, hasFieldError } from '../../utils/validation';
import { useAuth } from '../../hooks/useAuth';
import { AVAILABLE_ROLES, ROLE_DISPLAY } from '../../constants/auth';

interface RegisterFormProps {
    onSuccess?: () => void;
    onSwitchToLogin?: () => void;
}

export const RegisterForm: React.FC<RegisterFormProps> = ({ onSuccess, onSwitchToLogin }) => {
    const [formData, setFormData] = useState<RegisterRequest>({
        username: '',
        email: '',
        password: '',
        firstName: '',
        lastName: '',
        role: UserRole.EMPLOYEE
    });

    const [confirmPassword, setConfirmPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [validationErrors, setValidationErrors] = useState<any[]>([]);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const { register, error: authError, clearError } = useAuth();

    const handleInputChange = (field: keyof RegisterRequest | 'confirmPassword', value: string) => {
        if (field === 'confirmPassword') {
            setConfirmPassword(value);
        } else {
            setFormData(prev => ({ ...prev, [field]: value }));
        }

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
        const validation = validateRegistrationForm(
            formData.username,
            formData.email,
            formData.password,
            confirmPassword,
            formData.firstName,
            formData.lastName
        );

        if (!validation.isValid) {
            setValidationErrors(validation.errors);
            return;
        }

        setIsSubmitting(true);
        setValidationErrors([]);

        try {
            await register(formData);
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

    const toggleConfirmPasswordVisibility = () => {
        setShowConfirmPassword(!showConfirmPassword);
    };

    return (
        <div className="w-full max-w-md mx-auto">
            <div className="bg-white shadow-lg rounded-lg p-8">
                <div className="text-center mb-8">
                    <h2 className="text-2xl font-bold text-gray-900 mb-2">Create Account</h2>
                    <p className="text-gray-600">Join our employee review system</p>
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
                            placeholder="Choose a username"
                            disabled={isSubmitting}
                        />
                        {hasFieldError(validationErrors, 'username') && (
                            <p className="mt-1 text-sm text-red-600">
                                {getFieldError(validationErrors, 'username')}
                            </p>
                        )}
                    </div>

                    {/* Email Field */}
                    <div>
                        <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
                            Email Address
                        </label>
                        <input
                            id="email"
                            type="email"
                            value={formData.email}
                            onChange={(e) => handleInputChange('email', e.target.value)}
                            className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                                hasFieldError(validationErrors, 'email')
                                    ? 'border-red-300 focus:border-red-500'
                                    : 'border-gray-300 focus:border-blue-500'
                            }`}
                            placeholder="Enter your email"
                            disabled={isSubmitting}
                        />
                        {hasFieldError(validationErrors, 'email') && (
                            <p className="mt-1 text-sm text-red-600">
                                {getFieldError(validationErrors, 'email')}
                            </p>
                        )}
                    </div>

                    {/* Name Fields - Side by Side */}
                    <div className="grid grid-cols-2 gap-4">
                        {/* First Name */}
                        <div>
                            <label htmlFor="firstName" className="block text-sm font-medium text-gray-700 mb-2">
                                First Name
                            </label>
                            <input
                                id="firstName"
                                type="text"
                                value={formData.firstName}
                                onChange={(e) => handleInputChange('firstName', e.target.value)}
                                className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                                    hasFieldError(validationErrors, 'firstName')
                                        ? 'border-red-300 focus:border-red-500'
                                        : 'border-gray-300 focus:border-blue-500'
                                }`}
                                placeholder="First name"
                                disabled={isSubmitting}
                            />
                            {hasFieldError(validationErrors, 'firstName') && (
                                <p className="mt-1 text-sm text-red-600">
                                    {getFieldError(validationErrors, 'firstName')}
                                </p>
                            )}
                        </div>

                        {/* Last Name */}
                        <div>
                            <label htmlFor="lastName" className="block text-sm font-medium text-gray-700 mb-2">
                                Last Name
                            </label>
                            <input
                                id="lastName"
                                type="text"
                                value={formData.lastName}
                                onChange={(e) => handleInputChange('lastName', e.target.value)}
                                className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                                    hasFieldError(validationErrors, 'lastName')
                                        ? 'border-red-300 focus:border-red-500'
                                        : 'border-gray-300 focus:border-blue-500'
                                }`}
                                placeholder="Last name"
                                disabled={isSubmitting}
                            />
                            {hasFieldError(validationErrors, 'lastName') && (
                                <p className="mt-1 text-sm text-red-600">
                                    {getFieldError(validationErrors, 'lastName')}
                                </p>
                            )}
                        </div>
                    </div>

                    {/* Role Selection */}
                    <div>
                        <label htmlFor="role" className="block text-sm font-medium text-gray-700 mb-2">
                            Role
                        </label>
                        <select
                            id="role"
                            value={formData.role}
                            onChange={(e) => handleInputChange('role', e.target.value as UserRole)}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                            disabled={isSubmitting}
                        >
                            {AVAILABLE_ROLES.map((role) => (
                                <option key={role.value} value={role.value}>
                                    {role.label}
                                </option>
                            ))}
                        </select>
                        <p className="mt-1 text-sm text-gray-500">
                            {ROLE_DISPLAY[formData.role].description}
                        </p>
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
                                placeholder="Create a password"
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

                    {/* Confirm Password Field */}
                    <div>
                        <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700 mb-2">
                            Confirm Password
                        </label>
                        <div className="relative">
                            <input
                                id="confirmPassword"
                                type={showConfirmPassword ? 'text' : 'password'}
                                value={confirmPassword}
                                onChange={(e) => handleInputChange('confirmPassword', e.target.value)}
                                className={`w-full px-3 py-2 pr-10 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                                    hasFieldError(validationErrors, 'confirmPassword')
                                        ? 'border-red-300 focus:border-red-500'
                                        : 'border-gray-300 focus:border-blue-500'
                                }`}
                                placeholder="Confirm your password"
                                disabled={isSubmitting}
                            />
                            <button
                                type="button"
                                onClick={toggleConfirmPasswordVisibility}
                                className="absolute inset-y-0 right-0 pr-3 flex items-center"
                                disabled={isSubmitting}
                            >
                                {showConfirmPassword ? (
                                    <EyeSlashIcon className="h-5 w-5 text-gray-400 hover:text-gray-600" />
                                ) : (
                                    <EyeIcon className="h-5 w-5 text-gray-400 hover:text-gray-600" />
                                )}
                            </button>
                        </div>
                        {hasFieldError(validationErrors, 'confirmPassword') && (
                            <p className="mt-1 text-sm text-red-600">
                                {getFieldError(validationErrors, 'confirmPassword')}
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
                                Creating Account...
                            </>
                        ) : (
                            'Create Account'
                        )}
                    </button>
                </form>

                {/* Switch to Login */}
                {onSwitchToLogin && (
                    <div className="mt-6 text-center">
                        <p className="text-sm text-gray-600">
                            Already have an account?{' '}
                            <button
                                type="button"
                                onClick={onSwitchToLogin}
                                className="text-blue-500 hover:text-blue-600 font-medium"
                                disabled={isSubmitting}
                            >
                                Sign in here
                            </button>
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
};
