from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import (
    Column, Integer, String, DateTime, Text, func
)

Base = declarative_base()

class UserInteraction(Base):
    __tablename__ = 'user_interactions'
    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, index=True, nullable=False)
    product_id = Column(Integer, index=True, nullable=False)
    timestamp = Column(DateTime, server_default=func.now())

class Product(Base):
    __tablename__ = 'products'
    id = Column(Integer, primary_key=True)
    name = Column(String(255), nullable=False)
    description = Column(Text, nullable=False)
    vector = Column(Text, nullable=False)  # JSON-encoded list of floats

class UserEmbedding(Base):
    __tablename__ = 'user_embeddings'
    user_id = Column(Integer, primary_key=True)
    vector = Column(Text, nullable=False) # JSON-encoded list of floats
