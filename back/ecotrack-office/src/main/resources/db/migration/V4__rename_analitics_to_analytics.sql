-- Rename table analitics_report to analytics_report for consistency
-- Legacy migration: remove table with typo if it exists
-- For fresh installs, this does nothing (V1 creates analytics_report correctly)
-- For existing databases with the typo, this cleans it up
DROP TABLE IF EXISTS analitics_report;
