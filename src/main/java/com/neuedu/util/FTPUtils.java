package com.neuedu.util;

import com.google.common.collect.Lists;
import com.neuedu.common.Const;
import lombok.Data;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Data
public class FTPUtils {

    public FTPUtils()
    {

    }

    public FTPUtils(String ip, String userName, String psw) {
        this.ip = ip;
        this.userName = userName;
        this.psw = psw;
    }

    //打印日志
    //logger
    private Logger logger = LoggerFactory.getLogger(FTPUtils.class);


    //定义变量
    private String ip;

    private String userName;

    private String psw;

    private FTPClient fc;

    //定义上传文件的方法
    public boolean uploadFile(String remotePath, List<File> files)
    {
        boolean result = true;
        if(isConnectSuc(Const.SERVERIP,Const.FTPUSERNAME,Const.FTPUSERPSW))
        {
            logger.info("开始上传文件");
            FileInputStream fis = null;
            try {
                fc.setFileType(FTPClient.BINARY_FILE_TYPE);
                fc.setControlEncoding("UTF-8");
                fc.setBufferSize(1024);
                fc.enterLocalPassiveMode();
                fc.changeWorkingDirectory(remotePath);
                logger.info("执行上传操作");

                for(File file:files)
                {
                    fis = new FileInputStream(file);
                    fc.storeFile(file.getName(),fis);
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.debug("上传失败...");
                result =  false;
            } finally {
                    if(null != fis)
                    {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                try {
                    fc.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                logger.info("上传成功");
            }
        }
        return result;
    }




    //定义判断是否连接成功的方法
    private boolean isConnectSuc(String ip,String name,String psw)
    {
        logger.info("正在连接。。。");
        boolean result = false;
        fc = new FTPClient();
        try {
            fc.connect(ip);
            result = fc.login(name,psw);
        } catch (IOException e) {
            e.printStackTrace();
            logger.debug("连接失败...");
        }
        logger.info("连接成功");
        return result;
    }


    public static void main(String[] args) {
        File file2 = new File("d://pom.xml");
        File file1 = new File("d://pom.xml");
        ArrayList<File> objects = Lists.newArrayList();
        objects.add(file1);
        objects.add(file2);
        boolean b = new FTPUtils().uploadFile("file/", objects);
        System.out.println(b);
    }
}
