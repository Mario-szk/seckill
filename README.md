## 聚焦Java性能优化，打造亿级流量秒杀系统

![](C:\Users\86157\AppData\Roaming\Typora\typora-user-images\image-20201204143633860.png)

### 部署生产环境

1. 本地在项目根目录下使用mvn clean package打包生成seckill-1.0.0-SNAPSHOT.jar文件

2. 将jar包服务上传到服务端上并编写额外的application.properties配置文件

3. 编写deploy.sh脚本文件启动对应的项目

   ```
   source /etc/profile;		//使java环境生效
   nohup java -Xms2048m -Xmx2048m  -XX:NewSize=1024m -XX:MaxNewSize=1024m -jar seckill-1.0.0-SNAPSHOT.jar --spring.config.addition-location=/usr/local/seckill/application.properties
   ```

4. 授权并执行使用

   ```
   chmod 777 deploy.sh
   
   ./deploy.sh &
   ```

   启动应用程序

   查看日志

   ```
   tail -f nohup.out
   ```

5. 打开阿里云的网络安全组配置，将80端口开放给外网可访问

**参数说明**

- nohup：以非停止方式运行程序，这样即便控制台退出了程序也不会停止
- java：java命令启动，设置jvm初始和最大内存为2048m，2个g大小，设置jvm中初始新生代和最大新生代大小为1024m，设置成一样的目的是为了减少扩展jvm内存池过程中向操作系统索要内存分配的消耗
- –-spring.config.addtion-location=：指定额外的配置文件地址

### 性能压测 ApacheJMeter

##### 1. 前置知识

##### 2. 组件

- 线程组
- Http请求
- 察看结果树
- 聚合报告

##### 3. 常用操作命令

```
查看服务器性能
top -H

查看某个进程
ps -ef | grep java

通过端口查看进程
netstat -anp | grep 5240

查看某进程的线程
pstree -p [progressId] wc -l

统计某进程的线程数量
pstree -p [progressId] wc -l

杀死进程
kill 5240
```



### 1.  解决容量问题

#### 1.1 服务端并发容量上不去

##### 1.1.1 修改Spring Boot内嵌Tomcat默认配置（spring-configuration-metadata.json ）

```
server.tomcat.accept-count:等待队列长度，默认100 
server.tomcat.max-connections:最大可被连接数，默认10000 
server.tomcat.max-threads:最大工作线程数，默认200 
server.tomcat.min-spare-threads:最小工作线程数，默认10

默认配置下，链接超10000后出现拒绝链接情况 
默认配置下，发出的请求超过200+100后拒绝处理
```

修改配置如下

```
server.tomcat.accept-count: 1000
server.tomcat.max-connections: 10000 
server.tomcat.max-threads: 800
server.tomcat.min-spare-threads: 10000

对4核8G的服务器来说，经验上最好的max-threads是800
```

##### 1.1.2 定制化内嵌Tomcat配置定制化内嵌Tomcat配置

```
@Component
public class WebServerConfiguration implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    @Override
    public void customize(ConfigurableWebServerFactory configurableWebServerFactory) {
         //使用对应工厂类提供给我们的接口定制化我们的tomcat connector
        ((TomcatServletWebServerFactory)configurableWebServerFactory).addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
                //定制化keepalivetimeout(设置30秒内没有请求则服务端自动断开keepalive链接)
                protocol.setKeepAliveTimeout(30000);
                //当客户端发送超过10000个请求则自动断开keepalive链接
                protocol.setMaxKeepAliveRequests(10000);
            }
        });
    }
}
```

**说明：**

- keepAliveTimeOut：多少毫秒后不响应就断开keepAlive
- maxKeepAliveRequests：多少次请求后keepAlive断开失效
- KeepAlive ：建立长连接，保护系统不受客户端连接的拖累，减少网络消耗

#### 1.2  响应时间长，TPS上不去

##### 1.2.1 单web容器上限

- 线程数量：4核8G的单进程调度线程数最好是800，超过1000后即花费巨大的时间在cpu调度上
- 等待队列长度：队列做缓冲池用，但不能无限长，消耗内存，出对入队消耗cpu（一般1000-2000）

