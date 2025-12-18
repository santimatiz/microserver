package com.smatiz.microrest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class DBProviderTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @Mock
    private Config mockConfig;

    @Mock
    private ResultSetMetaData mockResultSetMetaData;

    private DBProvider dbProvider;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(mockConfig.getConnectionUrl()).thenReturn("jdbc:postgresql://localhost/test");
        when(mockConfig.getDb_user()).thenReturn("user");
        when(mockConfig.getDb_password()).thenReturn("password");
        dbProvider = new DBProvider(mockConfig);
        dbProvider.setCon(mockConnection);
    }

    @Test
    public void testGetQuery() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("query")).thenReturn("SELECT * FROM users");
        when(mockResultSet.getString("path")).thenReturn("/users");
        when(mockResultSet.getString("required_token")).thenReturn("0");

        struct_page page = new struct_page();
        page.setPath("/users");
        page.setAction("GET");

        struct_response response = dbProvider.getQuery(page);

        assertEquals("SELECT * FROM users", response.getQuery());
        assertEquals("/users", response.getPath());
        assertEquals("0", response.getRequire_token());
    }

    @Test
    public void testExcecuteQuery() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.getMetaData()).thenReturn(mockResultSetMetaData);
        when(mockResultSetMetaData.getColumnCount()).thenReturn(1);
        when(mockResultSetMetaData.getColumnName(1)).thenReturn("name");
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getObject(1)).thenReturn("test_result");
        when(mockResultSet.isLast()).thenReturn(true);

        List<Object> params = new ArrayList<>();
        params.add("test_param");

        String result = dbProvider.excecuteQuery("SELECT * FROM users WHERE name = ?", params);

        assertEquals("[{\"name\":\"test_result\"}]", result);
    }

    @Test
    public void testSearchToken() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString(1)).thenReturn("{\"MSG\":\"OK\"}");

        boolean result = dbProvider.search_token("test_token");

        assertEquals(true, result);
    }
}
