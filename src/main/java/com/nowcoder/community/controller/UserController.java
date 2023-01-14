package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @Author: 少不入川
 * @Date: 2023/1/14 10:23
 */

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            model.addAttribute("error","您还没有上传图片!");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确!");
            return "/site/setting";
        }

        // 生成随机文件名
        fileName = CommunityUtil.generateUUID() + "." + suffix;
        // 确认文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            /**
             * todo
             * SpringBoot使用全局异常，会自动捕捉异常，当异常出现时会自动返回，异常之后的代码不会再执行
             * 而使用try...catch...结构，异常被捕捉以后代码会继续执行。
             * 如果异常被捕捉以后我们不抛出异常，这个异常就被catch块吃掉，程序虽然继续运行，但是实际上已经出问题了，所以我们需要抛出这个异常让程序中断。
             *
             * 在这里我们为了记录下日志，但是又不想让后面的代码继续执行，所以抛出异常让程序返回。
             * 你当然可以写throw e;但是这个e是一般的异常，如果这样抛出的话，你得在这个函数头上用throws来声明，
             * 比如：public void abc() throws Exception然后调用这个函数的函数也还得这么干，所以一般的处理是把e包装成运行时异常
             *
             * 运行时异常的特点是：当程序中可能出现这类异常，即使没有用try-catch语句捕获它，也没有用throws子句声明抛出它，也会编译通过。
             * */
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败", e);
        }

        // 存取成功以后就要更新用户头像的路径（web访问路径）
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){

        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 响应图片
        response.setContentType("image/" + suffix);
        try {
            OutputStream os = response.getOutputStream();
            FileInputStream fis = new FileInputStream(fileName);
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1){
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败" + e.getMessage());
        }

    }

    @RequestMapping(path = "/modifyPwd", method = RequestMethod.POST)
    public String modifyPassword(String password, String newPassword,Model model) {

        Map<String, Object> map = userService.updatePassword(password, newPassword);

        if (map == null || map.isEmpty()) {
            // 修改成功，跳转到login页面，需要登录
            model.addAttribute("msg", "修改成功！");
            model.addAttribute("target", "/logout");
            return "/site/operate-result";
        }else {
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));

            return "/site/setting";
        }
    }
}
