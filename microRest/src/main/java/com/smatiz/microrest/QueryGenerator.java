package com.smatiz.microrest;

import java.util.Map;

public class QueryGenerator {

    public static String generateSelectQuery(struct_page page) {
        String schema = page.getSchema();
        String table = page.getTable();
        Map<String, String> params = page.getParams();

        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(schema).append(".").append(table);

        if (!params.isEmpty()) {
            query.append(" WHERE ");
            int i = 0;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                query.append(entry.getKey()).append(" = ?");
                if (i < params.size() - 1) {
                    query.append(" AND ");
                }
                i++;
            }
        }

        return query.toString();
    }

    public static String generateInsertQuery(struct_page page) {
        String schema = page.getSchema();
        String table = page.getTable();
        Map<String, String> body = page.getParams();

        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(schema).append(".").append(table).append(" (");

        StringBuilder values = new StringBuilder(") VALUES (");

        int i = 0;
        for (Map.Entry<String, String> entry : body.entrySet()) {
            query.append(entry.getKey());
            values.append("?");
            if (i < body.size() - 1) {
                query.append(",");
                values.append(",");
            }
            i++;
        }

        query.append(values).append(")");

        return query.toString();
    }

    public static String generateUpdateQuery(struct_page page) {
        String schema = page.getSchema();
        String table = page.getTable();
        Map<String, String> body = page.getBodyAsMap();
        Map<String, String> params = page.getParams();

        StringBuilder query = new StringBuilder("UPDATE ");
        query.append(schema).append(".").append(table).append(" SET ");

        int i = 0;
        for (Map.Entry<String, String> entry : body.entrySet()) {
            query.append(entry.getKey()).append(" = ?");
            if (i < body.size() - 1) {
                query.append(",");
            }
            i++;
        }

        if (!params.isEmpty()) {
            query.append(" WHERE ");
            i = 0;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                query.append(entry.getKey()).append(" = ?");
                if (i < params.size() - 1) {
                    query.append(" AND ");
                }
                i++;
            }
        }

        return query.toString();
    }

    public static String generateDeleteQuery(struct_page page) {
        String schema = page.getSchema();
        String table = page.getTable();
        Map<String, String> params = page.getParams();

        StringBuilder query = new StringBuilder("DELETE FROM ");
        query.append(schema).append(".").append(table);

        if (!params.isEmpty()) {
            query.append(" WHERE ");
            int i = 0;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                query.append(entry.getKey()).append(" = ?");
                if (i < params.size() - 1) {
                    query.append(" AND ");
                }
                i++;
            }
        }

        return query.toString();
    }
}
