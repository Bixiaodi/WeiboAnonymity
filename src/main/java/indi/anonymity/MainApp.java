package indi.anonymity;

import indi.anonymity.helper.DatabaseConnector;
import org.apache.camel.main.Main;

import java.util.Arrays;

/**
 * A Camel Application
 */
public class MainApp {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {
//        Main main = new Main();
//        main.addRouteBuilder(new MyRouteBuilder());
//        main.run(args);
//        DatabaseConnector databaseConnector = new DatabaseConnector();
//        databaseConnector.connect();
        String[] s = new String[5];
        s[0] = "北京,东城区";
        s[1] = "广西柳州";
        s[2] = "广西";
        s[3] = "广东广州";
        s[4] = "广东,广州";
        Arrays.sort(s);
        for(String v: s) {
            System.out.println(v);
        }
    }

}