##### 1.2.2  Mysql数据库QPS容量问题

- 主键查询：千万级别数据 = 1-10毫秒。 
- 唯一索引查询： 千万级别数据= 10-100毫秒 
- 非唯一索引查询： 千万级别数据= 100-1000毫秒 
- 无索引： 百万条数据= 1000毫秒 +

数据库查询尽量使用到主键查询和唯一索引查询，如果非唯一索引查询在数据达到一定数量级后，则要进行分表分库来优化。

##### 1.2.3  单机容量问题

- 表象：单机cpu使用率增高，memory占用增加，网络带宽使用增加
- cpu us：用户空间的Cpu使用情况
- cpu sy：内核空间的cpu使用情况
- load average：1，5，15分钟load平均值，跟着核数系数，0代表通常，1代表打满，1+代表阻塞
- memory：free空闲内存，used使用内存

### 2. 分布式扩展（服务端水平对称部署）

#### 2.1 引入Nginx实现反向代理、负载均衡

##### 2.1.1 部署使用OpenResty作为Nginx框架

如果对Nginx开发有特殊要求或者OpenResty的Nginx达不到我们的需求，可以到Nginx官网下载，操作之后得到所需的Nginx替换掉openResty/sbin目录下的nginx即可。

```
1.先行条件，需要在linux安装pcre，openssl，gcc，curl等
apt install PCRE
apt install pcre-devel openssl-devel gcc curl

2.下载openresty 下载页面 http://openresty.org/cn/download.html

3.上传并解压
tar -xvzf openresty**.tar.gz

4.解压后执行如下命令
./configure
make
make install
安装完成，nginx默认安装在 /usr/local/openresty/nginx目录下
修改本地和阿里云服务器的host路径，以便于统一访问
```

##### 2.1.2  将Nginx作为Web服务器

- location节点path：指定url映射key
- location节点内容：root指定location path后对应的根路径，index指定默认的访问页
- sbin/nginx -c conf/nginx.conf启动
- 修改配置后直接sbin/nginx -s reload无缝重启

```
1.静态资源部署
进入nginx根目录下的html下，新建resources目录用于存放前端静态资源
设置指向resources目录下的location可以访问对应的html下的静态资源文件
```

![](E:\程序人生\个人学习笔记\学习笔记图床\QQ图片20201204183955.png)

##### 2.1.3 将Nginx作为动静分离服务器

- location节点path特定resources：静态资源路径
- location节点其他路径：动态请求

##### 2.1.4 将Nginx作为反向代理服务器

- 设置upStream sserver

  反向代理配置，配置一个backend server，可以用于指向后端不同的server集群，配置内容为server集群的局域网ip，以及轮训的权重值，并且配置一个location，当访问规则命中location任何一个规则的时候则可以进入反向代理规则

```
    upstream backend_server{
        server 192.168.75.180 weight=1;
        server 192.168.75.181 weight=1;
    }
```

- 设置动态请求location为proxy pass路径

```
    location / {
         proxy_pass http://backend_server;
         #设置请求头
         proxy_set_header Host $http_host:$proxy_port;
         proxy_set_header X-Real-IP $remote_addr;
         proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
```

- 开启tomcat access log认证

```
#日志开关
server.tomcat.accesslog.enabled=true
#日志格式
server.tomcat.accesslog.pattern=%h %l %u %t "%r" %s %b %D
server.tomcat.accesslog.directory=/usr/local/seckill/logs
```

- 设置Nginx与应用服务器的长连接

```
    upstream backend_server{
        server 192.168.75.180 weight=1;
        server 192.168.75.181 weight=1;
        keepalive_timeout 30;			//添加此行
    }
```

```
    location / {
         proxy_pass http://backend_server;
         #设置请求头
         proxy_set_header Host $http_host:$proxy_port;
         proxy_set_header X-Real-IP $remote_addr;
         proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
         # 添加如下两行
         proxy_http_version 1.1;
         #允许重新定义或追加字段到传递给代理服务器的请求头信息（默认是close）
         proxy_set_header Connection "";
    }
```

##### 2.1.5 Nginx高性能原因

