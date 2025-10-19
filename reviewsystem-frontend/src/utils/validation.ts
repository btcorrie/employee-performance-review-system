import { VALIDATION_RULES } from '../constants/auth';

export interface ValidationError {
    field: string;
    message: string;
}

export interface ValidationResult {
    isValid: boolean;
    errors: ValidationError[];
}

// Validate individual fields
export const validateField = (field: string, value: string): ValidationError | null => {
    switch (field) {
        case 'username':
            return validateUsername(value);
        case 'email':
            return validateEmail(value);
        case 'password':
            return validatePassword(value);
        case 'firstName':
        case 'lastName':
            return validateName(field, value);
        default:
            return null;
    }
};

// Validate username
export const validateUsername = (username: string): ValidationError | null => {
    if (!username.trim()) {
        return { field: 'username', message: 'Username is required' };
    }

    if (username.length < VALIDATION_RULES.username.minLength) {
        return { field: 'username', message: `Username must be at least ${VALIDATION_RULES.username.minLength} characters` };
    }

    if (username.length > VALIDATION_RULES.username.maxLength) {
        return { field: 'username', message: `Username cannot exceed ${VALIDATION_RULES.username.maxLength} characters` };
    }

    if (!VALIDATION_RULES.username.pattern.test(username)) {
        return { field: 'username', message: VALIDATION_RULES.username.message };
    }

    return null;
};

// Validate email
export const validateEmail = (email: string): ValidationError | null => {
    if (!email.trim()) {
        return { field: 'email', message: 'Email is required' };
    }

    if (!VALIDATION_RULES.email.pattern.test(email)) {
        return { field: 'email', message: VALIDATION_RULES.email.message };
    }

    return null;
};

// Validate password
export const validatePassword = (password: string): ValidationError | null => {
    if (!password) {
        return { field: 'password', message: 'Password is required' };
    }

    if (password.length < VALIDATION_RULES.password.minLength) {
        return { field: 'password', message: `Password must be at least ${VALIDATION_RULES.password.minLength} characters` };
    }

    if (password.length > VALIDATION_RULES.password.maxLength) {
        return { field: 'password', message: `Password cannot exceed ${VALIDATION_RULES.password.maxLength} characters` };
    }

    return null;
};

// Validate password confirmation
export const validatePasswordConfirmation = (password: string, confirmPassword: string): ValidationError | null => {
    if (!confirmPassword) {
        return { field: 'confirmPassword', message: 'Please confirm your password' };
    }

    if (password !== confirmPassword) {
        return { field: 'confirmPassword', message: 'Passwords do not match' };
    }

    return null;
};

// Validate name fields (firstName, lastName)
export const validateName = (field: string, name: string): ValidationError | null => {
    const displayName = field === 'firstName' ? 'First name' : 'Last name';

    if (!name.trim()) {
        return { field, message: `${displayName} is required` };
    }

    if (name.length < VALIDATION_RULES.name.minLength) {
        return { field, message: `${displayName} must be at least ${VALIDATION_RULES.name.minLength} characters` };
    }

    if (name.length > VALIDATION_RULES.name.maxLength) {
        return { field, message: `${displayName} cannot exceed ${VALIDATION_RULES.name.maxLength} characters` };
    }

    if (!VALIDATION_RULES.name.pattern.test(name)) {
        return { field, message: VALIDATION_RULES.name.message };
    }

    return null;
};

// Validate entire login form
export const validateLoginForm = (username: string, password: string): ValidationResult => {
    const errors: ValidationError[] = [];

    const usernameError = validateUsername(username);
    if (usernameError) errors.push(usernameError);

    const passwordError = validatePassword(password);
    if (passwordError) errors.push(passwordError);

    return {
        isValid: errors.length === 0,
        errors
    };
};

// Validate entire registration form
export const validateRegistrationForm = (
    username: string,
    email: string,
    password: string,
    confirmPassword: string,
    firstName: string,
    lastName: string
): ValidationResult => {
    const errors: ValidationError[] = [];

    const usernameError = validateUsername(username);
    if (usernameError) errors.push(usernameError);

    const emailError = validateEmail(email);
    if (emailError) errors.push(emailError);

    const passwordError = validatePassword(password);
    if (passwordError) errors.push(passwordError);

    const confirmPasswordError = validatePasswordConfirmation(password, confirmPassword);
    if (confirmPasswordError) errors.push(confirmPasswordError);

    const firstNameError = validateName('firstName', firstName);
    if (firstNameError) errors.push(firstNameError);

    const lastNameError = validateName('lastName', lastName);
    if (lastNameError) errors.push(lastNameError);

    return {
        isValid: errors.length === 0,
        errors
    };
};

// Get error message for a specific field
export const getFieldError = (errors: ValidationError[], fieldName: string): string | null => {
    const error = errors.find(err => err.field === fieldName);
    return error ? error.message : null;
};

// Check if a specific field has an error
export const hasFieldError = (errors: ValidationError[], fieldName: string): boolean => {
    return errors.some(err => err.field === fieldName);
};
