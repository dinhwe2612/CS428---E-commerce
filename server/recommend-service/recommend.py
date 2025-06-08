import os
import json
from datetime import datetime
import numpy as np
from dotenv import load_dotenv
from sqlalchemy import create_engine, func
from sqlalchemy.orm import sessionmaker
from sentence_transformers import SentenceTransformer
from annoy import AnnoyIndex

from models import UserInteraction, Product, UserEmbedding, Base

class RecommendService:
    def __init__(
        self,
        db_url: str,
        index_path: str = 'item_index.ann',
        tree_count: int = 10,
        alpha: float = 0.8,
    ):
        load_dotenv()

        # DB
        self.engine = create_engine(db_url, pool_pre_ping=True)
        Base.metadata.create_all(self.engine)
        self.Session = sessionmaker(bind=self.engine)

        # Params
        self.alpha = alpha
        self.index_path = index_path
        self.tree_count = tree_count

        # Embeddings
        self.model = SentenceTransformer('all-MiniLM-L6-v2')
        self.DIM = self.model.get_sentence_embedding_dimension()

        # Load or build Annoy index
        self.ann = AnnoyIndex(self.DIM, 'angular')
        if os.path.exists(self.index_path):
            self.ann.load(self.index_path)
        else:
            self._build_index()

    def _load_item_embeddings(self):
        session = self.Session()
        embeddings = {}
        for p in session.query(Product).all():
            emb = np.array(json.loads(p.vector))
            embeddings[p.id] = emb
        session.close()
        return embeddings

    def _build_index(self):
        # rebuild from scratch
        embeddings = self._load_item_embeddings()
        new_ann = AnnoyIndex(self.DIM, 'angular')
        for pid, vec in embeddings.items():
            new_ann.add_item(pid, vec)
        new_ann.build(self.tree_count)
        new_ann.save(self.index_path)
        self.ann = new_ann

    def recommend_for_user(self, user_id: int, k: int = 10) -> list[int]:
        session = self.Session()
        ue = session.get(UserEmbedding, user_id)
        if ue:
            vec = np.array(json.loads(ue.vector))
            ids = self.ann.get_nns_by_vector(vec, k)
            session.close()
            return ids
        # cold-start: most popular products
        # select product ID and sort by interaction count on left join
        rows = session.query(
            Product.id,
            func.count(UserInteraction.id).label('interaction_count')
        ).outerjoin(
            UserInteraction, Product.id == UserInteraction.product_id
        ).filter(
            Product.id.isnot(None)
        ).group_by(
            Product.id
        ).order_by(
            func.count(UserInteraction.id).desc()
        ).limit(k).all()
        session.close()
        return [pid for pid, _ in rows]

    def recommend_similar(self, product_id: int, k: int = 10) -> list[int]:
        try:
            return self.ann.get_nns_by_item(product_id, k)
        except Exception:
            raise KeyError(f"Product {product_id} not in index")

    def user_interact_callback(self, user_id: int, product_id: int, timestamp: str = None):
        ts = timestamp or datetime.utcnow().isoformat()
        session = self.Session()

        # 1) record the interaction
        ui = UserInteraction(user_id=user_id, product_id=product_id, timestamp=ts)
        session.add(ui)

        # 2) update the user embedding
        prod = session.get(Product, product_id)
        if prod and prod.vector:
            item_vec = np.array(json.loads(prod.vector))
            ue = session.get(UserEmbedding, user_id)
            if ue:
                old = np.array(json.loads(ue.vector))
                new = self.alpha * old + (1 - self.alpha) * item_vec
            else:
                new = item_vec
            ue = UserEmbedding(user_id=user_id, vector=json.dumps(new.tolist()))
            session.merge(ue)

        session.commit()
        session.close()

    def product_created_updated_callback(self, product_id: int, name: str, description: str):
        session = self.Session()

        # upsert Product record
        vec = self.model.encode(f"{name} {description}").tolist()
        p = Product(
            id=product_id,
            name=name,
            description=description,
            vector=json.dumps(vec)
        )
        session.merge(p)
        session.commit()
        session.close()

        # rebuild index
        self._build_index()

    def product_deleted_callback(self, product_id: int):
        session = self.Session()
        session.query(Product).filter(Product.id == product_id).delete()
        session.commit()
        session.close()

        # rebuild index
        self._build_index()