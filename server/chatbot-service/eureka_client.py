import os
import socket
import time
import threading
import requests
import json
from datetime import datetime

class EurekaClient:
    def __init__(self):
        self.eureka_server = os.getenv('EUREKA_SERVER_URL', 'http://eureka-server:8761/eureka')
        self.app_name = 'CHATBOT-SERVICE'
        self.instance_id = f"{socket.gethostname()}:{self.app_name.lower()}:5005"
        self.host_name = os.getenv('HOSTNAME', socket.gethostname())
        self.ip_address = self.get_local_ip()
        self.port = 5005
        self.health_check_url = f"http://{self.host_name}:{self.port}/webhooks/rest/webhook"
        self.status_page_url = f"http://{self.host_name}:{self.port}/"
        self.home_page_url = f"http://{self.host_name}:{self.port}/"
        
    def get_local_ip(self):
        try:
            s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
            s.connect(("8.8.8.8", 80))
            ip = s.getsockname()[0]
            s.close()
            return ip
        except:
            return "127.0.0.1"
    
    def get_instance_data(self):
        return {
            "instance": {
                "instanceId": self.instance_id,
                "hostName": self.host_name,
                "app": self.app_name,
                "ipAddr": self.ip_address,
                "status": "UP",
                "overriddenstatus": "UNKNOWN",
                "port": {
                    "$": self.port,
                    "@enabled": "true"
                },
                "securePort": {
                    "$": 443,
                    "@enabled": "false"
                },
                "countryId": 1,
                "dataCenterInfo": {
                    "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
                    "name": "MyOwn"
                },
                "healthCheckUrl": self.health_check_url,
                "statusPageUrl": self.status_page_url,
                "homePageUrl": self.home_page_url,
                "metadata": {
                    "management.port": str(self.port),
                    "service.type": "chatbot"
                },
                "vipAddress": self.app_name.lower(),
                "secureVipAddress": self.app_name.lower(),
                "isCoordinatingDiscoveryServer": "false",
                "lastUpdatedTimestamp": str(int(time.time() * 1000)),
                "lastDirtyTimestamp": str(int(time.time() * 1000))
            }
        }
    
    def register(self):
        try:
            url = f"{self.eureka_server}/apps/{self.app_name}"
            headers = {'Content-Type': 'application/json'}
            data = self.get_instance_data()
            
            response = requests.post(url, headers=headers, json=data, timeout=30)
            if response.status_code == 204:
                print(f"Successfully registered {self.app_name} with Eureka")
                return True
            else:
                print(f"Failed to register with Eureka. Status code: {response.status_code}")
                return False
        except Exception as e:
            print(f"Error registering with Eureka: {e}")
            return False
    
    def send_heartbeat(self):
        try:
            url = f"{self.eureka_server}/apps/{self.app_name}/{self.instance_id}"
            response = requests.put(url, timeout=10)
            if response.status_code == 200:
                print(f"Heartbeat sent successfully at {datetime.now()}")
                return True
            else:
                print(f"Heartbeat failed. Status code: {response.status_code}")
                return False
        except Exception as e:
            print(f"Error sending heartbeat: {e}")
            return False
    
    def unregister(self):
        try:
            url = f"{self.eureka_server}/apps/{self.app_name}/{self.instance_id}"
            response = requests.delete(url, timeout=10)
            if response.status_code == 200:
                print(f"Successfully unregistered {self.app_name} from Eureka")
                return True
            else:
                print(f"Failed to unregister from Eureka. Status code: {response.status_code}")
                return False
        except Exception as e:
            print(f"Error unregistering from Eureka: {e}")
            return False
    
    def heartbeat_loop(self):
        while True:
            time.sleep(30)  # Send heartbeat every 30 seconds
            self.send_heartbeat()
    
    def start(self):
        print("Starting Eureka client...")
        
        # Wait for Eureka server to be available
        max_retries = 30
        for i in range(max_retries):
            try:
                response = requests.get(f"{self.eureka_server}/apps", timeout=10)
                if response.status_code == 200:
                    print("Eureka server is available")
                    break
            except:
                pass
            print(f"Waiting for Eureka server... ({i+1}/{max_retries})")
            time.sleep(10)
        
        # Register with Eureka
        if self.register():
            # Start heartbeat thread
            heartbeat_thread = threading.Thread(target=self.heartbeat_loop, daemon=True)
            heartbeat_thread.start()
            print("Eureka client started successfully")
        else:
            print("Failed to start Eureka client")

eureka_client = EurekaClient() 