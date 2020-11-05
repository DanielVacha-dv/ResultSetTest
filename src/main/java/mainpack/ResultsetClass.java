package mainpack;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class ResultsetClass {


  static final Logger LOGGER=Logger.getLogger("ResultsetClass.class");
    ResultSet rs;

    void doAction() {
        rs=getResultSet();
        try {
            LOGGER.info("string doit="+rs);
            final boolean next = rs.next();
//            String string = rs.getString(1);
//            LOGGER.info("string="+string);
            String string2 = rs.getString("klic11");
            LOGGER.info("string2="+string2);

        } catch (SQLException  e){
            LOGGER.warning("to neni hezke");
        } catch (Exception e) {
            LOGGER.warning("to ne Fuj " +e.getMessage());
        }
    }
    ResultSet getResultSet() {
        return rs;
    }

    public static void main(String[] args) throws Exception {
//        ResultsetClass rs=new ResultsetClass();
//        rs.doAction();
        List<String> s=new ArrayList<>();
        System.out.println("s ="+s.size() );
        s.add("pipi");
        System.out.println("s ="+s.size() );
    }
}
