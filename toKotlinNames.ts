export function toKotlinClassName(input: string): string {
    const sanitized = input
        .replace(/[^a-zA-Z0-9\s-]/g, '') // Remove all non-alphanumeric, non-space, and non-hyphen characters
        .replace(/\s+/g, '-') // Replace whitespace with hyphens
        .split('-') // Split by hyphens
        .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()) // Capitalize each word
        .join(''); // Join into a single string

    // Drop digits at the beginning
    return sanitized.replace(/^\d+/, '');
}

export function toKotlinMemberName(input: string): string {
    const sanitized = input
        .replace(/[^a-zA-Z0-9\s-]/g, '') // Remove all non-alphanumeric, non-space, and non-hyphen characters
        .replace(/\s+/g, '-') // Replace whitespace with hyphens
        .split('-') // Split by hyphens
        .map((word, index) => index === 0 
            ? word.charAt(0).toLowerCase() + word.slice(1).toLowerCase() 
            : word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()) // Capitalize first letter of subsequent words
        .join(''); // Join into a single string

    // Drop digits at the beginning and convert the first character to lowercase
    const result = sanitized.replace(/^\d+/, '');
    return result.charAt(0).toLowerCase() + result.slice(1);
}

export function toKotlinFunctionName(input: string): string {
    return toKotlinMemberName(input); // Function name follows the same rules as member names
}

