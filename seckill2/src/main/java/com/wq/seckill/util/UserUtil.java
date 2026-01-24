package com.wq.seckill.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wq.seckill.pojo.User;
import com.wq.seckill.vo.RespBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

//生成多用户测试的脚本
//创建多个用户保存到seckill_user表 模拟http请求
public class UserUtil {
    public static void create(int count) throws Exception {
        List<User> users = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId(13300000100L + i);
            user.setNickname("user" + i);
            //可以动态生成不同的盐
            user.setSalt("wqab1257");//用户数据表的 salt,由程序员设置
            //?是用户原始密码,比如 12345 , hello 等
            user.setPassword(MD5Util.inputPassToDBPass("12345", user.getSalt()));
            users.add(user);
        }
        System.out.println("create user");
//插入数据库
        Connection connection = getConn();
        String sql = "insert into seckill_user(nickname,salt,password,id) values(?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            preparedStatement.setString(1, user.getNickname());
            preparedStatement.setString(2, user.getSalt());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setLong(4, user.getId());
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
        preparedStatement.clearParameters();//关闭
        connection.close();
        System.out.println("insert to do");
//登录拿到 userTicket
        String urlStr = "http://localhost:9090/login/doLogin";
        File file = new File("D:\\wqjmeter\\config.txt");
        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(0);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
//请求
            URL url = new URL(urlStr);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
//设置输入网页密码（相当于输出到页面）
            co.setDoOutput(true);
            OutputStream outputStream = co.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" + MD5Util.inputPassToMidpass("12345");
            outputStream.write(params.getBytes());
            outputStream.flush();
// 获 取 网 页 输 出 ，（ 得 到 输 入 流 ， 把 结 果 得 到 ， 再 输 出 到ByteArrayOutputStream 内）
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(bytes)) >= 0) {
                bout.write(bytes, 0, len);
            }
            inputStream.close();
            bout.close();
//把 ByteArrayOutputStream 内的东西转换为 respBean 对象
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
//得到 userTicket
            String userTicket = (String) respBean.getObj();
            System.out.println("create userTicket" + userTicket);
            String row = user.getId() + "," + userTicket;
//写入指定文件
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file:" + user.getId());
        }
        raf.close();
        System.out.println("over");
    }
    private static Connection getConn() throws Exception {
        String url =
                "jdbc:mysql://localhost:3306/seckill?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "wq";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }
    public static void main(String[] args) throws Exception {
        create(2000);
    }
}
