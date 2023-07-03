## 1.RedisTemplate 的使用
## 2. redis常用命令
 [参考文档](https://redis.com.cn/commands.html) <br>
 [lmove指令](https://redis.com.cn/commands/lmove.html) ,redis服务器版本要大于6.2.0 (目前使用的是6.0.9)

<hr>
我将redis安装到docker中了<br>

1. docker ps (查看所有docker 容器,找到redis的容器id)
2. docker exec -it redis_容器id /bin/bash
3. redis-server --version
