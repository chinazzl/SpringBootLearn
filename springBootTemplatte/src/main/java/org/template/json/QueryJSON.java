package org.template.json;

/**********************************
 * @author zhang zhao lin
 * @date 2023年08月14日 14:15
 * @Description:
 **********************************/
public interface QueryJSON {

    String queryJson = "{\n" +
            "  \"targets\" :[],\n" +
            "  \"targetType\" : \"HOST\",\n" +
            "  \"viewTags\" : [\"UNDER_MAGIC\", \"OS_TYPE\", \"SYSTEM_NAME\"],\n" +
            "  \"tagFilters\" : [\n" +
            "    {\n" +
            "  \"condition\" : \"OR\",\n" +
            "  \"filters\" : [\n" +
            "    {\n" +
            "      \"condition\" : \"AND\",\n" +
            "      \"filters\" : [\n" +
            "        {\n" +
            "          \"tagKey\" : \"UNDER_MAGIC\",\n" +
            "          \"conditionType\" : \"LIKE\",\n" +
            "          \"tagValue\" : \"OS\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"tagKey\" : \"SYSTEM_NAME\",\n" +
            "          \"conditionType\" : \"EQUALS\",\n" +
            "          \"tagValue\" : \"xx系统\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"tagKey\" : \"OS_TYPE\",\n" +
            "      \"conditionType\" : \"EQUALS\",\n" +
            "      \"tagValue\" : \"1\"\n" +
            "    }\n" +
            "  ]\n" +
            "}\n" +
            "  ],\n" +
            "  \"pageSize\" : 10,\n" +
            "  \"page\" : 1\n" +
            "}";
}