- epoll多路复用
  - java bio模型 - 阻塞式进程
  - linux select模型 - 变更触发轮询查找，有1024数量上限
  - **epoll模型 - 变更触发回调直接读取，理论上无上限**

- master-worker进程模型

![](E:\程序人生\个人学习笔记\学习笔记图床\master-worker模型.png)

- 协程机制
  - 依附于线程的内存模型，切换开销小
  - 遇阻塞及归还执行权，代码同步
  - 无需加锁

#### 2.2 引入Redis实现分布式会话管理存储

##### 2.2.1 基于cookie传输sessionid（不适用于移动端【安卓、IOS、微信小程序】）

- 引入依赖

  ```
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
      </dependency>
      <dependency>
        <groupId>org.springframework.session</groupId>
        <artifactId>spring-session-data-redis</artifactId>
        <version>2.0.5.RELEASE</version>
      </dependency>
  ```

- 配置类

  ```
  @Component
  @EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600)
  public class RedisSessionConfig {
      
  }
  ```

##### 2.2.2 基于token传输类似sessionid(推荐)

- 修改后端代码

```
        //生成登录凭证token，UUID
        String uuidToken = UUID.randomUUID().toString();
        uuidToken = uuidToken.replace("-","");
        //建议token和用户登陆态之间的联系
        redisTemplate.opsForValue().set(uuidToken,userModel);
        redisTemplate.expire(uuidToken,1, TimeUnit.HOURS);
        //下发token
        return CommonReturnType.create(uuidToken);
```

- 修改前端代码

### 3. 查询性能优化 - 实现多级缓存

#### 	缓存设计原则

- 用快速存取设备，用内存
- 将缓存推到离用户最近的地方
- 脏缓存清理

#### 3.1 Redis缓存

##### 3.1.1 单机版

```
//存入Redis
redisTemplate.opsForValue().set("item_"+id,itemModel);
redisTemplate.expire("item_"+id,10, TimeUnit.MINUTES);
```

```
//序列化
public class JodaDateTimeJsonSerializer extends JsonSerializer<DateTime> {
    @Override
    public void serialize(DateTime dateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(dateTime.toString("yyyy-MM-dd HH:mm:ss"));
    }
}
```

```
//反序列化
public class JodaDateTimeJsonDeserializer extends JsonDeserializer<DateTime> {
    @Override
    public DateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String dateString =jsonParser.readValueAs(String.class);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return DateTime.parse(dateString,formatter);
    }
}
```

```
//自定义RedisTemplates
@Component
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600)
public class RedisSessionConfig {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //解决key的序列化方式 -> String
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        //解决value的序列化方式 -> Json
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper =  new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(DateTime.class,new JodaDateTimeJsonSerializer());
        simpleModule.addDeserializer(DateTime.class,new JodaDateTimeJsonDeserializer());
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        objectMapper.registerModule(simpleModule);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        return redisTemplate;
    }
}
```

##### 3.1.2 Sentinal哨兵模式



##### 3.1.3 集群cluster模式



#### 3.2 本地热点缓存

##### 3.2.1 **特点**

- 热点数据
- 脏读非常不敏感
- 内存可控

##### 3.2.2 解决方案 -  Guava cache

- 可控制的大小和超时时间
- 可配置的lru策略（达到存储上限后，最近最少被访问的key优先被淘汰）
- 线程安全

#### 3.3 nginx+lua 缓存

##### 3.3.1 原理解析

- **lua协程机制**

  - 依附于线程的内存模型，切换开销小
  - 遇阻塞及归还执行权，代码同步
  - 无需加锁

- **nginx协程机制**

  - nginx每个工作进程创建一个lua虚拟机
  - 工作进程内的所有协程共享同一个VM
  - 每个外部请求由一个lua协程处理，之间数据隔离
  - lua代码调用io等异步接口时，协程被挂起，上下文数据保持不变
  - 自动保存，不阻塞工作进程
  - io异步操作完成后还原协程上下文，代码继续执行

  **Nginx处理阶段：**

  ![](E:\程序人生\个人学习笔记\学习笔记图床\QQ图片20201206161316.png)

