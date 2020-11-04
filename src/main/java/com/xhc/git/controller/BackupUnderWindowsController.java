package com.xhc.git.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @Author xuhongchang
 * @Date 2020/11/2  5:03 下午
 * @Describetion windows环境下备份
 */
@RestController
@Slf4j
public class BackupUnderWindowsController {

    public static final String batDir = "C:/project/backup.bat";
    public static final String workspace = "C:/repository";


    @PostMapping("/backupUnderWindowsReceive")
    public void receiveEventPush(HttpServletRequest request, HttpServletResponse response) {
        log.info("请求对象：{}", request);
        // 1.得到请求的所有对象
        String body = readAsChars(request);
        log.info("完整的参数：{}", body);

        // 2.获取提交信息
        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONObject repository = jsonObject.getJSONObject("repository");
        String sshUrl = repository.getString("git_ssh_url");
        String name = repository.getString("name");

        // 3.调用bat脚本拉取代码
        execBat(name, sshUrl);
    }

    /**
     * 执行bat脚本文件
     *
     * @param project 项目名称
     * @param url     项目的ssh地址
     */
    private void execBat(String project, String url) {
        log.info("------start exec bat------");
        // 脚本的路径
        String batPath = batDir + " " + workspace + " " + project + " " + url;
        log.info("------start exec bat ------" + batPath);
        File batFile = new File(batDir);
        boolean batFileExist = batFile.exists();
        if (batFileExist) {
            StringBuilder sb = new StringBuilder();
            try {
                Process child = Runtime.getRuntime().exec(batPath);
                InputStream in = child.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                in.close();
                try {
                    child.waitFor();
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
                log.info("执行完成：{}", sb.toString());
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public static String readAsChars(HttpServletRequest request) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder("");
        try {
            br = request.getReader();
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

}
