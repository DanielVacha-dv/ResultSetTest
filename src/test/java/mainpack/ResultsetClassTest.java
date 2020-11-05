package mainpack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

class ResultsetClassTest {
    //vysledek list of map pro jeden executeQuery, ten muze mit vicero zaznamu
    List<Map> resultRowListOfMap =new ArrayList<>();
    // aktualni resultRowListOfMap
    List<Map> actualResultRowListOfMap =new ArrayList<>();
    // list  vsechny  executeQuery s jednotlivymi resultRowListOfMap
    List<List> statementList=new ArrayList<>();
    //prave uzivany resultset
    MockRowRS usedresult1Rows = new MockRowRS();
// atomicky citac zaznamu v resultset
    AtomicInteger idx = new AtomicInteger(0);

    @Mock
    private ResultSet resultSet;

    @Mock
    private Statement statement;

    @InjectMocks
    @Spy
    private ResultsetClass testableClass;


    /** vygeneruje data pro resultset s 1 radkem
     * */
    List<Map> initFirstRowsRS() {
        List<Map> resultRowListOfMapLoc =new ArrayList<>();
        Map<String,String> hashMap2=new HashMap<>();
        hashMap2.put("prvniklic11","value11");
        hashMap2.put("prvniklic12","value12");
        resultRowListOfMapLoc.add(hashMap2);
        return resultRowListOfMapLoc;
    }

    /** vygeneruje data pro resultset s 2radky
     * */
    List<Map> init2RowsRS() {
        List<Map> resultRowListOfMapLoc =new ArrayList<>();
        Map<String,String> hashMap=new HashMap<>();
        hashMap.put("klic11","value11");
        hashMap.put("klic12","value12");
        resultRowListOfMapLoc.add(hashMap);
        Map<String,String> hashMap2=new HashMap<>();
        hashMap2.put("klic21","value21");
        hashMap2.put("klic22","value22");
        resultRowListOfMapLoc.add(hashMap2);
        return resultRowListOfMapLoc;
    }


    @BeforeEach
    public   void beforeTest() {
        statementList= new ArrayList<>(Arrays.asList(initFirstRowsRS(),init2RowsRS()));
        resultSet=Mockito.mock(ResultSet.class);
        statement=Mockito.mock(Statement.class);
        testableClass=Mockito.spy(new ResultsetClass());
        testableClass.setRs(resultSet);
        testableClass.setStatement(statement);
    }

    public void beforeMethod() throws SQLException {
      //  when(testableClass.getResultSet()).thenReturn(resultSet);
          idx = new AtomicInteger(0);
        final AtomicInteger idStatements = new AtomicInteger(0);
       MockRowRS resultRows = new MockRowRS();
        doAnswer(new Answer<ResultSet>() {
            @Override
            public ResultSet answer(InvocationOnMock invocation) throws Throwable {
                int index = idStatements.getAndIncrement();
                actualResultRowListOfMap=statementList.get(index);
                idx = new AtomicInteger(0);
                return resultSet;
            }
        }).when(statement).executeQuery(anyString());
        doAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                int index = idx.getAndIncrement();
                if (actualResultRowListOfMap.size() <= index) {
                    return false;
                }
                Map<String,String> map=    actualResultRowListOfMap.get(index);
                resultRows.setCurrentHashMapData(map);
                return true;
            }
        }).when(resultSet).next();
        doAnswer(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                String  idx =  (String) args[0];
                return resultRows.getColumn(idx);
            }

            ;
        }).when(resultSet).getString(anyString());
    }

    static class MockRowRS {
        Map<String,String> hashMap=new HashMap<>();

        public void setCurrentHashMapData(Map<String, String> hashMap) {
            this.hashMap = hashMap;
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