- **nginx lua插载点**

  - init_by_lua：系统启动时调用
  - init_worker_by_lua：worker进程启动时调用
  - set_by_lua：nginx变量用复杂lua return
  - rewrite_by_lua：重写url规则
  - access_by_lua：权限验证阶段
  - content_by_lua：内容输出节点

![](E:\程序人生\个人学习笔记\学习笔记图床\QQ图片20201206161545.png)

- **OpenResty**
  - OpenResty由Nginx核心和很多第三方模块组成，默认集成了lua开发环境，使Nginx可以作为一个Web Server使用
  - 借助于Nginx的事件驱动模型和非阻塞IO，可以实现高性能的Web应用程序
  - OpenResty提供了大量组件，如Mysql，Redis，Memcached等，使在Nginx上开发Web应用程序更方便更简单

##### 3.3.2 方式一： share dic 共享内存字典（存在脏读问题）

- 编写lua脚本 - itemsharedic.lua

```
function get_from_cache(key)
        local cache_ngx = ngx.shared.my_cache
        local value = cache_ngx:get(key)
        return value
end

function set_to_cache(key,value,exptime)
        if not exptime then
                exptime = 0
        end
        local cache_ngx = ngx.shared.my_cache
        local succ,err,forcible = cache_ngx:set(key,value,exptime)
        return succ
end

local args = ngx.req.get_uri_args()
local id = args["id"]
local item_model = get_from_cache("item_"..id)
if item_model == nil then
        local resp = ngx.location.capture("/item/get?id="..id)
        item_model = resp.body
        set_to_cache("item_"..id,item_model,1*60)
end
ngx.say(item_model)
```

- 配置nginx.conf

加入shared dictionary的扩展，声明128m的共享字典的访问内存

```
lua_shared_dict my_cache 128m;
```

- 设置location用来访问shared dict的lua文件

```
location /itemlua/get {
		default_type 'application/json';
		content_by_lua_file '/usr/local/openresty/lua/itemsharedic.lua';
}
```

**适用场景：**更新较少，热点数据

##### 3.3.3 方式二：Redis的支持

- 编写lua脚本 - itemredis.lua

```
local args = ngx.req.get_uri_args()
local id = args[ "id"]
local redis= require "resty.redis"
local cache= redis:new()
local ok,err = cache:connect("192.168.75.140",6379)   //连接只读Redis，只读操作
local item_model= cache:get("item_"..id)
if item_model == ngx.null or item_model = nil then
       local resp = ngx.location.capture("/item/get?id="..id) 
       item_model= resp.body
end
ngx.say(item_model)
```

- 配置nginx.conf

```
location /itemlua/get {
		default_type 'application/json';
		content_by_lua_file '/usr/local/openresty/lua/itemredis.lua';
}
```

**适用场景：**更新较多，非热点数据

#### 3.4 nginx proxy cache 缓存（不推荐）

##### 3.4.1 前置条件

- Nginx反向代理
- 依靠文件系统存索引级的文件
- 依靠内存缓存文件地址

##### 3.4.2 配置步骤

1. 申明一个cache缓存节点的路径

   ```
   proxy_cache_path /usr/local/openresty/nginx/cache_temp levels=1:2 keys_zone=tmp_cache:100m inactive=7d max_size=100g;
   ```

   参数说明：

   - /usr/local/openresty/nginx/cache_temp：设置缓存文件路径
   - levels：目录设置两层结构用来缓存
   - keys_zone：指定了一个叫tmp_cache的缓存区，并且设置了100m的内存用来存储缓存key到文件路径的位置 
   - inactive：缓存文件超过7天后自动释放淘汰
   - max_size：缓存文件总大小超过100g后自动释放淘汰

2. location内加入

   ```
   proxy_cache tmp_cache;   //缓存节点名称
   proxy_cache_valid 200 206 304 302 7d;		//只缓存指定的状态码的请求
   proxy_cache_key $request_uri;			//将请求uri作为缓存的key
   ```

##### 3.4.3 发现问题 - 效果反而下降：

​		这种缓存方法读取的Nginx的本地文件，并没有将文件缓存到Nginx的内存中，所以效果并不好，不推荐。

**总结：**

