MYSQL 5.6 + TOMCAT V9.0 + NIGNX

项目已经部署上服务器: 使用nignx服务器部署前端网页,并使用反向代理解决跨域访问tomcat服务器上本项目server
项目网站: http://lococo.site/shop.html  
后端服务器: 47.106.107.239:22

基于JJWT库的Token验证

公共方法封装在login类中,没有开common包,没有使用MVC模式,也是为了赶时间,不需要创建太多无用的模型类bean

命名方法使用大小驼峰法,但数据库字段是间隔法

本项目使用postman调试: 

调试的生成的接口文档:
https://documenter.getpostman.com/view/3514825/collection/RVuABmVF

也有doc的版本的接口文档(见目录),不过由于几次前后端对接改参,所以doc文档有所偏差