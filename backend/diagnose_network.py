import subprocess
import socket
import requests
import time

print("=== ANDROID EMULATOR CONNECTIVITY DIAGNOSTIC ===\n")

print("1. Checking network interfaces...")
try:
    hostname = socket.gethostname()
    local_ip = socket.gethostbyname(hostname)
    print(f"Computer name: {hostname}")
    print(f"Local IP: {local_ip}")
except Exception as e:
    print(f"Error getting network info: {e}")

print("\n2. Testing server accessibility...")
test_urls = [
    ("Localhost", "http://localhost:8000"),
    ("127.0.0.1", "http://127.0.0.1:8000"),
    ("0.0.0.0", "http://0.0.0.0:8000"),
    ("Local IP", f"http://{local_ip}:8000"),
    ("Android Emulator", "http://10.0.2.2:8000")
]

for name, url in test_urls:
    try:
        response = requests.get(f"{url}/", timeout=2)
        print(f"✅ {name} ({url}): Status {response.status_code}")
    except requests.ConnectionError:
        print(f"❌ {name} ({url}): Connection failed")
    except requests.Timeout:
        print(f"⏱️ {name} ({url}): Timeout")
    except Exception as e:
        print(f"❌ {name} ({url}): Error - {e}")

print("\n3. Testing profile endpoint specifically...")
test_data = {"full_name": "Test User"}

for name, base_url in [("Localhost", "http://localhost:8000"), ("Android URL", "http://10.0.2.2:8000")]:
    try:
        response = requests.put(
            f"{base_url}/users/profile",
            json=test_data,
            headers={"Content-Type": "application/json"},
            timeout=3
        )
        print(f"✅ {name} profile endpoint: Status {response.status_code}")
    except requests.ConnectionError:
        print(f"❌ {name} profile endpoint: Connection failed")
    except Exception as e:
        print(f"❌ {name} profile endpoint: Error - {e}")

print("\n4. Checking if firewall is blocking...")
try:
    result = subprocess.run(['netstat', '-an'], capture_output=True, text=True, timeout=5)
    if ':8000' in result.stdout:
        print("✅ Port 8000 is listening")
    else:
        print("❌ Port 8000 is not visible in netstat")
except Exception as e:
    print(f"❌ Could not check netstat: {e}")

print("\n=== RECOMMENDATIONS ===")
print("If Android emulator can't reach 10.0.2.2:8000:")
print("1. Make sure the server is running with host 0.0.0.0:8000")
print("2. Check Windows Firewall settings for port 8000")
print("3. Try starting server with: uvicorn app.main:app --host 0.0.0.0 --port 8000")
print("4. Alternative: Use the actual local IP address in Android app")
