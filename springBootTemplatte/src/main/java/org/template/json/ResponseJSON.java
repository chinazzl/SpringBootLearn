package org.template.json;

/**********************************
 * @author zhang zhao lin
 * @date 2023年08月22日 16:09
 * @Description:
 **********************************/
public interface ResponseJSON {

    String out = "{\n" +
            "  \"code\" : 0,\n" +
            "  \"success\" : true,\n" +
            "  \"data\" : {\n" +
            "    \"pageData\" : {\n" +
            "      \"page\" : 1,\n" +
            "      \"pageSize\" : 10,\n" +
            "      \"total\" : 53,\n" +
            "      \"listData\" : [\n" +
            "        {\n" +
            "          \"target\" : \"192.168.1.1\",\n" +
            "          \"targetType\" : \"HOST\",\n" +
            "          \"tags\" : [\n" +
            "            {\n" +
            "              \"tagId\" : 12341,\n" +
            "              \"tagKey\" : \"tag1\",\n" +
            "              \"tagValue\" : \"xxxxx系统\",\n" +
            "              \"tagType\" : \"MUTI_TEXT\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"tagId\" : 12342,\n" +
            "              \"tagKey\" : \"tag2\",\n" +
            "              \"tagValue\" : \"xxxxx\",\n" +
            "              \"tagType\" : \"SINGLE_TEXT\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"tagId\" : 12343,\n" +
            "              \"tagKey\" : \"tag3\",\n" +
            "              \"tagValue\" : \"true\",\n" +
            "              \"tagType\" : \"BOOLEAN\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"tagId\" : 12344,\n" +
            "              \"tagKey\" : \"tag4\",\n" +
            "              \"tagValue\" : \"01\",\n" +
            "              \"tagType\" : \"SINGLE_SELECT\",\n" +
            "              \"tag_text\" : \"运行\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"tagId\" : 12345,\n" +
            "              \"tagKey\" : \"tag5\",\n" +
            "              \"tagValue\" : \"01\",\n" +
            "              \"tagType\" : \"MUTI_SELECT\",\n" +
            "              \"tag_text\" : \"value1\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  }\n" +
            "}";

}
