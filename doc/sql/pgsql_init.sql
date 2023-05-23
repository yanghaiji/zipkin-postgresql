
-- ----------------------------
-- Table structure for zipkin_annotations
-- ----------------------------
DROP TABLE IF EXISTS "public"."zipkin_annotations";
CREATE TABLE "public"."zipkin_annotations" (
  "trace_id_high" int8 NOT NULL DEFAULT 0,
  "trace_id" int8 NOT NULL,
  "span_id" int8 NOT NULL,
  "a_key" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "a_value" bytea,
  "a_type" int4 NOT NULL,
  "a_timestamp" int8,
  "endpoint_ipv4" int4,
  "endpoint_ipv6" bytea,
  "endpoint_port" int2,
  "endpoint_service_name" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."zipkin_annotations"."trace_id_high" IS 'If non zero, this means the trace uses 128 bit traceIds instead of 64 bit';
COMMENT ON COLUMN "public"."zipkin_annotations"."trace_id" IS 'coincides with zipkin_spans.trace_id';
COMMENT ON COLUMN "public"."zipkin_annotations"."span_id" IS 'coincides with zipkin_spans.id';
COMMENT ON COLUMN "public"."zipkin_annotations"."a_key" IS 'BinaryAnnotation.key or Annotation.value if type == -1';
COMMENT ON COLUMN "public"."zipkin_annotations"."a_value" IS 'BinaryAnnotation.value(), which must be smaller than 64KB';
COMMENT ON COLUMN "public"."zipkin_annotations"."a_type" IS 'BinaryAnnotation.type() or -1 if Annotation';
COMMENT ON COLUMN "public"."zipkin_annotations"."a_timestamp" IS 'Used to implement TTL; Annotation.timestamp or zipkin_spans.timestamp';
COMMENT ON COLUMN "public"."zipkin_annotations"."endpoint_ipv4" IS 'Null when Binary/Annotation.endpoint is null';
COMMENT ON COLUMN "public"."zipkin_annotations"."endpoint_ipv6" IS 'Null when Binary/Annotation.endpoint is null, or no IPv6 address';
COMMENT ON COLUMN "public"."zipkin_annotations"."endpoint_port" IS 'Null when Binary/Annotation.endpoint is null';
COMMENT ON COLUMN "public"."zipkin_annotations"."endpoint_service_name" IS 'Null when Binary/Annotation.endpoint is null';

-- ----------------------------
-- Table structure for zipkin_authorities
-- ----------------------------
DROP TABLE IF EXISTS "public"."zipkin_authorities";
CREATE TABLE "public"."zipkin_authorities" (
  "id" int4 NOT NULL DEFAULT nextval('authorities_id_seq'::regclass),
  "username" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "authority" varchar(50) COLLATE "pg_catalog"."default" NOT NULL
)
;

-- ----------------------------
-- Table structure for zipkin_dependencies
-- ----------------------------
DROP TABLE IF EXISTS "public"."zipkin_dependencies";
CREATE TABLE "public"."zipkin_dependencies" (
  "day" date NOT NULL,
  "parent" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "child" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "call_count" int8,
  "error_count" int8
)
;

-- ----------------------------
-- Table structure for zipkin_spans
-- ----------------------------
DROP TABLE IF EXISTS "public"."zipkin_spans";
CREATE TABLE "public"."zipkin_spans" (
  "trace_id_high" int8 NOT NULL DEFAULT 0,
  "trace_id" int8 NOT NULL,
  "id" int8 NOT NULL,
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "parent_id" int8,
  "debug" bool,
  "start_ts" int8,
  "duration" int8
)
;
COMMENT ON COLUMN "public"."zipkin_spans"."trace_id_high" IS 'If non zero, this means the trace uses 128 bit traceIds instead of 64 bit';
COMMENT ON COLUMN "public"."zipkin_spans"."start_ts" IS 'Span.timestamp(): epoch micros used for endTs query and to implement TTL';
COMMENT ON COLUMN "public"."zipkin_spans"."duration" IS 'Span.duration(): micros used for minDuration and maxDuration query';

-- ----------------------------
-- Table structure for zipkin_users
-- ----------------------------
DROP TABLE IF EXISTS "public"."zipkin_users";
CREATE TABLE "public"."zipkin_users" (
  "id" int4 NOT NULL DEFAULT nextval('users_id_seq'::regclass),
  "username" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "password" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "enabled" bool NOT NULL,
  "role" varchar(255) COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Indexes structure for table zipkin_annotations
-- ----------------------------
CREATE INDEX "zipkin_annotations_index_a_key" ON "public"."zipkin_annotations" USING btree (
  "a_key" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "zipkin_annotations_index_a_type" ON "public"."zipkin_annotations" USING btree (
  "a_type" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE INDEX "zipkin_annotations_index_endpoint_service_name" ON "public"."zipkin_annotations" USING btree (
  "endpoint_service_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "zipkin_annotations_index_trace_id_high_trace_id" ON "public"."zipkin_annotations" USING btree (
  "trace_id_high" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "trace_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "zipkin_annotations_index_trace_id_high_trace_id2" ON "public"."zipkin_annotations" USING btree (
  "trace_id_high" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "trace_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "span_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "zipkin_annotations_index_trace_id_high_trace_id_id1" ON "public"."zipkin_annotations" USING btree (
  "trace_id_high" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "trace_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "span_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "a_key" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "a_timestamp" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "zipkin_annotations_index_trace_id_span_id_a_key" ON "public"."zipkin_annotations" USING btree (
  "trace_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "span_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "a_key" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table zipkin_annotations
-- ----------------------------
ALTER TABLE "public"."zipkin_annotations" ADD CONSTRAINT "key_zipkin_annotations_trace_id_high" UNIQUE ("trace_id_high", "trace_id", "span_id", "a_key", "a_timestamp");

-- ----------------------------
-- Primary Key structure for table zipkin_authorities
-- ----------------------------
ALTER TABLE "public"."zipkin_authorities" ADD CONSTRAINT "authorities_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table zipkin_dependencies
-- ----------------------------
ALTER TABLE "public"."zipkin_dependencies" ADD CONSTRAINT "key_zipkin_dependencies_day" UNIQUE ("day", "parent", "child");

-- ----------------------------
-- Indexes structure for table zipkin_spans
-- ----------------------------
CREATE INDEX "zipkin_spans_index_name" ON "public"."zipkin_spans" USING btree (
  "name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "zipkin_spans_index_start_ts" ON "public"."zipkin_spans" USING btree (
  "start_ts" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "zipkin_spans_index_trace_id_high_trace_id" ON "public"."zipkin_spans" USING btree (
  "trace_id_high" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "trace_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "zipkin_spans_index_trace_id_high_trace_id_id" ON "public"."zipkin_spans" USING btree (
  "trace_id_high" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "trace_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "id" "pg_catalog"."int8_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table zipkin_spans
-- ----------------------------
ALTER TABLE "public"."zipkin_spans" ADD CONSTRAINT "key_zipkin_spans_trace_id_high" UNIQUE ("trace_id_high", "trace_id", "id");

-- ----------------------------
-- Primary Key structure for table zipkin_users
-- ----------------------------
ALTER TABLE "public"."zipkin_users" ADD CONSTRAINT "users_pkey" PRIMARY KEY ("id");

-- 初始化用户
INSERT INTO "public"."zipkin_users" ("id", "username", "password", "enabled", "role") VALUES (2, 'yanghaiji', '$2a$10$zbEyyUK7s1K8tcCPtPh7Qe/QFAaN3mYbZ4qya7Roy4vovXIPHB3c6', 't', 'ROLE_ADMIN');
INSERT INTO "public"."zipkin_users" ("id", "username", "password", "enabled", "role") VALUES (3, 'admin', '$2a$10$zbEyyUK7s1K8tcCPtPh7Qe/QFAaN3mYbZ4qya7Roy4vovXIPHB3c6', 't', 'ROLE_ADMIN');
INSERT INTO "public"."zipkin_users" ("id", "username", "password", "enabled", "role") VALUES (4, 'tester', '$2a$10$zbEyyUK7s1K8tcCPtPh7Qe/QFAaN3mYbZ4qya7Roy4vovXIPHB3c6', 't', 'ROLE_ADMIN');
