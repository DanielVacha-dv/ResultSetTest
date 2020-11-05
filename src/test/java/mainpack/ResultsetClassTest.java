package mainpack;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

class ResultsetClassTest {
    String[][] result = { { "column1", "column2" }, { "column1", "column2" } };
    List<Map> resultRowMap=new ArrayList<>();

    @InjectMocks
    @Spy
    private ResultsetClass testableClass;

    @Mock
    private ResultSet resultSet;

    void initMap() {
        Map<String,String> hashMap=new HashMap<>();
        hashMap.put("klic11","value11");
        hashMap.put("klic12","value12");
        resultRowMap.add(hashMap);
        Map<String,String> hashMap2=new HashMap<>();
        hashMap2.put("klic21","value21");
        hashMap2.put("klic22","value22");
        resultRowMap.add(hashMap2);
    }
    @BeforeEach
    public   void beforeTest() {
        initMap();
        testableClass=Mockito.spy(new ResultsetClass());
        resultSet=Mockito.mock(ResultSet.class);
    }

    public void beforeMethod() throws SQLException {
        when(testableClass.getResultSet()).thenReturn(resultSet);
        final AtomicInteger idx = new AtomicInteger(0);
        final MockRow row = new MockRow();

        doAnswer(new Answer<Boolean>() {

            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                int index = idx.getAndIncrement();
                if (result.length <= index) {
                    return false;
                }
                String[] current = result[index];
            Map<String,String> map=    resultRowMap.get(index);
                row.setCurrentRowData(current);
                row.setCurrentHashMapData(map);
                return true;
            }
        }).when(resultSet).next();

        doAnswer(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                int idx = ((Integer) args[0]).intValue();
                return row.getColumn(idx);
            }

            ;
        }).when(resultSet).getString(anyInt());
        doAnswer(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                String  idx =  (String) args[0];
                return row.getColumn(idx);
            }

            ;
        }).when(resultSet).getString(anyString());
    }

    static class MockRow {
        String[] rowData;
        Map<String,String> hashMap=new HashMap<>();

        public void setCurrentRowData(String[] rowData) {
            this.rowData = rowData;
        }

        public void setCurrentHashMapData(Map<String, String> hashMap) {
            this.hashMap = hashMap;
        }

        public String getColumn(int idx) {
            return rowData[idx - 1];
        }
        public String getColumn(String  key) {
            return hashMap.get(key);
        }

    }
    @Test
    void testMain() throws SQLException {
        beforeMethod();
        testableClass.doAction();
    }
}