- **在大型的应用集群中若对Redis访问过度依赖，会因为应用服务器到Redis之间的网络带宽产生瓶颈**

  针对读请求导致的性能瓶颈，只需要在数据写入过程中复制一份，数据就变成两份，数据读能力就扩展了一倍。
  针对写请求，redis是key-value存储结构，通过对写请求key做sharding，分散到不同的master实例上，解决写的瓶颈问题。

- 架构的越顶层性能越高，占用的系统资源越昂贵，更新机制越难

- 架构的底层更容易集中式的存储数据，但是性能最差

![](E:\程序人生\个人学习笔记\学习笔记图床\QQ图片20201206175610.png)

所以，**无通用的解决方案，结合场景选择合适的架构**

### 4. 查询性能优化 - 页面静态化

![](E:\程序人生\个人学习笔记\学习笔记图床\页面静态化.png)

#### 4.1 实现静态资源CDN及其原理解析

- DNS用CNAME解析到源站
- 回源缓存设置
- 强推失效

##### 4.1.1 cache-control响应头

<img src="E:\程序人生\个人学习笔记\学习笔记图床\cache-control.png" style="zoom:80%;" />

- private：客户端可以缓存
- publlic：客户端和代理服务器都可以缓存
- max-age=xxx：缓存的内容将在XXX秒后失效
- no-cache：强制向服务端再验证一次
- no-store：不缓存请求的任何返回内容

##### 4.1.2 有效性判断

- ETag：资源唯一标识
- If-None-Match：客户端发送的匹配ETag标识符
- Last-modified：资源最后被修改的时间
- If-Modified-Since：客户端发送的匹配资源最后修改时间的标识符

<img src="E:\程序人生\个人学习笔记\学习笔记图床\QQ图片20201206194105.png" style="zoom:80%;" />

**协商机制**：比较Last-modified和ETag到服务端，若服务端判断没变化则304不返回数据，否则200返回数据

##### 4.1.3 浏览器的刷新方式

- 回车刷新或a链接
- F5刷新或command+R刷新

- ctrl+F5或command+shift+R刷新

##### 4.1.4 CDN自定缓存策略

- 可自定义目录过期时间
- 可自定义后缀名过期时间
- 可自定义对应策略权重
- 可通过界面或api强制cdn对应目录刷新（非保成功）

<img src="E:\程序人生\个人学习笔记\学习笔记图床\QQ图片20201206195202.png" style="zoom: 80%;" />

##### 4.1.5 静态资源部署策略

**（一）**

- css,js,img等元素使用版本号部署（不便利，维护困难）

- css,js,img等元素使用带摘要部署（存在先部署html还是先部署资源的覆盖问题）
- css,js,img等元素使用摘要做文件名部署，新老版本并存且可回滚，资源部署完后在部署html（推荐）

**（二）**

- 对应静态资源保持生命周期内不会变，max-age可设置的很长，无视失效更新周期
- html文件设置no-cache或较短max-age，以便于更新
- html文件仍然设置较长的mac-age，依靠动态的获取版本号请求发送到后端，异步下载最新的版本号的html后展示渲染在前端

**（三）**

- 动态请求也可以静态化成json资源推送到cdn上
- 依靠异步请求获取后端节点对应的资源状态做紧急下架处理
- 可通过跑批紧急推送cdn内容以使其下架等操作

#### 4.2 全页面静态化（*）

##### 4.2.1 概念

在服务端完成html，css，甚至js的load渲染成纯html文件后，直接以静态资源的方式部署到cdn上。

##### 4.2.2 phantomjs应用 - 无头浏览器

- 修改需要全页面静态化的实现，采用initView和hasInit方式防止多次初始化
- 编写对应轮询生成内容方式
- 将全静态化页面生成后推送到cdn

### 5. 交易性能优化 - 缓存库存

#### 交易性能瓶颈

- 交易验证完全依赖于数据库
- 库存行锁
- 后置处理逻辑

#### 5.1 优化交易验证

- 用户风控策略优化：策略缓存模型化

- 活动校验策略优化：引入活动发布流程，模型缓存化，紧急下线功能

  **具体实现见代码优化**

#### 5.2 优化库存行锁

##### 5.2.1 解决方案

- 扣减库存缓存化

- 异步同步数据库

