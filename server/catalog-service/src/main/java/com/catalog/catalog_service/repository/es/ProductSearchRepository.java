package com.catalog.catalog_service.repository.es;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.catalog.catalog_service.model.ProductDocument;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {
    
    @Query("""
        {
            "bool": {
                "should": [
                    { "match": { "name": { "query": "?0", "fuzziness": "AUTO" } } },
                    { "match": { "description": { "query": "?0", "fuzziness": "AUTO" } } },
                    { "match": { "categoryName": { "query": "?0", "fuzziness": "AUTO" } } }
                ],
                "minimum_should_match": 1
            }
        }
    """)
    List<ProductDocument> searchByNameOrDescription(String query);
    
    @Query("""
        {
            "bool": {
                "should": [
                    { "match_phrase_prefix": { "name": "?0" } },
                    { "match_phrase_prefix": { "description": "?0" } },
                    { "match_phrase_prefix": { "categoryName": "?0" } }
                ],
                "minimum_should_match": 1
            }
        }
    """)
    List<ProductDocument> searchSuggestions(String query);
} 