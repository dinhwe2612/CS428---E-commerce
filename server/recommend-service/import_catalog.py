import os
import json
import pymysql
from dotenv import load_dotenv
from sqlalchemy import create_engine, MetaData, Table, select
from sqlalchemy.orm import sessionmaker

from models import Base, Product

# Make SQLAlchemy use PyMySQL under the hood
pymysql.install_as_MySQLdb()

#–– Load env and build URLs ––
load_dotenv()
RECO_DB_URL = (
    f"mysql+pymysql://{os.getenv('DB_USER')}:{os.getenv('DB_PASSWORD')}"
    f"@{os.getenv('DB_HOST')}:{os.getenv('DB_PORT')}/{os.getenv('DB_NAME')}"
)
CATALOG_DB_URL = os.getenv('CATALOG_DB_URL')
if not CATALOG_DB_URL:
    raise RuntimeError("CATALOG_DB_URL not set in .env")

#–– Engines & sessions ––
reco_engine    = create_engine(RECO_DB_URL, pool_pre_ping=True)
catalog_engine = create_engine(CATALOG_DB_URL, pool_pre_ping=True)
SessionReco    = sessionmaker(bind=reco_engine)
session_reco   = SessionReco()

#–– Ensure reco tables exist ––
Base.metadata.create_all(reco_engine)

#–– Reflect catalog’s products table ––
metadata         = MetaData()
catalog_products = Table('products', metadata, autoload_with=catalog_engine)

#–– Load your embedding model ––
from sentence_transformers import SentenceTransformer
model = SentenceTransformer('all-MiniLM-L6-v2')

#–– Fetch catalog rows ––
with catalog_engine.connect() as conn:
    rows = conn.execute(
        select(
            catalog_products.c.id,
            catalog_products.c.name,
            catalog_products.c.description_text
        )
    ).fetchall()

#–– Upsert into reco’s Product table ––
for pid, name, desc_text in rows:
    # 1) compute embedding
    vec = model.encode(f"{name} {desc_text}").tolist()
    # 2) upsert—serialize vector as JSON string
    p = Product(
        id=pid,
        name=name,
        description=desc_text,
        vector=json.dumps(vec)
    )
    session_reco.merge(p)

session_reco.commit()
print(f"Imported {len(rows)} products (with embeddings) into reco DB.")
session_reco.close()
