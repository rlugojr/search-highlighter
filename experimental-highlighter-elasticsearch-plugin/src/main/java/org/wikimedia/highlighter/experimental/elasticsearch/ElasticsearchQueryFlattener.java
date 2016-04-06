package org.wikimedia.highlighter.experimental.elasticsearch;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.Query;
import org.elasticsearch.common.lucene.search.function.FiltersFunctionScoreQuery;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.wikimedia.highlighter.experimental.lucene.QueryFlattener;

public class ElasticsearchQueryFlattener extends QueryFlattener {
    /**
     * Default configuration.
     */
    public ElasticsearchQueryFlattener() {
        super();
    }

    public ElasticsearchQueryFlattener(int maxMultiTermQueryTerms, boolean phraseAsTerms, boolean removeHighFrequencyTermsFromCommonTerms) {
        super(maxMultiTermQueryTerms, phraseAsTerms, removeHighFrequencyTermsFromCommonTerms);
    }

    @Override
    protected boolean flattenUnknown(Query query, float pathBoost, Object sourceOverride,
            IndexReader reader, Callback callback) {
        if (query instanceof FilteredQuery) {
            flattenQuery((FilteredQuery) query, pathBoost, sourceOverride, reader, callback);
            return true;
        }

        if (query instanceof FunctionScoreQuery) {
            flattenQuery((FunctionScoreQuery) query, pathBoost, sourceOverride, reader,
                    callback);
            return true;
        }
        if (query instanceof FiltersFunctionScoreQuery) {
            flattenQuery((FiltersFunctionScoreQuery) query, pathBoost, sourceOverride, reader,
                    callback);
            return true;
        }
        return false;
    }

    protected void flattenQuery(FilteredQuery query, float pathBoost, Object sourceOverride,
            IndexReader reader, Callback callback) {
        if (query.getQuery() != null) {
            flatten(query.getQuery(), pathBoost * query.getBoost(), sourceOverride, reader,
                    callback);
        }
        // TODO maybe flatten filter like Elasticsearch does
    }

    protected void flattenQuery(FunctionScoreQuery query, float pathBoost,
            Object sourceOverride, IndexReader reader, Callback callback) {
        if (query.getSubQuery() != null) {
            flatten(query.getSubQuery(), pathBoost * query.getBoost(), sourceOverride, reader, callback);
        }
    }

    protected void flattenQuery(FiltersFunctionScoreQuery query, float pathBoost,
            Object sourceOverride, IndexReader reader, Callback callback) {
        if (query.getSubQuery() != null) {
            flatten(query.getSubQuery(), pathBoost * query.getBoost(), sourceOverride, reader, callback);
        }
    }
}
