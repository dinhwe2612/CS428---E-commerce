import os, json
from datetime import datetime
import pika

RABBITMQ_URL = 'amqp://guest:guest@localhost:5672/'

def publish_interaction():
    params = pika.URLParameters(RABBITMQ_URL)
    conn = pika.BlockingConnection(params)
    ch = conn.channel()
    ch.exchange_declare(exchange='recommend.topic', exchange_type='topic', durable=True)

    msg = {
        'userId': 1,
        'productId': 3,
        'timestamp': datetime.utcnow().isoformat()
    }
    rk = 'user.interact'  # matches '#.interact'
    ch.basic_publish(
        exchange='recommend.topic',
        routing_key=rk,
        body=json.dumps(msg),
        properties=pika.BasicProperties(delivery_mode=2)
    )
    print(f"→ Sent interaction: {msg}")
    conn.close()

def publish_product_created():
    params = pika.URLParameters(RABBITMQ_URL)
    conn = pika.BlockingConnection(params)
    ch = conn.channel()
    ch.exchange_declare(exchange='product.topic', exchange_type='topic', durable=True)

    msg = {
        'id': 123,
        'name': 'Test Widget',
        'description': 'A widget created for one-time test'
    }
    rk = 'product.created'  # matches your 'product.#.created' binding
    ch.basic_publish(
        exchange='product.topic',
        routing_key=rk,
        body=json.dumps(msg),
        properties=pika.BasicProperties(delivery_mode=2)
    )
    print(f"→ Sent product creation: {msg}")
    conn.close()

if __name__ == '__main__':
    publish_interaction()
    publish_product_created()