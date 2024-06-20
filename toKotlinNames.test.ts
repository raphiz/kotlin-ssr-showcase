import { describe, it, expect } from 'vitest';
import { toKotlinClassName, toKotlinMemberName, toKotlinFunctionName } from './toKotlinNames';

describe('Kotlin Naming Conventions', () => {
    it.each([
        ['foo-bar-controller', 'FooBarController'],
        ['hello world', 'HelloWorld'],
        ['kotlin\nclass\nname', 'KotlinClassName'],
        ['example string', 'ExampleString'],
        ['test-case-123', 'TestCase123'],
        ['a-b-c-🔥', 'ABC'],
        ['single', 'Single'],
        ['123-foo-bar', 'FooBar'],
        ['   leading and trailing spaces   ', 'LeadingAndTrailingSpaces'],
    ])('toKotlinClassName converts %s to %s', (input, expected) => {
        expect(toKotlinClassName(input)).toBe(expected);
    });

    it.each([
        ['foo-bar-controller', 'fooBarController'],
        ['hello world', 'helloWorld'],
        ['kotlin\nmember\nname', 'kotlinMemberName'],
        ['example string', 'exampleString'],
        ['test-case-123', 'testCase123'],
        ['a-b-c-🔥', 'aBC'],
        ['single', 'single'],
        ['123-foo-bar', 'fooBar'],
        ['   leading and trailing spaces   ', 'leadingAndTrailingSpaces'],
    ])('toKotlinMemberName converts %s to %s', (input, expected) => {
        expect(toKotlinMemberName(input)).toBe(expected);
    });

    it.each([
        ['foo-bar-function', 'fooBarFunction'],
        ['hello world', 'helloWorld'],
        ['kotlin\nfunction\nname', 'kotlinFunctionName'],
        ['example string', 'exampleString'],
        ['test-case-123', 'testCase123'],
        ['a-b-c-🔥', 'aBC'],
        ['single', 'single'],
        ['123-foo-bar', 'fooBar'],
        ['   leading and trailing spaces   ', 'leadingAndTrailingSpaces'],
    ])('toKotlinFunctionName converts %s to %s', (input, expected) => {
        expect(toKotlinFunctionName(input)).toBe(expected);
    });
});

