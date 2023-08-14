package com.xfire;

import org.codehaus.xfire.client.Client;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.MalformedURLException;

/**
 * @Author: zhang zhao lin
 * @Description:
 * @Date:Create：in 2020/11/5 18:30
 * @Modified By：
 */
@Controller
@RequestMapping("/t")
public class XfireController {

    private final static String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
            + "<ORDER><serviceCode>ydzyWorkNotice</serviceCode><source>02</source><target>"
            + "</target><data><sysCode>02</sysCode><sendTime>2020-11-02</sendTime><workLists>"
            + "<workList><busiName>06</busiName><workNum>1</workNum><timestamp>20201102111832</timestamp>"
            + "</workList></workLists></data></ORDER>";

    private final static String METHOD_NAME = "workNotice";

    private final static String URL = "http://192.168.110.21:38024/WFW/services/commonService?wsdl";

    @RequestMapping("/test")
    public String test() throws Exception {
        System.out.println("开始连接移动作业接口！");
        Client client = new Client(new java.net.URL(URL));
        // 设置连接超时时间
        client.setTimeout(1000);
        System.out.println("连接移动地址成功！发送XML：" + XML);
        System.out.println("接口连接超时时间：" + client.getTimeout() + "毫秒");

        System.out.println("---------------");
        System.out.println("连接移动作业方法：" + METHOD_NAME);
        System.out.println("---------------");
        System.out.println("连接地址: " + URL);
        System.out.println("---------------");
        Object[] results = client.invoke(METHOD_NAME, new Object[]{XML});
        if (results != null && !results[0].toString().equals(null)) {
            System.out.println("返回 : " + results[0].toString());
        }
        return results[0].toString();
    }
}
