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

    String tagFilter = "{\n" +
            "    \"condition\": \"AND\",\n" +
            "    \"tagFilters\": [\n" +
            "        {\n" +
            "            \"condition\": \"OR\",\n" +
            "            \"tagFilters\": [\n" +
            "                {\n" +
            "                    \"condition\": \"EQUALS\",\n" +
            "                    \"tagKey\": \"tag3\",\n" +
            "                    \"tagValue\": \"value3\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"condition\": \"LIKE\",\n" +
            "                    \"tagKey\": \"tag3\",\n" +
            "                    \"tagValue\": \"%value1%\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"condition\": \"EQUALS\",\n" +
            "                    \"tagKey\": \"tag3\",\n" +
            "                    \"tagValue\": \"value2\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"condition\": \"EQUALS\",\n" +
            "            \"tagKey\": \"tag4\",\n" +
            "            \"tagValue\": \"value4\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";
}
