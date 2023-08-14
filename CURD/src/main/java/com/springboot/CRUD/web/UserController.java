package com.springboot.CRUD.web;

import com.springboot.CRUD.entity.Student;
import com.springboot.CRUD.service.IQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IQueryService queryService;

    @Value("${web.downLoad.path}")
    private String path;

    @RequestMapping("/query")
    public String query(Model model, HttpServletRequest request){
        List<Student> userList = queryService.getAll();
        model.addAttribute("users",userList);
        return "usr/list.html";
    }

    @RequestMapping("/toEdit")
    public String tiEdit(@RequestParam(name = "id") int id){

        return null;
    }

    @RequestMapping("/downLoadView")
    public String downLoadView(){

        return "usr/download.html";
    }

    /**
     * 下载
     */
    //实现Spring Boot 的文件下载功能，映射网址为/download
    @RequestMapping("/download")
    public String downloadFile(HttpServletRequest request,
                               HttpServletResponse response){
        //读取文件夹
        File fileDic = new File(path);
        File[] files = fileDic.listFiles();
        System.out.println(files[0]);
        String fileName = files[0].getName();
        System.out.println(fileName);
        if(fileName!= null){
            String realPath = path;
            File file = new File(path,fileName);
            //如果文件存在进行下载
            if (file.exists()){
                //配置文件下载
                response.setHeader("content-type","application/octet-stream");
                response.setContentType("application/octet-stream");
                //下载文件正常显示中文
                try {
                    response.setHeader("Content-Disposition","attachment;filename="+URLEncoder.encode(fileName,"UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //实现文件下载
                byte[] bytes = new byte[2048];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try{
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    ServletOutputStream os = response.getOutputStream();
                    int i = 0;
                    while((i=bis.read(bytes)) != -1){
                        os.write(bytes,0,i);
                    }
                    System.out.println(fileName + "下载成功");

                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("下载失败");
                }finally {
                    if(bis != null){
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if(fis != null){
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }

}
