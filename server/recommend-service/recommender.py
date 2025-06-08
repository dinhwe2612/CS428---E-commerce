import os
import threading
from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException, Query, BackgroundTasks
import py_eureka_client.eureka_client as eureka_client
import uvicorn

from recommend import RecommendService
from consumer import Consumer

#–– Load environment ––
load_dotenv()
EUREKA_SERVER = os.getenv("EUREKA_SERVER_URL", "http://eureka-server:8761/eureka")
APP_NAME = os.getenv("EUREKA_APP_NAME", "RECOMMEND-SERVICE")
PORT = int(os.getenv("RECOMMEND_PORT", 5000))
INSTANCE_IP = os.getenv("RECOMMEND_INSTANCE_IP", "recommend-service")
INSTANCE_HOST = os.getenv("RECOMMEND_INSTANCE_HOST", "recommend-service")

DB_URL = (
    f"mysql+pymysql://{os.getenv('DB_USER')}:{os.getenv('DB_PASSWORD')}@"
    f"{os.getenv('DB_HOST')}:{os.getenv('DB_PORT')}/{os.getenv('DB_NAME')}"
)
RABBITMQ_URL = os.getenv('RABBITMQ_URL')

# Instantiate the shared RecommendService
service = RecommendService(db_url=DB_URL,
                            index_path='item_index.ann',
                            tree_count=10,
                            alpha=0.7)
# Instantiate the Consumer with the shared service
consumer = Consumer(service=service,
                    rabbitmq_url=RABBITMQ_URL)

#–– Create FastAPI app ––
app = FastAPI()

#–– Startup: register with Eureka and start consumer ––
@app.on_event("startup")
async def startup_event():
    # Register service in Eureka
    await eureka_client.init_async(
        eureka_server=EUREKA_SERVER,
        app_name=APP_NAME,
        instance_port=PORT,
        instance_ip=INSTANCE_IP,
        instance_host=INSTANCE_HOST
    )
    # Start RabbitMQ consumer in background thread
    thread = threading.Thread(target=consumer.start, daemon=True)
    thread.start()

#–– Recommendation endpoints ––
@app.get("/user/{user_id}")
def recommend_for_user(user_id: int, k: int = Query(10, ge=1, le=100)):
    products = service.recommend_for_user(user_id, k)
    return {"products": products}

@app.get("/product/{product_id}")
def recommend_similar_item(product_id: int, k: int = Query(10, ge=1, le=100)):
    try:
        products = service.recommend_similar(product_id, k)
        return {"products": products}
    except KeyError:
        raise HTTPException(404, "Product not in index")

@app.get("/health")
def health():
    return {"status": "UP"}

#–– Run server when invoked as main ––
if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=PORT)