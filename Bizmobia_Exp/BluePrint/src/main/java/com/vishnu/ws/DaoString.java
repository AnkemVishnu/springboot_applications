package com.vishnu.ws;

/**
 *
 * @author Vishnu
 */
public class DaoString {

    public static String getDAOString(String modelnameuc) {
        return "package com.bizmobia.jcb.dao;\n"
                + "\n"
                + "import com.bizmobia.jcb.models." + modelnameuc + ";\n"
                + "import org.springframework.data.jpa.repository.JpaRepository;\n"
                + "import org.springframework.stereotype.Repository;\n"
                + "\n"
                + "/**\n"
                + " *\n"
                + " * @author Vishnu\n"
                + " */\n"
                + "@Repository\n"
                + "public interface " + modelnameuc + "Dao extends JpaRepository<" + modelnameuc + ", Long> {\n"
                + "}";
    }
}
