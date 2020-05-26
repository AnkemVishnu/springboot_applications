package com.vishnu.ws;

/**
 *
 * @author Vishnu
 */
public class ControllerString {

    public static String getControllerString(String modelnameuc, String modelnamelc) {//first param model name is in uppercase & removed spaces, second param modelname is in lowercase & removed spaces
        return "package com.bizmobia.jcb.controller;\n"
                + "\n"
                + "import com.bizmobia.jcb.request.RequestModel;\n"
                + "import com.bizmobia.jcb.response.ResponseModel;\n"
                + "import com.bizmobia.jcb.service." + modelnameuc + "Service;\n"
                + "import org.springframework.beans.factory.annotation.Autowired;\n"
                + "import org.springframework.web.bind.annotation.PostMapping;\n"
                + "import org.springframework.web.bind.annotation.RequestBody;\n"
                + "import org.springframework.web.bind.annotation.RequestHeader;\n"
                + "import org.springframework.web.bind.annotation.RequestMapping;\n"
                + "import org.springframework.web.bind.annotation.RestController;\n"
                + "\n"
                + "/**\n"
                + " *\n"
                + " * @author Vishnu\n"
                + " */\n"
                + "@RestController\n"
                + "@RequestMapping(\"/\")\n"
                + "public class " + modelnameuc + "Controller {\n"
                + "\n"
                + "    @Autowired\n"
                + "    private " + modelnameuc + "Service " + modelnamelc + "Service;\n"
                + "\n"
                + "    @PostMapping(\"insert" + modelnamelc + "\")\n"
                + "    public ResponseModel add" + modelnameuc + "(@RequestHeader(\"auth_token\") String autho_token, @RequestBody RequestModel requestObj) {\n"
                + "        return " + modelnamelc + "Service.add" + modelnameuc + "(autho_token, requestObj);\n"
                + "    }\n"
                + "\n"
                + "    @PostMapping(value = \"update" + modelnamelc + "\")\n"
                + "    public ResponseModel update" + modelnameuc + "(@RequestHeader(\"auth_token\") String autho_token, @RequestBody RequestModel requestObj) {\n"
                + "        return " + modelnamelc + "Service.update" + modelnameuc + "(autho_token, requestObj);\n"
                + "    }\n"
                + "\n"
                + "    @PostMapping(value = \"delete" + modelnamelc + "\")\n"
                + "    public ResponseModel delete" + modelnameuc + "(@RequestHeader(\"auth_token\") String autho_token, @RequestBody RequestModel requestObj) {\n"
                + "        return " + modelnamelc + "Service.delete" + modelnameuc + "(autho_token, requestObj);\n"
                + "    }\n"
                + "\n"
                + "    @PostMapping(value = \"getall" + modelnamelc + "\")\n"
                + "    public ResponseModel getAll" + modelnameuc + "(@RequestHeader(\"auth_token\") String autho_token, @RequestBody RequestModel requestObj) {\n"
                + "        return " + modelnamelc + "Service.getAll" + modelnameuc + "(autho_token, requestObj);\n"
                + "    }\n"
                + "\n"
                + "    @PostMapping(value = \"get" + modelnamelc + "byid\")\n"
                + "    public ResponseModel get" + modelnameuc + "ById(@RequestHeader(\"auth_token\") String autho_token, @RequestBody RequestModel requestObj) {\n"
                + "        return " + modelnamelc + "Service.get" + modelnameuc + "ById(autho_token, requestObj);\n"
                + "    }\n"
                + "}";
    }

}
