package com.kaly.pageGrade;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kaly.bean.GradeDetail;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class ConnectJWGL {

    private final String url = "http://jwsys.gdpu.edu.cn";
    private Map<String,String> cookies = new HashMap<String,String>();
    private String modulus;
    private String exponent;
    private String csrftoken;
    private Connection connection;
    private Connection.Response response;
    private Document document;
    private String stuNum;
    private String password;

    public ConnectJWGL(){}
    public ConnectJWGL(String stuNum,String password){
        this.stuNum = stuNum;
        this.password = password;
    }

    public String getStuNum() {
        return stuNum;
    }

    public void setStuNum(String stuNum) {
        this.stuNum = stuNum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }


    public void init() throws Exception{
        getCsrftoken();
        getRSApublickey();
    }

    // 获取csrftoken和Cookies
    private void getCsrftoken(){
        try{
            connection = Jsoup.connect(url+ "/xtgl/login_slogin.html?language=zh_CN&_t="+new Date().getTime());
            connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
            response = connection.timeout(30000).execute();
            cookies = response.cookies();
            //保存csrftoken
            document = Jsoup.parse(response.body());
            csrftoken = document.getElementById("csrftoken").val();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    // 获取公钥并加密密码
    public void getRSApublickey() throws Exception{
        connection = Jsoup.connect(url+ "/xtgl/login_getPublicKey.html?" +
                "time="+ new Date().getTime());
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
        response = connection.cookies(cookies).ignoreContentType(true).timeout(30000).execute();
        JSONObject jsonObject = JSON.parseObject(response.body());
        modulus = jsonObject.getString("modulus");
        exponent = jsonObject.getString("exponent");
        password = RSAEncoder.RSAEncrypt(password, B64.b64tohex(modulus), B64.b64tohex(exponent));
        password = B64.hex2b64(password);
    }

    //登录
    public String beginLogin() throws Exception{
        connection = Jsoup.connect(url+ "/xtgl/login_slogin.html");//登录的url，下面是在浏览器中F12获取到的参数
        connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");

        connection.data("csrftoken",csrftoken);
        connection.data("yhm",stuNum);//用户名
        connection.data("mm",password);
        connection.data("mm",password);
        response = connection.cookies(cookies).ignoreContentType(true)
                .method(Connection.Method.POST).timeout(30000).execute();
        document = Jsoup.parse(response.body());
        cookies = response.cookies();

        Element passCheck = document.getElementById("tips");
        if(passCheck == null){
            return "欢迎登陆";
        }else{
            return passCheck.text();
        }


    }

    // 获取成绩信息
    public String getStudentGradeJson(int year , int term)  {
        Map<String,String> datas = new HashMap<String, String>();
        datas.put("xnm",String.valueOf(year));
        datas.put("xqm",String.valueOf(term * term * 3));
        datas.put("_search","false");
        datas.put("nd",String.valueOf(new Date().getTime()));
        datas.put("queryModel.showCount","30");
        datas.put("queryModel.currentPage","1");
        datas.put("queryModel.sortName","");
        datas.put("queryModel.sortOrder","asc");
        datas.put("time","0");

        connection = Jsoup.connect(url+ "/cjcx/cjcx_cxDgXscj.html?gnmkdm=N305005&layout=default&su=" + stuNum);
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
        try {
            response = connection.cookies(cookies).method(Connection.Method.POST).data(datas).ignoreContentType(true).timeout(30000).execute();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        connection = Jsoup.connect(url+ "/cjcx/cjcx_cxDgXscj.html?doType=query&gnmkdm=N305005");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
        try {
            response = connection.cookies(cookies).method(Connection.Method.POST)
                    .data(datas).ignoreContentType(true).timeout(30000).execute();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        JSONObject jsonObject = JSON.parseObject(response.body());
        logout();
        return jsonObject.toString();
    }
    // 获取成绩信息详情
    public List<GradeDetail> getStudentGradeDetail(int xnm , int xqm,String jxb_id,String kcmc) throws Exception {
        Map<String,String> datas = new HashMap<String, String>();
        datas.put("jxb_id",jxb_id);
        datas.put("xnm",String.valueOf(xnm));
        datas.put("xqm",String.valueOf(xqm * xqm * 3));
        datas.put("kcmc",kcmc);
        connection = Jsoup.connect(url+ "/cjcx/cjcx_cxCjxqGjh.html?time="+"1594719195217"+"&gnmkdm=N305005");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
        try {
            response = connection.cookies(cookies).method(Connection.Method.POST).data(datas).ignoreContentType(true).timeout(30000).execute();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        document = Jsoup.parse(response.body());
        Elements rows = document.select("tbody").get(0).select("tr");
        List<GradeDetail> gradeDetails = new ArrayList<>();
        if (rows.size() == 1) {
            return null;
        }else {
            for(int i=1;i<rows.size();i++)
            {
                Element row = rows.get(i);
                gradeDetails.add(new GradeDetail(row.select("td").get(0).text(),
                                                row.select("td").get(1).text(),
                                                row.select("td").get(2).text()));
            }
        }
        logout();
        return gradeDetails;
    }

    public static void main(String[] args) throws Exception {
//        ConnectJWGL connectJWGL = new ConnectJWGL("1700502255","hgh123321");
//        connectJWGL.init();
//        connectJWGL.beginLogin();
//        System.out.println(connectJWGL.getStudentGradeJson1(2017,1,connectJWGL.getCookies()));
//        System.out.println(connectJWGL.getStudentGradeDetail(2018,2,"","软件工程"));
        //匿名类
        Thread t1= new Thread(){
            int i = 0;
            public void run(){
                //匿名类中用到外部的局部变量teemo，必须把teemo声明为final
                //但是在JDK7以后，就不是必须加final的了
                while(i<100){
                    System.out.println("t1");
                    i++;
                }
            }
        };

        t1.start();

        Thread t2= new Thread(){
            int i = 0;
            public void run(){
                while(i<100){
                    System.out.println("t2");
                    i++;
                }
            }
        };
        t2.start();

    }
    public void logout()  {
        connection = Jsoup.connect(url+ "/jwglxt/logout");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        try {
            response = connection.cookies(cookies).ignoreContentType(true).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
