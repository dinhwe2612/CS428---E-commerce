#!/bin/bash
set -e

if [ "$SKIP_EUREKA_CHECK" != "true" ]; then
    echo "Waiting for Eureka server..."
    EUREKA_HOST="eureka-server:8761"
    MAX_RETRIES=30
    RETRY_COUNT=0

    while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
        echo "Checking Eureka server (attempt $((RETRY_COUNT + 1))/$MAX_RETRIES)..."
        
        if curl -f --connect-timeout 5 --max-time 10 "http://$EUREKA_HOST/actuator/health" 2>/dev/null || \
           curl -f --connect-timeout 5 --max-time 10 "http://$EUREKA_HOST/eureka/" 2>/dev/null || \
           curl -f --connect-timeout 5 --max-time 10 "http://$EUREKA_HOST/" 2>/dev/null; then
            echo "Eureka server is ready!"
            break
        fi
        
        RETRY_COUNT=$((RETRY_COUNT + 1))
        if [ $RETRY_COUNT -lt $MAX_RETRIES ]; then
            echo "Eureka not ready yet, waiting 10 seconds..."
            sleep 10
        else
            break
        fi
    done
else
    echo "Skipping Eureka check (SKIP_EUREKA_CHECK=true)"
fi

echo "Starting Eureka client..."
python -c "from eureka_client import eureka_client; eureka_client.start()" &
EUREKA_PID=$!

echo "Starting Rasa Action Server..."
rasa run actions --port 5055 --debug &
ACTION_PID=$!

echo "Waiting for Action Server to start..."
sleep 10

if ! kill -0 $ACTION_PID 2>/dev/null; then
    echo "Action server failed to start"
    exit 1
fi

echo "Starting Rasa Core Server..."
rasa run --enable-api --port 5005 --cors "*" --debug &
RASA_PID=$!

cleanup() {
    echo "Shutting down services..."
    if [ ! -z "$EUREKA_PID" ] && kill -0 $EUREKA_PID 2>/dev/null; then
        python -c "from eureka_client import eureka_client; eureka_client.unregister()"
        kill $EUREKA_PID
    fi
    if [ ! -z "$ACTION_PID" ] && kill -0 $ACTION_PID 2>/dev/null; then
        kill $ACTION_PID
    fi
    if [ ! -z "$RASA_PID" ] && kill -0 $RASA_PID 2>/dev/null; then
        kill $RASA_PID
    fi
}

trap cleanup EXIT

wait $RASA_PID