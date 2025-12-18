package com.smatiz.microrest;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryGeneratorTest {

    @Test
    public void testGenerateSelectQuery() {
        struct_page page = new struct_page();
        page.setSchema("public");
        page.setTable("users");
        Map<String, String> params = new HashMap<>();
        params.put("id", "1");
        page.setParams(params);

        String query = QueryGenerator.generateSelectQuery(page);
        assertEquals("SELECT * FROM public.users WHERE id = ?", query);
    }

    @Test
    public void testGenerateInsertQuery() {
        struct_page page = new struct_page();
        page.setSchema("public");
        page.setTable("users");
        Map<String, String> params = new HashMap<>();
        params.put("name", "test");
        params.put("email", "test@test.com");
        page.setParams(params);

        String query = QueryGenerator.generateInsertQuery(page);
        assertEquals("INSERT INTO public.users (name,email) VALUES (?,?)", query);
    }

    @Test
    public void testGenerateUpdateQuery() {
        struct_page page = new struct_page();
        page.setSchema("public");
        page.setTable("users");
        Map<String, String> params = new HashMap<>();
        params.put("id", "1");
        page.setParams(params);
        Map<String, String> body = new HashMap<>();
        body.put("name", "test2");
        page.setBody(body.toString());


        String query = QueryGenerator.generateUpdateQuery(page);
        assertEquals("UPDATE public.users SET name = ? WHERE id = ?", query);
    }

    @Test
    public void testGenerateDeleteQuery() {
        struct_page page = new struct_page();
        page.setSchema("public");
        page.setTable("users");
        Map<String, String> params = new HashMap<>();
        params.put("id", "1");
        page.setParams(params);

        String query = QueryGenerator.generateDeleteQuery(page);
        assertEquals("DELETE FROM public.users WHERE id = ?", query);
    }
}
