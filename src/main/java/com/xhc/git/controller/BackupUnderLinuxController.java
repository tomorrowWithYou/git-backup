package com.xhc.git.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Author xuhongchang
 * @Date 2020/11/2  5:03 下午
 * @Describetion linux环境下备份
 *
 *
 * 【扩展】：
 * 在得到所有推送参数后，可以根据对应的参数作出不同处理。比如：判断分支，不同的分支执行不同的操作。
 *
 */
@RestController
@Slf4j
public class BackupUnderLinuxController {

    public static final String shellDir = "/mnt/soft/git_backup/backup.sh";
    public static final String workspace = "/mnt/repository";

    /**
     * 接收请求，这里不做特别详细的处理，在接收到推送信息后，调用shell脚本更新代码到本地
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @PostMapping("/backupUnderLinuxReceive")
    public String receive(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {
        log.info("请求对象：{}", request);

        // 1.得到请求的所有对象
        String body = readAsChars(request);
        log.info("完整的参数：{}", body);

        // 2.取出关键参数
        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONObject repository = jsonObject.getJSONObject("repository");
        // 得到ssh地址，需要使用ssh地址，不能使用https，因为https需要密码，ssh免密
        String sshUrl = repository.getString("ssh_url");
        // 仓库名称
        String name = repository.getString("name");

        // 3.调用sh脚本拉取代码
        execShell(name, sshUrl);
        return "";
    }


    /**
     * 执行shell脚本，拉取代码
     *
     * @param name 仓库名称
     * @param url  ssh地址
     * @throws IOException
     * @throws InterruptedException
     */
    private void execShell(String name, String url) throws IOException, InterruptedException {
        String bashCommand = "/bin/sh " + shellDir + " " + workspace + " " + name + " " + url;
        log.info("执行sh脚本命令：{}", bashCommand);
        Runtime runtime = Runtime.getRuntime();
        Process pro = runtime.exec(bashCommand);
        int status = pro.waitFor();
        if (status != 0) {
            log.info("Failed to call shell's command ");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(pro.getInputStream()));
        StringBuffer strbr = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            strbr.append(line).append("\n");
        }
        String result = strbr.toString();
        log.info("执行完成：{}", result);
    }

    /**
     * 得到所有请求对象
     *
     * @param request
     * @return
     */
    private static String readAsChars(HttpServletRequest request) {
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
