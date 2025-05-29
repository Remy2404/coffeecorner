# Coffee Shop API Deployment Guide

## Production Deployment Issues & Solutions

### Current Issues Fixed:

1. **Supabase Client Proxy Error**
   - Fixed: Removed `proxy` parameter from Supabase client initialization
   - The newer Supabase Python client doesn't accept this parameter

2. **Port Binding Issue**
   - Fixed: Updated main.py to use `PORT` environment variable
   - Server now binds to `0.0.0.0:${PORT}` instead of `127.0.0.1:8000`

3. **Production Environment Variables**
   - Added better logging for environment variable status
   - Improved error handling for missing Supabase credentials

### Environment Variables Required:

```bash
# Supabase Configuration
SUPABASE_URL=your_supabase_url
SUPABASE_ANON_KEY=your_anon_key
SUPABASE_SERVICE_ROLE_KEY=your_service_role_key  # Optional but recommended

# Firebase Configuration
FIREBASE_PROJECT_ID=coffee-corner-2697f
FIREBASE_ADMIN_SDK_PATH=firebase-admin-sdk.json

# JWT Configuration
JWT_SECRET_KEY=your_jwt_secret_key
JWT_ALGORITHM=HS256
JWT_EXPIRE_HOURS=24

# Application Configuration
DEBUG=false
PORT=8000  # Will be set automatically by hosting platform
```

### Deployment Commands:

1. **Using main.py (recommended for platforms like Render)**:
   ```bash
   python app/main.py
   ```

2. **Using start.py (production script)**:
   ```bash
   python start.py
   ```

3. **Using uvicorn directly**:
   ```bash
   uvicorn app.main:app --host 0.0.0.0 --port $PORT
   ```

### Health Check Endpoints:

- `GET /` - Basic API info
- `GET /health` - Health status check
- `GET /debug/status` - Detailed system status (includes database connectivity)

### Common Issues:

1. **"No open ports detected"**
   - Ensure server binds to `0.0.0.0:$PORT`, not `127.0.0.1:8000`
   - Fixed in current version

2. **Supabase Connection Failed**
   - Check if SUPABASE_URL and SUPABASE_ANON_KEY are set correctly
   - App will run with mock client if Supabase fails (no database access)

3. **Firebase Auth Issues**
   - Ensure firebase-admin-sdk.json is uploaded to production
   - Check FIREBASE_PROJECT_ID matches your Firebase project

### Testing Deployment:

1. Check root endpoint: `curl https://your-domain.com/`
2. Check health: `curl https://your-domain.com/health`
3. Check debug status: `curl https://your-domain.com/debug/status`