- 库存数据库最终一致性保证

  **具体实现见代码优化**

##### 5.2.2 部署RocketMq

- docker-compose

```
version: '3.3'
services:
  rmqnamesrv:
    image: foxiswho/rocketmq:server
    container_name: rmqnamesrv
    ports:
      - 9876:9876
    volumes:
      - ./data/logs:/opt/logs
      - ./data/store:/opt/store
    networks:
        rmq:
          aliases:
            - rmqnamesrv
  
  rmqbroker:
    image: foxiswho/rocketmq:broker
    container_name: rmqbroker
    ports:
      - 10909:10909
      - 10911:10911
    volumes:
      - ./data/logs:/opt/logs
      - ./data/store:/opt/store
      - ./data/brokerconf/broker.conf:/etc/rocketmq/broker.conf
    environment:
        NAMESRV_ADDR: "rmqnamesrv:9876"
        JAVA_OPTS: " -Duser.home=/opt"
        JAVA_OPT_EXT: "-server -Xms128m -Xmx128m -Xmn128m"
    command: mqbroker -c /etc/rocketmq/broker.conf
    depends_on:
      - rmqnamesrv
    networks:
      rmq:
        aliases:
          - rmqbroker

  rmqconsole:
    image: styletang/rocketmq-console-ng
    container_name: rmqconsole
    ports:
    	- 8080:8080
    environment:
        JAVA_OPTS: "-Drocketmq.namesrv.addr=rmqnamesrv:9876 -Dcom.rocketmq.sendMessageWithVIPChannel=false"
    depends_on:
        - rmqnamesrv
    networks:
        rmq:
          aliases:
            - rmqconsole

networks:
    rmq:
        driver: bridge
```

```
docker update --restart=always XXX
```

**具体实现见代码优化**

##### 5.2.3 问题

- 异步消息发送失败
- 扣减操作执行失败
- 下单失败无法正确回滚库存

### 6. 交易性能优化 - 事务型消息 

- 事务型消息应用
- 库存流水状态
- 库存售罄处理方案

### 7. 流量削峰技术

#### 7.1 秒杀令牌

##### 7.1.1 原理

- 秒杀接口需要依靠令牌才能进入
- 秒杀的令牌由秒杀活动模块负责生成
- 秒杀活动模块对秒杀令牌生成全权处理，逻辑收口
- 秒杀下单前需要先获取秒杀令牌

#### 7.2 秒杀大闸

##### 7.2.1 原理

- 依靠秒杀令牌的授权原理定制化发牌逻辑，做到大闸功能
- 根据秒杀商品初始库存颁发对应数量令牌，控制大闸流量
- 用户风控策略前置到秒杀令牌发放中
- 库存售罄判断前置到秒杀令牌发放中

#### 7.3 队列泄洪

##### 7.3.1 原理

- 依靠排队去限制并发流量
- 依靠排队和下游拥塞窗口程度调整队列释放流量大小
- 典型案例：支付宝银行网关队列

##### 7.3.2 代码实现

- 本地：将队列维护在本地内存中

```
    private ExecutorService executorService;

    /***
     * 同步调用线程池的submit方法
     * 拥塞窗口为20的等待队列，实现队列泄洪
     */
    @PostConstruct
    public void init(){
        executorService = Executors.newFixedThreadPool(20);
    }
```

- 分布式：将队列设置到外部redis中

### 8. 防刷限流

#### 8.1 验证码生成与验证技术 - 错峰

- 包装秒杀令牌前置，验证码错峰
- 数学公式验证码生成器

#### 8.2 限流原理与实现

##### 8.2.1 方案

- 限并发
- 令牌桶算法（可以应对突发流量）
- 漏桶算法（固定速率）

##### 8.2.2  实现

```
    /***
     * 限流
     */
    @PostConstruct
    public void init(){
        orderCreateRateLimiter = RateLimiter.create(300);
    }
```

#### 8.3 防黄牛技术

##### 8.3.1 传统防刷

- 限制一个会话在一定时间内接口调用次数（无法解决多会话接入的问题）

- 限制一个ip在一定时间内接口调用次数（不好控制，容易误伤）

##### 8.3.2 设备指纹

##### 8.3.3 凭证系统