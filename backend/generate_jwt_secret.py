#!/usr/bin/env python3
"""
Generate a secure JWT secret key
"""

import secrets
import string

def generate_jwt_secret(length=64):
    """Generate a secure random string for JWT secret"""
    # Use a combination of letters, digits, and some special characters
    alphabet = string.ascii_letters + string.digits + "!@#$%^&*()-_=+[]{}|;:,.<>?"
    return ''.join(secrets.choice(alphabet) for _ in range(length))

def generate_simple_secret(length=64):
    """Generate a URL-safe base64 string"""
    return secrets.token_urlsafe(length)

if __name__ == "__main__":
    print("JWT Secret Key Options:")
    print("=" * 50)
    
    print("\n1. Complex secret with special characters:")
    complex_secret = generate_jwt_secret(64)
    print(f"JWT_SECRET_KEY={complex_secret}")
    
    print("\n2. URL-safe base64 secret:")
    simple_secret = generate_simple_secret(48)
    print(f"JWT_SECRET_KEY={simple_secret}")
    
    print("\n3. Very long secure secret:")
    long_secret = generate_jwt_secret(128)
    print(f"JWT_SECRET_KEY={long_secret}")
    
    print("\n" + "=" * 50)
    print("Choose any one of the above and replace in your .env file")
    print("The longer the key, the more secure it is.")
