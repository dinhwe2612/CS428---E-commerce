package com.catalog.catalog_service.repository.es;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.catalog.catalog_service.model.ProductDocument;

public interface ProductSearchRepository
    extends ElasticsearchRepository<ProductDocument, Long> {

    @Query("""
    {
      "bool": {
        "should": [
          {
            "match_phrase_prefix": {
              "name": {
                "query": "?0",
                "max_expansions": 50
              }
            }
          },
          {
            "wildcard": {
              "name.keyword": {
                "value": "?0*"
              }
            }
          },
          {
            "match_phrase_prefix": {
              "description": {
                "query": "?0"
              }
            }
          },
          {
            "match_phrase_prefix": {
              "categoryName": {
                "query": "?0"
              }
            }
          }
        ],
        "minimum_should_match": 1
      }
    }
    """)
    List<ProductDocument> searchSuggestions(String query);
}
