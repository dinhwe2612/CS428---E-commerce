import os
import json
import pika
from dotenv import load_dotenv
from recommend import RecommendService

class Consumer:
    def __init__(self, service: RecommendService, rabbitmq_url: str = None):
        # Load environment to get RABBITMQ_URL if not provided
        load_dotenv()
        self.service = service
        self.rabbitmq_url = rabbitmq_url or os.getenv('RABBITMQ_URL')

    def start(self):
        # Establish connection to RabbitMQ
        params = pika.URLParameters(self.rabbitmq_url)
        connection = pika.BlockingConnection(params)
        channel = connection.channel()

        # 1) User interaction events
        channel.exchange_declare(
            exchange='recommend.topic', exchange_type='topic', durable=True
        )
        channel.queue_declare(queue='recommend.interact', durable=True)
        channel.queue_bind(
            exchange='recommend.topic',
            queue='recommend.interact',
            routing_key='#.interact'
        )
        channel.basic_consume(
            queue='recommend.interact',
            on_message_callback=self._on_interact,
            auto_ack=True
        )

        # 2) Product CRUD events
        channel.exchange_declare(
            exchange='product.topic', exchange_type='topic', durable=True
        )
        channel.queue_declare(queue='recommend.product', durable=True)
        channel.queue_bind(
            exchange='product.topic',
            queue='recommend.product',
            routing_key='product.*'
        )
        channel.basic_consume(
            queue='recommend.product',
            on_message_callback=self._on_product,
            auto_ack=True
        )

        print("Consumer started: waiting for RabbitMQ messages...")
        channel.start_consuming()

    def _on_interact(self, ch, method, props, body):
        # Parse user interaction and delegate to service
        data = json.loads(body)
        self.service.user_interact_callback(
            user_id=int(data['userId']),
            product_id=int(data['productId']),
            timestamp=data.get('timestamp')
        )

    def _on_product(self, ch, method, props, body):
        # Parse product event based on routing key
        data = json.loads(body)
        routing_key = method.routing_key
        pid = int(data.get('id'))
        if routing_key == 'product.deleted':
            self.service.product_deleted_callback(product_id=pid)
        else:
            self.service.product_created_updated_callback(
                product_id=pid,
                name=data.get('name', ''),
                description=data.get('description', '')
            )