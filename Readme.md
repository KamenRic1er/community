# 项目启动
项目根路径：http://localhost:8080/community/index
## 启动ES
直接找到ES路径的bin目录下，点击E:\Java\ElasticSearch\elasticsearch-6.4.3\bin\elasticsearch.bat
## 启动Redis
同样的找到Redis启动路径，点击E:\Java\Redis\Redis\windows\Redis-x64-3.2.100\redis-server.exe



## 启动服务Kafka

### 关于Kafka使用的重要提示！！！
- 现象：在windows的命令行里启动kafka之后，当关闭命令行窗口时，就会强制关闭kafka。这种关闭方式为暴力关闭，很可能会导致kafka无法完成对日志文件的解锁。届时，再次启动kafka的时候，就会提示日志文件被锁，无法成功启动。
- 方案：将kafka的日志文件全部删除，再次启动即可。
- 建议：不要暴力关闭kafka，建议通过在命令行执行kafka-server-stop命令来关闭它。
- 其他：将来在Linux上部署kafka之后，采用后台运行的方式，就会避免这样的问题。

- 先进入Kafka根目录
E:\Java\kafka\kafka_2.11-2.3.0 
### 启动 zookeeper:
bin\windows\zookeeper-server-start.bat config\zookeeper.properties
### 启动kafka:
bin\windows\kafka-server-start.bat config\server.properties
### 关闭zookeeper服务器
bin\windows\zookeeper-server-stop.bat
### 关闭kafka服务器
bin\windows\kafka-server-stop.bat

