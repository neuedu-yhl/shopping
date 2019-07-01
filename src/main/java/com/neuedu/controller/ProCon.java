package com.neuedu.controller;

import com.google.common.collect.Lists;
import com.neuedu.common.Const;
import com.neuedu.common.HigherResponse;
import com.neuedu.pojo.User;
import com.neuedu.util.FTPUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.HashMap;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class ProCon {
    @RequestMapping("/upload.do")
    public HigherResponse uploadFile(HttpSession session, MultipartFile file, HttpServletRequest request)
    {
        //判断是否有用户登录
//        if((null == session.getAttribute(Const.CURRENTADMIN)) || null ==session.getAttribute(Const.CURRENTUSER))
//        {
//            return HigherResponse.getResponseFailed("请登录后再操作。。。");
//        }
        //拿到文件名
        String originalFilename = file.getOriginalFilename();
        //String lastIndexOf(".");
        //index4.html
        //拿到文件名的后缀
        String endsStr = originalFilename.substring(originalFilename.lastIndexOf("."),originalFilename.length());
        //将传上来的文件名修改
        String uuid = UUID.randomUUID().toString();
        //拼新生成的文件名
        String uri = uuid.concat(endsStr);
        //先创建一个临时目录
        String upload = request.getSession().getServletContext().getRealPath("upload");

        //没有目录 创建目录
        File newPath = new File(upload);
        if(!newPath.exists())
        {
            newPath.setWritable(true);
            newPath.mkdir();
        }
        //new File出来 将multipartFile转换为file
        File file1 = new File(newPath, uri);
        try {
            //将multipartFile转换file文件
            file.transferTo(file1);
            //将文件上传到ftp服务器
            boolean b = new FTPUtils().uploadFile("/file/", Lists.newArrayList(file1));
            if(true == b)
            {
                String url = Const.PREFIX+":81/file/"+uri;
                HashMap<String, String> stringStringHashMap = new HashMap<>();
                stringStringHashMap.put("uri",uri);
                stringStringHashMap.put("url",url);
                //删除临时文件
                file1.delete();
                return HigherResponse.getResponseSuccess("上传成功",stringStringHashMap);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return HigherResponse.getResponseFailed("上传失败。。。");
    }
}
