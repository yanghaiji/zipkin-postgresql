/**
 * Copyright ${license.git.copyrightYears} The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.javayh.zipkin.storage.postgresql;

import com.javayh.zipkin.storage.postgresql.internal.generated.tables.ZipkinAnnotations;
import com.javayh.zipkin.storage.postgresql.internal.generated.tables.ZipkinSpans;
import org.jooq.*;
import org.jooq.impl.DSL;
import zipkin.internal.Pair;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.jooq.impl.DSL.row;
import static com.javayh.zipkin.storage.postgresql.internal.generated.Tables.ZIPKIN_DEPENDENCIES;


final class Schema {
    final List<Field<?>> spanIdFields;
    final List<Field<?>> spanFields;
    final List<Field<?>> annotationFields;
    final List<Field<?>> dependencyLinkerFields;
    final List<Field<?>> dependencyLinkerGroupByFields;
    final List<Field<?>> dependencyLinkFields;
    final boolean hasTraceIdHigh;
    final boolean hasPreAggregatedDependencies;
    final boolean hasIpv6;
    final boolean hasErrorCount;
    final boolean strictTraceId;

    Schema(DataSource datasource, DSLContexts context, boolean strictTraceId) {
        hasTraceIdHigh = HasTraceIdHigh.test(datasource, context);
        hasPreAggregatedDependencies = HasPreAggregatedDependencies.test(datasource, context);
        hasIpv6 = HasIpv6.test(datasource, context);
        hasErrorCount = HasErrorCount.test(datasource, context);
        this.strictTraceId = strictTraceId;

        spanIdFields = list(ZipkinSpans.ZIPKIN_SPANS.TRACE_ID_HIGH, ZipkinSpans.ZIPKIN_SPANS.TRACE_ID);
        spanFields = list(ZipkinSpans.ZIPKIN_SPANS.fields());
        annotationFields = list(ZipkinAnnotations.ZIPKIN_ANNOTATIONS.fields());
        dependencyLinkFields = list(ZIPKIN_DEPENDENCIES.fields());
        dependencyLinkerFields = list(
                ZipkinSpans.ZIPKIN_SPANS.TRACE_ID_HIGH, ZipkinSpans.ZIPKIN_SPANS.TRACE_ID, ZipkinSpans.ZIPKIN_SPANS.PARENT_ID,
                ZipkinSpans.ZIPKIN_SPANS.ID, ZipkinAnnotations.ZIPKIN_ANNOTATIONS.A_KEY, ZipkinAnnotations.ZIPKIN_ANNOTATIONS.A_TYPE,
                ZipkinAnnotations.ZIPKIN_ANNOTATIONS.ENDPOINT_SERVICE_NAME, ZipkinSpans.ZIPKIN_SPANS.PARENT_ID
        );
        dependencyLinkerGroupByFields = new ArrayList<>(dependencyLinkerFields);
        dependencyLinkerGroupByFields.remove(ZipkinSpans.ZIPKIN_SPANS.PARENT_ID);
        if (!hasTraceIdHigh) {
            spanIdFields.remove(ZipkinSpans.ZIPKIN_SPANS.TRACE_ID_HIGH);
            spanFields.remove(ZipkinSpans.ZIPKIN_SPANS.TRACE_ID_HIGH);
            annotationFields.remove(ZipkinAnnotations.ZIPKIN_ANNOTATIONS.TRACE_ID_HIGH);
            dependencyLinkerFields.remove(ZipkinSpans.ZIPKIN_SPANS.TRACE_ID_HIGH);
            dependencyLinkerGroupByFields.remove(ZipkinSpans.ZIPKIN_SPANS.TRACE_ID_HIGH);
        }
        if (!hasIpv6) {
            annotationFields.remove(ZipkinAnnotations.ZIPKIN_ANNOTATIONS.ENDPOINT_IPV6);
        }
        if (!hasErrorCount) {
            dependencyLinkFields.remove(ZIPKIN_DEPENDENCIES.ERROR_COUNT);
        }
    }

    Condition joinCondition(ZipkinAnnotations annotationTable) {
        if (hasTraceIdHigh) {
            return ZipkinSpans.ZIPKIN_SPANS.TRACE_ID_HIGH.eq(annotationTable.TRACE_ID_HIGH)
                    .and(ZipkinSpans.ZIPKIN_SPANS.TRACE_ID.eq(annotationTable.TRACE_ID))
                    .and(ZipkinSpans.ZIPKIN_SPANS.ID.eq(annotationTable.SPAN_ID));
        } else {
            return ZipkinSpans.ZIPKIN_SPANS.TRACE_ID.eq(annotationTable.TRACE_ID)
                    .and(ZipkinSpans.ZIPKIN_SPANS.ID.eq(annotationTable.SPAN_ID));
        }
    }

    /**
     * Returns a mutable list
     */
    static <T> List<T> list(T... elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }

    Condition spanTraceIdCondition(SelectOffsetStep<? extends Record> traceIdQuery) {
        if (hasTraceIdHigh && strictTraceId) {
            Result<? extends Record> result = traceIdQuery.fetch();
            List<Row2<Long, Long>> traceIds = new ArrayList<>(result.size());
            for (Record r : result) {
                traceIds.add(DSL.row(r.get(ZipkinSpans.ZIPKIN_SPANS.TRACE_ID_HIGH), r.get(ZipkinSpans.ZIPKIN_SPANS.TRACE_ID)));
            }
            return row(ZipkinSpans.ZIPKIN_SPANS.TRACE_ID_HIGH, ZipkinSpans.ZIPKIN_SPANS.TRACE_ID).in(traceIds);
        } else {
            List<Long> traceIds = traceIdQuery.fetch(ZipkinSpans.ZIPKIN_SPANS.TRACE_ID);
            return ZipkinSpans.ZIPKIN_SPANS.TRACE_ID.in(traceIds);
        }
    }

    Condition spanTraceIdCondition(Long traceIdHigh, long traceIdLow) {
        return traceIdHigh != null && hasTraceIdHigh
                ? row(ZipkinSpans.ZIPKIN_SPANS.TRACE_ID_HIGH, ZipkinSpans.ZIPKIN_SPANS.TRACE_ID).eq(traceIdHigh, traceIdLow)
                : ZipkinSpans.ZIPKIN_SPANS.TRACE_ID.eq(traceIdLow);
    }

    Condition annotationsTraceIdCondition(Set<Pair<Long>> traceIds) {
        boolean hasTraceIdHigh = false;
        for (Pair<Long> traceId : traceIds) {
            if (traceId._1 != 0) {
                hasTraceIdHigh = true;
                break;
            }
        }
        if (hasTraceIdHigh && strictTraceId) {
            Row2[] result = new Row2[traceIds.size()];
            int i = 0;
            for (Pair<Long> traceId128 : traceIds) {
                result[i++] = row(traceId128._1, traceId128._2);
            }
            return row(ZipkinAnnotations.ZIPKIN_ANNOTATIONS.TRACE_ID_HIGH, ZipkinAnnotations.ZIPKIN_ANNOTATIONS.TRACE_ID).in(result);
        } else {
            Long[] result = new Long[traceIds.size()];
            int i = 0;
            for (Pair<Long> traceId128 : traceIds) {
                result[i++] = traceId128._2;
            }
            return ZipkinAnnotations.ZIPKIN_ANNOTATIONS.TRACE_ID.in(result);
        }
    }
}
