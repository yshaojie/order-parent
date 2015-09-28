import com.jyall.BusinessDistrict;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by shaojieyue on 9/11/15.
 */
public class MMTest {
    public static void main(String[] args) throws Exception {
        String server_jvm_args = "xxx";
        if (!server_jvm_args.contains("-server")) {
            server_jvm_args = "-server "+server_jvm_args;
        }

        System.out.println(server_jvm_args);
    }


}
