"""
Performance and Load Testing for AI Service
"""

import requests
import time
import statistics
from datetime import datetime
from concurrent.futures import ThreadPoolExecutor, as_completed

BASE_URL = "http://localhost:8000"

def test_single_request():
    """Test single request latency"""
    payload = {
        "ipAddress": "192.168.1.100",
        "userAgent": "Mozilla/5.0",
        "method": "GET",
        "endpoint": "/api/products",
        "requestPath": "/api/products",
        "requestCount": 5,
        "timestamp": datetime.now().isoformat()
    }

    start = time.time()
    response = requests.post(f"{BASE_URL}/predict", json=payload)
    latency = (time.time() - start) * 1000  # Convert to ms

    return latency, response.status_code == 200

def performance_test(num_requests=100):
    """Run performance test"""
    print(f"\n🚀 Performance Test: {num_requests} sequential requests\n")

    latencies = []
    successes = 0

    for i in range(num_requests):
        latency, success = test_single_request()
        latencies.append(latency)
        if success:
            successes += 1

        if (i + 1) % 20 == 0:
            print(f"Progress: {i + 1}/{num_requests}")

    print(f"\n📊 Results:")
    print(f"   Total Requests: {num_requests}")
    print(f"   Successful: {successes}")
    print(f"   Failed: {num_requests - successes}")
    print(f"   Success Rate: {(successes/num_requests)*100:.1f}%")
    print(f"\n⏱️  Latency Statistics:")
    print(f"   Min: {min(latencies):.2f}ms")
    print(f"   Max: {max(latencies):.2f}ms")
    print(f"   Average: {statistics.mean(latencies):.2f}ms")
    print(f"   Median: {statistics.median(latencies):.2f}ms")
    print(f"   95th percentile: {statistics.quantiles(latencies, n=20)[18]:.2f}ms")

def load_test(num_requests=100, num_workers=10):
    """Run concurrent load test"""
    print(f"\n🔥 Load Test: {num_requests} requests with {num_workers} concurrent workers\n")

    latencies = []
    successes = 0
    start_time = time.time()

    with ThreadPoolExecutor(max_workers=num_workers) as executor:
        futures = [executor.submit(test_single_request) for _ in range(num_requests)]

        for future in as_completed(futures):
            latency, success = future.result()
            latencies.append(latency)
            if success:
                successes += 1

    total_time = time.time() - start_time
    throughput = num_requests / total_time

    print(f"\n📊 Results:")
    print(f"   Total Requests: {num_requests}")
    print(f"   Successful: {successes}")
    print(f"   Failed: {num_requests - successes}")
    print(f"   Success Rate: {(successes/num_requests)*100:.1f}%")
    print(f"   Total Time: {total_time:.2f}s")
    print(f"   Throughput: {throughput:.2f} req/s")
    print(f"\n⏱️  Latency Statistics:")
    print(f"   Min: {min(latencies):.2f}ms")
    print(f"   Max: {max(latencies):.2f}ms")
    print(f"   Average: {statistics.mean(latencies):.2f}ms")
    print(f"   Median: {statistics.median(latencies):.2f}ms")

if __name__ == "__main__":
    print("═" * 60)
    print("AI SERVICE PERFORMANCE TESTING")
    print("═" * 60)

    # Sequential performance test
    performance_test(100)

    # Concurrent load test
    load_test(200, 20)

    print("\n✅ Performance testing complete!\n")