FROM python:3.9 AS builder
WORKDIR /build

COPY chatbot-service/requirements.txt .
RUN --mount=type=cache,target=/root/.cache/pip \
    pip install --upgrade pip && \
    pip install --prefix=/install -r requirements.txt

ENV PATH="/install/bin:${PATH}"
ENV PYTHONPATH="/install/lib/python3.9/site-packages:${PYTHONPATH}"

COPY chatbot-service/ .

RUN rasa train --fixed-model-name flower-bot.tar.gz \
               --data data \
               --config config.yml \
               --domain domain.yml

FROM python:3.9-slim
WORKDIR /app

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

COPY --from=builder /install /usr/local
COPY chatbot-service/ .
COPY --from=builder /build/models/flower-bot.tar.gz ./models/
EXPOSE 5005 5055

COPY chatbot-service/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
