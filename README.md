## 项目目录介绍

```apl
cloudreview
├── cloudreview-alibaba ----- cloud alibaba相关组件整合使用例子
├── cloudreview-alibaba-provider-payment-gateway -----  cloudreview-alibaba中cloudreview-alibaba-consumer-feign-order80使用的gateway网关
├── cloudreview-eureka-server ----- cloud中搭建两个eureka server集群的例子
├── cloudreview-ek-rb-feign ----- cloud中使用 eureka+ribbon+RestTemplate 和 eureka+openfeign 的例子
├── cloudreview-hystrix ----- cloud中使用 eureka+openfeign+hystrix 和 hystrix dashboard搭建 的例子
└── cloudreview-provider-payment-gateway ----- cloudreview-ek-rb-feign中cloudreview-consumer-feign-order80使用的gateway网关
```

## Cloud组件停更替换

停更引发的“升级惨案”

停更不停用
被动修复bugs
不再接受合并请求
不再发布新版本
Cloud升级

- 服务注册中心

  × Eureka
  √ Zookeeper
  √ Consul
  √ Nacos

- 服务调用

  √ Ribbon
  √ LoadBalancer

- 服务调用2

  × Feign
  √ OpenFeign

- 服务降级

  × Hystrix
  √ resilience4j
  √ sentienl

- 服务网关

  × Zuul
  ! Zuul2
  √ gateway

- 服务配置

  × Config
  √ Nacos

- 服务总线

  × Bus
  √ Nacos

搭建基础的几个模块，实现多模块调用

![](https://s3.bmp.ovh/imgs/2022/07/14/b99f8c0228f35d7b.png)

![](https://s3.bmp.ovh/imgs/2022/07/14/c30f94c1ab9f802e.png)

![](https://s3.bmp.ovh/imgs/2022/07/14/c05c77bad8a41a4a.png)

在父工程模块的pom文件加上本地私服镜像地址

```xml
	<distributionManagement>
        <repository>
            <id>releases</id>
            <url>http://localhost:8081/repository/cloudreview-release/</url>
        </repository>
        <snapshotRepository>
            <id>snapshots</id>
            <url>http://localhost:8081/repository/cloudreview-snapshot/</url>
        </snapshotRepository>
    </distributionManagement>
```

在不需要发布到本地私服仓库的模块加上

```xml
	<properties>
        <maven.deploy.skip>true</maven.deploy.skip>
    </properties>
```



cloud alibaba组件版本选择

https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E 



## Eureka



#### 什么是服务治理

Spring Cloud封装了Netflix 公司开发的Eureka模块来实现服务治理

在传统的RPC远程调用框架中，管理每个服务与服务之间依赖关系比较复杂，管理比较复杂，所以需要使用服务治理，管理服务于服务之间依赖关系，可以实现服务调用、负载均衡、容错等，实现服务发现与注册。

#### 什么是服务注册与发现

Eureka采用了CS的设计架构，Eureka Sever作为服务注册功能的服务器，它是服务注册中心。而系统中的其他微服务，使用Eureka的客户端连接到 Eureka Server并维持心跳连接。这样系统的维护人员就可以通过Eureka Server来监控系统中各个微服务是否正常运行。

在服务注册与发现中，有一个注册中心。当服务器启动的时候，会把当前自己服务器的信息比如服务地址通讯地址等以别名方式注册到注册中心上。另一方(消费者服务提供者)，以该别名的方式去注册中心上获取到实际的服务通讯地址，然后再实现本地RPC调用RPC远程调用框架核心设计思想:在于注册中心，因为使用注册中心管理每个服务与服务之间的一个依赖关系(服务治理概念)。在任何RPC远程框架中，都会有一个注册中心存放服务地址相关信息(接口地址)

#### Eureka包含两个组件:Eureka Server和Eureka Client

#### Eureka Server提供服务注册服务

各个微服务节点通过配置启动后，会在EurekaServer中进行注册，这样EurekaServer中的服务注册表中将会存储所有可用服务节点的信息，服务节点的信息可以在界面中直观看到。

#### EurekaClient通过注册中心进行访问

它是一个Java客户端，用于简化Eureka Server的交互，客户端同时也具备一个内置的、使用轮询(round-robin)负载算法的负载均衡器。在应用启动后，将会向Eureka Server发送心跳(默认周期为30秒)。如果Eureka Server在多个心跳周期内没有接收到某个节点的心跳，EurekaServer将会从服务注册表中把这个服务节点移除（默认90秒)

### EurekaServer服务端

1.创建一个父工程  cloudreview-eureka-server， 在下面创建cloudreview-eureka-server7001的Maven工程

2.pom.xml 依赖

```xml
    <dependencies>

        <!-- SpringCloud eureka server 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>

        <!-- 项目本身api通用整合 依赖 -->
        <dependency>
            <groupId>org.elianacc</groupId>
            <artifactId>cloudreview-api-commons</artifactId>
            <version>${project.version}</version>
        </dependency>

        <!-- SpringBoot SpringMVC 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- SpringBoot 指标监控 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- SpringBoot lombok 依赖 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- SpringBoot Spring测试 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

    </dependencies>
```

3.添加application.yml

```yaml
server:
  port: 7001

eureka:
  #server:
    # 关闭自我保护机制，保证不可用服务被及时踢除
    #enable-self-preservation: false
    #eviction-interval-timer-in-ms: 2000
  instance:
    # eureka服务端的实例名称  单机设置localhost
    hostname: locathost
  client:
    # false表示不向注册中心注册自己。
    register-with-eureka: false
    # false表示自己端就是注册中心，我的职责就是维护服务实例，并不需要去检索服务
    fetch-registry: false
    service-url:
      # 设置与Eureka server交互的地址查询服务和注册服务都需要依赖这个地址-集群指向其它eureka
      #defaultZone: http://eureka7002.com:7002/eureka/
      # 单机就是7001自己
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

4.主启动

```java
/**
 * 7001 Eureka服务端 启动类
 *
 * @author CNY
 * @since 2021-10-20
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaMain7001 {
    public static void main(String[] args) {
        SpringApplication.run(EurekaMain7001.class, args);
    }
}
```

5.测试运行`EurekaMain7001`，浏览器输入`http://localhost:7001/`回车，会查看到Spring Eureka服务主页。

### 支付微服务8001入驻进EurekaServer

EurekaClient端cloudreview-provider-payment8001将注册进EurekaServer成为服务提供者provider，类似学校对外提供授课服务。

1.修改cloudreview-provider-payment8001

2.改POM

添加spring-cloud-starter-netflix-eureka-client依赖

```xml
        <!-- SpringCloud eureka client 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
```

3.写YML

```yaml
eureka:
  client:
    # 表示是否将自己注册进Eurekaserver默认为true。
    register-with-eureka: true
    # 是否从EurekaServer抓取已有的注册信息，默认为true。单节点无所谓，集群必须设置为true才能配合ribbon使用负载均衡
    fetchRegistry: true
    service-url:
      #defaultZone: http://eureka7001.com:7001/eureka, http://eureka7002.com:7002/eureka
      # 单机
      defaultZone: http://localhost:7001/eureka
```

4.主启动

```java
/**
 * 8001启动类
 *
 * @author CNY
 * @since 2021-10-18
 */
@SpringBootApplication
@MapperScan(basePackages = {"org.elianacc.cloudreview.dao"})
@EnableEurekaClient
public class PaymentMain8001 {

    public static void main(String[] args) {
        SpringApplication.run(PaymentMain8001.class, args);
    }

}
```

5.测试

启动cloudreview-provider-payment8001和cloudreview-eureka-server7001工程。

浏览器输入 - http://localhost:7001/ 主页内的Instances currently registered with Eureka会显示cloudreview-provider-payment8001的配置文件application.yml设置的应用名cloudreview-provider-payment

6.自我保护机制

EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY’RE NOT. RENEWALS ARELESSER THAN THRESHOLD AND HENCFT ARE NOT BEING EXPIRED JUST TO BE SAFE.

紧急情况！**EUREKA可能错误地声称实例在没有启动的情况下启动了**。续订小于阈值，因此实例不会为了安全而过期。

### 订单微服务80入驻进EurekaServer

EurekaClient端cloudreview-consumer-order80将注册进EurekaServer成为服务消费者consumer，类似来上课消费的同学

1.cloudreview-consumer-order80

2.POM 添加依赖

```xml
        <!-- SpringCloud eureka client 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
```

3.YML

```yaml
spring:
  application:
    name: cloudreview-consumer-order
    
eureka:
  client:
    # 表示是否将自己注册进Eurekaserver默认为true。
    register-with-eureka: true
    # 是否从EurekaServer抓取已有的注册信息，默认为true。单节点无所谓，集群必须设置为true才能配合ribbon使用负载均衡
    fetchRegistry: true
    service-url:
      #defaultZone: http://eureka7001.com:7001/eureka, http://eureka7002.com:7002/eureka
      # 单机
      defaultZone: http://localhost:7001/eureka
```

4.主启动

```java
/**
 * 80启动类
 *
 * @author CNY
 * @since 2021-10-19
 */
@SpringBootApplication
@EnableEurekaClient
public class OrderMain80 {

    public static void main(String[] args) {
        SpringApplication.run(OrderMain80.class, args);
    }

}
```

5.测试

启动cloudreview-provider-payment8001、cloudreview-eureka-server7001和cloudreview-consumer-order80这三工程。
浏览器输入 http://localhost:7001 , 在主页的Instances currently registered with Eureka将会看到cloudreview-provider-payment8001、cloudreview-consumer-order80两个工程名。

### Eureka集群原理说明

服务注册：将服务信息注册进注册中心

服务发现：从注册中心上获取服务信息

实质：存key服务命取value闭用地址

1先启动eureka注册中心

2启动服务提供者payment支付服务

3支付服务启动后会把自身信息(比服务地址以别名方式注册进eureka

4消费者order服务在需要调用接口时，使用服务别名去注册中心获取实际的RPC远程调用地址

5消去者导调用地址后，底层实际是利用HttpClient技术实现远程调用

6消费者取到服务地址后会缓存在本地jvm内存中，默认每间隔30秒更新—次服务调用地址

问题:微服务RPC远程服务调用最核心的是什么
高可用，试想你的注册中心只有一个only one，万一它出故障了，会导致整个为服务环境不可用。

解决办法：搭建Eureka注册中心集群，实现负载均衡+故障容错。

**互相注册，相互守望。**

### Eureka集群环境构建

创建cloudreview-eureka-server7002工程，同上

- 找到C:\Windows\System32\drivers\etc路径下的hosts文件，修改映射配置添加进hosts文件

```
127.0.0.1 eureka7001.com
127.0.0.1 eureka7002.com
```

- 修改cloudreview-eureka-server7001配置文件

```yaml
server:
  port: 7001

eureka:
  #server:
    # 关闭自我保护机制，保证不可用服务被及时踢除
    #enable-self-preservation: false
    #eviction-interval-timer-in-ms: 2000
  instance:
    # eureka服务端的实例名称  单机设置localhost
    #hostname: locathost
    hostname: eureka7001.com
  client:
    # false表示不向注册中心注册自己。
    register-with-eureka: false
    # false表示自己端就是注册中心，我的职责就是维护服务实例，并不需要去检索服务
    fetch-registry: false
    service-url:
      # 设置与Eureka server交互的地址查询服务和注册服务都需要依赖这个地址-集群指向其它eureka
      defaultZone: http://eureka7002.com:7002/eureka/
      # 单机就是7001自己
      #defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

cloudreview-eureka-server7002配置文件

```yaml
server:
  port: 7002

eureka:
  #server:
    # 关闭自我保护机制，保证不可用服务被及时踢除
    #enable-self-preservation: false
    #eviction-interval-timer-in-ms: 2000
  instance:
    # eureka服务端的实例名称  单机设置localhost
    #hostname: locathost
    hostname: eureka7002.com
  client:
    # false表示不向注册中心注册自己。
    register-with-eureka: false
    # false表示自己端就是注册中心，我的职责就是维护服务实例，并不需要去检索服务
    fetch-registry: false
    service-url:
      # 设置与Eureka server交互的地址查询服务和注册服务都需要依赖这个地址-集群指向其它eureka
      defaultZone: http://eureka7001.com:7001/eureka/
      # 单机就是7001自己
      #defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

访问   http://eureka7001.com:7001    http://eureka7002.com:7002

### 订单支付两微服务注册进Eureka集群

- 将支付服务8001微服务，订单服务80微服务发布到上面2台Eureka集群配置中

将它们的配置文件的eureka.client.service-url.defaultZone进行修改

```yaml
eureka:
  client:
    # 表示是否将自己注册进Eurekaserver默认为true。
    register-with-eureka: true
    # 是否从EurekaServer抓取已有的注册信息，默认为true。单节点无所谓，集群必须设置为true才能配合ribbon使用负载均衡
    fetchRegistry: true
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka, http://eureka7002.com:7002/eureka
      # 单机
      #defaultZone: http://localhost:7001/eureka
```

### 支付微服务集群配置

参考cloudreview-provider-payment8001 创建

**负载均衡**

cloudreview-consumer-order80订单服务访问地址不能写死，controller修改

```java
//    public static final String PAYMENT_URL = "http://localhost:8001";  // 单机支付服务
    public static final String PAYMENT_URL = "http://CLOUDREVIEW_PROVIDER_PAYMENT";  // 多个支付服务
```

使用@LoadBalanced注解赋予RestTemplate负载均衡的能力

测试

先要启动EurekaServer，7001/7002服务

再要启动服务提供者provider，8001/8002服务

浏览器输入 - http://localhost/consumer/payment/get/31

结果：负载均衡效果达到，8001/8002端口交替出现

Ribbon和Eureka整合后Consumer可以直接调用服务而不用再关心地址和端口号，且该服务还有负载功能。

**相互注册，相互守望**

![](https://img-blog.csdnimg.cn/img_convert/94c4c3eca8c2f9eb7497fe643b9b0622.png)

### actuator微服务信息完善

主机名称：服务名称修改（也就是将IP地址，换成可读性高的名字）

修改cloudreview-provider-payment8001，cloudreview-provider-payment8002

修改部分 - YML - eureka.instance.instance-id

```yaml
eureka:
  ...
  instance:
    instance-id: payment8001 #添加此处
```

```yaml
eureka:
  ...
  instance:
    instance-id: payment8002 #添加此处
```

修改之后

eureka主页将显示payment8001，payment8002代替原来显示的IP地址。

访问信息有IP信息提示，（就是将鼠标指针移至payment8001，payment8002名下，会有IP地址提示）

修改部分 - YML - eureka.instance.prefer-ip-address

```yaml
eureka:
  ...
  instance:
    instance-id: payment8001 
    prefer-ip-address: true #添加此处
```

```yaml
eureka:
  ...
  instance:
    instance-id: payment8002
    prefer-ip-address: true #添加此处
```

### 服务发现Discovery

对于注册进eureka里面的微服务，可以通过服务发现来获得该服务的信息

- 修改cloudreview-provider-payment8001的Controller

```java
@RestController
@Slf4j
public class PaymentController{
	...
    
    @Autowired
    private DiscoveryClient discoveryClient;

    ...

    @GetMapping(value = "/payment/discovery")
    public Object discovery()
    {
        List<String> services = discoveryClient.getServices();
        for (String element : services) {
            log.info("*****element: "+element);
        }

        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUDREVIEW-PROVIDER-PAYMENT");
        for (ServiceInstance instance : instances) {
            log.info(instance.getServiceId()+"\t"+instance.getHost()+"\t"+instance.getPort()+"\t"+instance.getUri());
        }

        return this.discoveryClient;
    }
}

```

- 8001主启动类

```java
@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient//添加该注解
public class PaymentMain001 {

    public static void main(String[] args) {
        SpringApplication.run(PaymentMain001.class, args);
    }
}

```

- 自测

先要启动EurekaSeryer

再启动8001主启动类，需要稍等一会儿

浏览器输入http://localhost:8001/payment/discovery

### Eureka自我保护理论知识

保护模式主要用于一组客户端和Eureka Server之间存在网络分区场景下的保护。一旦进入保护模式，Eureka Server将会尝试保护其服务注册表中的信息，不再删除服务注册表中的数据，也就是不会注销任何微服务。

如果在Eureka Server的首页看到以下这段提示，则说明Eureka进入了保护模式:

EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY’RE NOT. RENEWALS ARE LESSER THANTHRESHOLD AND HENCE THE INSTANCES ARE NOT BEING EXPIRED JUSTTO BE SAFE

导致原因

一句话：某时刻某一个微服务不可用了，Eureka不会立刻清理，依旧会对该微服务的信息进行保存。

属于CAP里面的AP分支。

为什么会产生Eureka自我保护机制?

为了EurekaClient可以正常运行，防止与EurekaServer网络不通情况下，EurekaServer不会立刻将EurekaClient服务剔除

什么是自我保护模式?

默认情况下，如果EurekaServer在一定时间内没有接收到某个微服务实例的心跳，EurekaServer将会注销该实例(默认90秒)。但是当网络分区故障发生(延时、卡顿、拥挤)时，微服务与EurekaServer之间无法正常通信，以上行为可能变得非常危险了——因为微服务本身其实是健康的，此时本不应该注销这个微服务。Eureka通过“自我保护模式”来解决这个问题——当EurekaServer节点在短时间内丢失过多客户端时(可能发生了网络分区故障)，那么这个节点就会进入自我保护模式。

自我保护机制∶默认情况下EurekaClient定时向EurekaServer端发送心跳包

如果Eureka在server端在一定时间内(默认90秒)没有收到EurekaClient发送心跳包，便会直接从服务注册列表中剔除该服务，但是在短时间( 90秒中)内丢失了大量的服务实例心跳，这时候Eurekaserver会开启自我保护机制，不会剔除该服务（该现象可能出现在如果网络不通但是EurekaClient为出现宕机，此时如果换做别的注册中心如果一定时间内没有收到心跳会将剔除该服务，这样就出现了严重失误，因为客户端还能正常发送心跳，只是网络延迟问题，而保护机制是为了解决此问题而产生的)。

在自我保护模式中，Eureka Server会保护服务注册表中的信息，不再注销任何服务实例。

它的设计哲学就是宁可保留错误的服务注册信息，也不盲目注销任何可能健康的服务实例。一句话讲解：好死不如赖活着。

综上，自我保护模式是一种应对网络异常的安全保护措施。它的架构哲学是宁可同时保留所有微服务（健康的微服务和不健康的微服务都会保留）也不盲目注销任何健康的微服务。使用自我保护模式，可以让Eureka集群更加的健壮、稳定。

### 怎么禁止自我保护

- 在eurekaServer端7001处设置关闭自我保护机制

出厂默认，自我保护机制是开启的

使用`eureka.server.enable-self-preservation = false`可以禁用自我保护模式

```yaml
eureka:
  ...
  server:
    #关闭自我保护机制，保证不可用服务被及时踢除
    enable-self-preservation: false
    eviction-interval-timer-in-ms: 2000
```

关闭效果：

spring-eureka主页会显示出一句：

THE SELF PRESERVATION MODE IS TURNED OFF. THIS MAY NOT PROTECT INSTANCE EXPIRY IN CASE OF NETWORK/OTHER PROBLEMS.

- 生产者客户端eureakeClient端8001

默认：

```
eureka.instance.lease-renewal-interval-in-seconds=30
eureka.instance.lease-expiration-duration-in-seconds=90
```

```yaml
eureka:
  ...
  instance:
    instance-id: payment8001
    prefer-ip-address: true
    #心跳检测与续约时间
    #开发时没置小些，保证服务关闭后注册中心能即使剔除服务
    #Eureka客户端向服务端发送心跳的时间间隔，单位为秒(默认是30秒)
    lease-renewal-interval-in-seconds: 1
    #Eureka服务端在收到最后一次心跳后等待时间上限，单位为秒(默认是90秒)，超时将剔除服务
    lease-expiration-duration-in-seconds: 2
```

- 测试
  - 7001和8001都配置完成
  - 先启动7001再启动8001

结果：先关闭8001，马上被删除了

----------------------------------------------------------------------------------------------Eureka  结束------------------------------------------------------------------------------------------------------



## Ribbon



### Ribbon入门介绍

Spring Cloud Ribbon是基于Netflix Ribbon实现的一套客户端负载均衡的工具。

简单的说，Ribbon是Netflix发布的开源项目，主要功能是提供客户端的软件负载均衡算法和服务调用。Ribbon客户端组件提供一系列完善的配置项如连接超时，重试等。

简单的说，就是在配置文件中列出Load Balancer(简称LB)后面所有的机器，Ribbon会自动的帮助你基于某种规则(如简单轮询，随机连接等）去连接这些机器。我们很容易使用Ribbon实现自定义的负载均衡算法。

Ribbon目前也进入维护模式。

Ribbon未来可能被Spring Cloud LoadBalacer替代。

LB负载均衡(Load Balance)是什么

简单的说就是将用户的请求平摊的分配到多个服务上，从而达到系统的HA (高可用)。

常见的负载均衡有软件Nginx，LVS，硬件F5等。

Ribbon本地负载均衡客户端VS Nginx服务端负载均衡区别

Nginx是服务器负载均衡，客户端所有请求都会交给nginx，然后由nginx实现转发请求。即负载均衡是由服务端实现的。
Ribbon本地负载均衡，在调用微服务接口时候，会在注册中心上获取注册信息服务列表之后缓存到JVM本地，从而在本地实现RPC远程服务调用技术。

集中式LB

即在服务的消费方和提供方之间使用独立的LB设施(可以是硬件，如F5, 也可以是软件，如nginx)，由该设施负责把访问请求通过某种策略转发至服务的提供方;

进程内LB

将LB逻辑集成到消费方，消费方从服务注册中心获知有哪些地址可用，然后自己再从这些地址中选择出一个合适的服务器。

Ribbon就属于进程内LB，它只是一个类库，集成于消费方进程，消费方通过它来获取到服务提供方的地址。

一句话

负载均衡 + RestTemplate调用

### Ribbon的负载均衡和Rest调用

总结：Ribbon其实就是一个软负载均衡的客户端组件，它可以和其他所需请求的客户端结合使用，和Eureka结合只是其中的一个实例。

![](https://img-blog.csdnimg.cn/img_convert/145b915e56a85383b3ad40f0bb2256e0.png)



Ribbon在工作时分成两步：

第一步先选择EurekaServer ,它优先选择在同一个区域内负载较少的server。

第二步再根据用户指定的策略，在从server取到的服务注册列表中选择一个地址。

其中Ribbon提供了多种策略：比如轮询、随机和根据响应时间加权。

POM

先前工程项目没有引入spring-cloud-starter-ribbon也可以使用ribbon。

这是因为spring-cloud-starter-netflix-eureka-client自带了spring-cloud-starter-ribbon引用。



### Ribbon默认自带的负载规则

lRule：根据特定算法中从服务列表中选取一个要访问的服务

![](https://img-blog.csdnimg.cn/img_convert/87243c00c0aaea211819c0d8fc97e445.png)

- RoundRobinRule 轮询
- RandomRule 随机
- RetryRule 先按照RoundRobinRule的策略获取服务，如果获取服务失败则在指定时间内会进行重试
- WeightedResponseTimeRule 对RoundRobinRule的扩展，响应速度越快的实例选择权重越大，越容易被选择
- BestAvailableRule 会先过滤掉由于多次访问故障而处于断路器跳闸状态的服务，然后选择一个并发量最小的服务
- AvailabilityFilteringRule 先过滤掉故障实例，再选择并发较小的实例
- ZoneAvoidanceRule 默认规则,复合判断server所在区域的性能和server的可用性选择服务器

### Ribbon负载规则替换（使用其他的自带规则）

1.修改cloudreview-consumer-order80 

2.注意配置细节

官方文档明确给出了警告:

这个自定义配置类不能放在@ComponentScan所扫描的当前包下以及子包下，

否则我们自定义的这个配置类就会被所有的Ribbon客户端所共享，达不到特殊化定制的目的了。（**也就是说不要将Ribbon配置类与主启动类同包**）

新建自定义规则

```java
package org.rbrule;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述
 *
 * @author CNY
 * @since 2021-10-21
 */
@Configuration
public class MySelfRule {

    @Bean
    public IRule myRule() {
        return new RandomRule();
    }
}
```

主启动类添加@RibbonClient

```java
/**
 * 80启动类
 *
 * @author CNY
 * @since 2021-10-19
 */
@SpringBootApplication
@EnableEurekaClient
@RibbonClient(name = "CLOUDREVIEW-PROVIDER-PAYMENT", configuration = MySelfRule.class)
public class OrderMain80 {

    public static void main(String[] args) {
        SpringApplication.run(OrderMain80.class, args);
    }

}
```

开启cloudreview-eureka-server7001，cloudreview-consumer-order80，cloudreview-provider-payment8001，cloudreview-provider-payment8002

浏览器-输入http://localhost/consumer/payment/get/1

返回结果中的serverPort在8001与8002两种间反复横跳。（随机的）



---------------------------------------------------------------------------------------------Ribbon 结束--------------------------------------------------------------------------------------------------------



## OpenFeign



Feign能干什么

Feign旨在使编写Java Http客户端变得更容易。

前面在使用Ribbon+RestTemplate时，利用RestTemplate对http请求的封装处理，形成了一套模版化的调用方法。但是在实际开发中，由于对服务依赖的调用可能不止一处，往往一个接口会被多处调用，所以通常都会针对每个微服务自行封装一些客户端类来包装这些依赖服务的调用。所以，Feign在此基础上做了进一步封装，由他来帮助我们定义和实现依赖服务接口的定义。在Feign的实现下，我们只需创建一个接口并使用注解的方式来配置它(以前是Dao接口上面标注Mapper注解,现在是一个微服务接口上面标注一个Feign注解即可)，即可完成对服务提供方的接口绑定，简化了使用Spring cloud Ribbon时，自动封装服务调用客户端的开发量。

**Feign集成了Ribbon**

利用Ribbon维护了Payment的服务列表信息，并且通过轮询实现了客户端的负载均衡。而与Ribbon不同的是，通过feign只需要定义服务绑定接口且以声明式的方法，优雅而简单的实现了服务调用。

**Feign和OpenFeign两者区别**

Feign是Spring Cloud组件中的一个轻量级RESTful的HTTP服务客户端Feign内置了Ribbon，用来做客户端负载均衡，去调用服务注册中心的服务。Feign的使用方式是:使用Feign的注解定义接口，调用这个接口，就可以调用服务注册中心的服务。

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-feign</artifactId>
</dependency>
```

OpenFeign是Spring Cloud在Feign的基础上支持了SpringMVC的注解，如@RequesMapping等等。OpenFeign的@Feignclient可以解析SpringMVc的@RequestMapping注解下的接口，并通过动态代理的方式产生实现类，实现类中做负载均衡并调用其他服务。

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

### OpenFeign服务调用

接口+注解：微服务调用接口 + @FeignClient

1.新建cloudreview-consumer-feign-order80

2.POM

```xml
    <dependencies>

        <!-- SpringCloud openfeign 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>

        <!-- SpringCloud eureka client 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

        <!-- 项目本身api通用整合 依赖 -->
        <dependency>
            <groupId>org.elianacc</groupId>
            <artifactId>cloudreview-api-commons</artifactId>
            <version>${project.version}</version>
        </dependency>

        <!-- SpringBoot SpringMVC 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- SpringBoot 指标监控 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- SpringBoot lombok 依赖 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- SpringBoot Spring测试 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

    </dependencies>
```

3.YML

```yaml
server:
  port: 80

spring:
  application:
    name: cloudreview-consumer-feign-order

eureka:
  client:
    # 表示是否将自己注册进Eurekaserver默认为true。
    register-with-eureka: true
    # 是否从EurekaServer抓取已有的注册信息，默认为true。单节点无所谓，集群必须设置为true才能配合ribbon使用负载均衡
    fetchRegistry: true
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka, http://eureka7002.com:7002/eureka
  instance:
    # 显示主机名称修改
    instance-id: order80
    # 访问信息IP信息提示
    prefer-ip-address: true
    # Eureka客户端向服务端发送心跳的时间间隔，单位为秒(默认是30秒)
    lease-renewal-interval-in-seconds: 3
    # Eureka服务端在收到最后一次心跳后等待时间上限，单位为秒(默认是90秒)，超时将剔除服务
    lease-expiration-duration-in-seconds: 5
```

4.主启动

```java
/**
 * 80（feign） 订单 启动类
 *
 * @author CNY
 * @since 2021-10-25
 */
@SpringBootApplication
@EnableFeignClients
public class OrderFeignMain80 {
    public static void main(String[] args) {
        SpringApplication.run(OrderFeignMain80.class, args);
    }
}
```

5.业务类

业务逻辑接口+@FeignClient配置调用provider服务

新建PaymentFeignService接口并新增注解@FeignClient

```java
@Component
@FeignClient(value = "CLOUDREVIEW-PROVIDER-PAYMENT")
public interface PaymentFeignService {

    @GetMapping(value = "/payment/get/{id}")
    public ApiResult getPaymentById(@PathVariable("id") Long id);

}
```

6.测试

先启动2个eureka集群7001/7002

再启动2个微服务8001/8002

启动OpenFeign启动

http://localhost/consumer/payment/get/1

Feign自带负载均衡配置项

### OpenFeign超时控制

**超时设置，故意设置超时演示出错情况**

1.服务提供方8001/8002故意写暂停程序

```java
@RestController
@Slf4j
public class PaymentController {
    
    ...
    
    @Value("${server.port}")
    private String serverPort;

    ...
    
    @GetMapping(value = "/payment/feign/timeout")
    public String paymentFeignTimeout()
    {
        // 业务逻辑处理正确，但是需要耗费3秒钟
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return serverPort;
    }
    
    ...
}
```

2.服务消费方80添加超时方法PaymentFeignService

```java
@Component
@FeignClient(value = "CLOUDREVIEW-PROVIDER-PAYMENT")
public interface PaymentFeignService{

    ...

    @GetMapping(value = "/payment/feign/timeout")
    public String paymentFeignTimeout();
}
```

3.服务消费方80添加超时方法OrderFeignController

```java
	@GetMapping(value = "/consumer/payment/feign/timeout")
    public String paymentFeignTimeout() {
        // OpenFeign客户端一般默认时等待1秒钟（可以配置更改，有些复杂业务需要更多时间）
        return paymentFeignService.paymentFeignTimeout();
    }
```

4.测试：

多次刷新http://localhost/consumer/payment/feign/timeout

将会跳出错误Spring Boot默认错误页面，主要异常：feign.RetryableException:Read timed out executing GET http://CLOUDREVIEW-PROVIDER-PAYMENT/payment/feign/timeout。

OpenFeign默认等待1秒钟，超过后报错

YML文件里需要开启OpenFeign客户端超时控制

```yaml
feign:
  client:
    config:
      default:
        # 指的是建立连接后从服务器读取到可用资源所用的时间
        connect-timeout: 5000
        # 指的是建立连接所用的时间，适用于网络状况正常的情况下,两端连接所用的时间
        read-timeout: 5000
```

### OpenFeign配置

使用其他内置的负载均衡策略（类似上面ribbon的，因为底层就是ribbon）

新建一个feign配置类

```java
@Configuration
public class FeignConfig {

    @Bean
    public IRule loadBalancedRule() {
        return new RandomRule();
    }

}
```

这样就可以替换默认的策略

**日志打印功能**

Feign提供了日志打印功能，我们可以通过配置来调整日恙级别，从而了解Feign 中 Http请求的细节。

说白了就是对Feign接口的调用情况进行监控和输出

**日志级别**

- NONE：默认的，不显示任何日志;
- BASIC：仅记录请求方法、URL、响应状态码及执行时间;
- HEADERS：除了BASIC中定义的信息之外，还有请求和响应的头信息;
- FULL：除了HEADERS中定义的信息之外，还有请求和响应的正文及元数据。

**配置日志bean**

```java
@Configuration
public class FeignConfig {

    @Bean
    public IRule loadBalancedRule() {
        return new RandomRule();
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
```

**YML文件里需要开启日志的Feign客户端**

```yaml
logging:
  level:
    # feign日志以什么级别监控哪个接口
    org.elianacc.cloudreview.service.PaymentFeignService: debug
```

**后台日志查看**

得到更多日志信息。





---------------------------------------------------------------------------------------OpenFeign 结束---------------------------------------------------------------------------------------------------------



## Hystrix



分布式系统面临的问题

复杂分布式体系结构中的应用程序有数十个依赖关系，每个依赖关系在某些时候将不可避免地失败。

服务雪崩

多个微服务之间调用的时候，假设微服务A调用微服务B和微服务C，微服务B和微服务C又调用其它的微服务，这就是所谓的“扇出”。如果扇出的链路上某个微服务的调用响应时间过长或者不可用，对微服务A的调用就会占用越来越多的系统资源，进而引起系统崩溃，所谓的“雪崩效应”.
对于高流量的应用来说，单一的后避依赖可能会导致所有服务器上的所有资源都在几秒钟内饱和。比失败更糟糕的是，这些应用程序还可能导致服务之间的延迟增加，备份队列，线程和其他系统资源紧张，导致整个系统发生更多的级联故障。这些都表示需要对故障和延迟进行隔离和管理，以便单个依赖关系的失败，不能取消整个应用程序或系统。

所以，通常当你发现一个模块下的某个实例失败后，这时候这个模块依然还会接收流量，然后这个有问题的模块还调用了其他的模块，这样就会发生级联故障，或者叫雪崩。

Hystrix是什么

Hystrix是一个用于处理分布式系统的延迟和容错的开源库，在分布式系统里，许多依赖不可避免的会调用失败，比如超时、异常等，Hystrix能够保证在一个依赖出问题的情况下，不会导致整体服务失败，避免级联故障，以提高分布式系统的弹性。

"断路器”本身是一种开关装置，当某个服务单元发生故障之后，通过断路器的故障监控（类似熔断保险丝)，向调用方返回一个符合预期的、可处理的备选响应（FallBack)，而不是长时间的等待或者抛出调用方无法处理的异常，这样就保证了服务调用方的线程不会被长时间、不必要地占用，从而避免了故障在分布式系统中的蔓延，乃至雪崩。

**服务降级**

服务器忙，请稍后再试，不让客户端等待并立刻返回一个友好提示，fallback

哪些情况会出发降级

程序运行导常
超时
服务熔断触发服务降级
线程池/信号量打满也会导致服务降级
**服务熔断**

类比保险丝达到最大服务访问后，直接拒绝访问，拉闸限电，然后调用服务降级的方法并返回友好提示。

服务的降级 -> 进而熔断 -> 恢复调用链路

**服务限流**

秒杀高并发等操作，严禁一窝蜂的过来拥挤，大家排队，一秒钟N个，有序进行。

### Hystrix支付微服务构建

1.新建cloudreview-provider-hystrix-payment8003

2.POM

```xml
    <dependencies>

        <!-- SpringCloud hystrix 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
        </dependency>

        <!-- SpringCloud eureka client 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

        <!-- 项目本身api通用整合 依赖 -->
        <dependency>
            <groupId>org.elianacc</groupId>
            <artifactId>cloudreview-api-commons</artifactId>
            <version>${project.version}</version>
        </dependency>

        <!-- SpringBoot SpringMVC 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- SpringBoot 指标监控 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- SpringBoot lombok 依赖 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- SpringBoot Spring测试 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

    </dependencies>

```

3.YML

```yaml
server:
  port: 8003

spring:
  application:
    name: cloudreview-provider-hystrix-payment

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka
  instance:
    # 显示主机名称修改
    instance-id: payment8003
    # 访问信息IP信息提示
    prefer-ip-address: true
    # Eureka客户端向服务端发送心跳的时间间隔，单位为秒(默认是30秒)
    lease-renewal-interval-in-seconds: 3
    # Eureka服务端在收到最后一次心跳后等待时间上限，单位为秒(默认是90秒)，超时将剔除服务
    lease-expiration-duration-in-seconds: 5

```

4.主启动

```java
@SpringBootApplication
@EnableEurekaClient
public class PaymentHystrixMain8003 {
    public static void main(String[] args) {
        SpringApplication.run(PaymentHystrixMain8003.class, args);
    }

}
```

6.正常测试

启动eureka7001 7002

启动cloudreview-provider-hystrix-payment8003

访问

success的方法 - http://localhost:8003/payment/hystrix/ok/1
每次调用耗费5秒钟 - http://localhost:8003/payment/hystrix/timeout/1

上述module均OK

以上述为根基平台，从正确 -> 错误 -> 降级熔断 -> 恢复。



### JMeter高并发压测后卡顿

开启Jmeter，来20000个并发压死8003，20000个请求都去访问paymentInfo_TimeOut服务

1.测试计划中右键添加-》线程-》线程组（线程组202102，线程数：200，线程数：100，其他参数默认）

2.刚刚新建线程组202102，右键它-》添加-》取样器-》Http请求-》基本 输入http://localhost:8003/payment/hystrix/ok/1

3.点击绿色三角形图标启动。

看演示结果：拖慢，原因：tomcat的默认的工作线程数被打满了，没有多余的线程来分解压力和处理。

Jmeter压测结论

上面还是服务提供者8003自己测试，假如此时外部的消费者80也来访问，那消费者只能干等，最终导致消费端80不满意，服务端8003直接被拖慢。



### 订单微服务调用支付服务出现卡顿

1.新建cloudreview-consumer-feign-hystrix-order80

2.POM

```xml
    <dependencies>

        <!-- SpringCloud hystrix 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
        </dependency>

        <!-- SpringCloud openfeign 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>

        <!-- SpringCloud eureka client 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

        <!-- 项目本身api通用整合 依赖 -->
        <dependency>
            <groupId>org.elianacc</groupId>
            <artifactId>cloudreview-api-commons</artifactId>
            <version>${project.version}</version>
        </dependency>

        <!-- SpringBoot SpringMVC 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- SpringBoot 指标监控 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- SpringBoot lombok 依赖 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- SpringBoot Spring测试 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

    </dependencies>

```

3.YML

```yaml
server:
  port: 80

spring:
  application:
    name: cloudreview-consumer-feign-hystrix-order

eureka:
  client:
    # 表示是否将自己注册进Eurekaserver默认为true。
    register-with-eureka: true
    # 是否从EurekaServer抓取已有的注册信息，默认为true。单节点无所谓，集群必须设置为true才能配合ribbon使用负载均衡
    fetchRegistry: true
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka, http://eureka7002.com:7002/eureka
  instance:
    # 显示主机名称修改
    instance-id: order80
    # 访问信息IP信息提示
    prefer-ip-address: true
    # Eureka客户端向服务端发送心跳的时间间隔，单位为秒(默认是30秒)
    lease-renewal-interval-in-seconds: 3
    # Eureka服务端在收到最后一次心跳后等待时间上限，单位为秒(默认是90秒)，超时将剔除服务
    lease-expiration-duration-in-seconds: 5

feign:
  client:
    config:
      default:
        # 指的是建立连接后从服务器读取到可用资源所用的时间
        connect-timeout: 5000
        # 指的是建立连接所用的时间，适用于网络状况正常的情况下,两端连接所用的时间
        read-timeout: 5000
```

4.主启动

```java
@SpringBootApplication
@EnableFeignClients
public class OrderHystrixMain80 {
    public static void main(String[] args) {
        SpringApplication.run(OrderHystrixMain80.class, args);
    }
}
```

5.业务类

```java
@Component
@FeignClient(value = "CLOUDREVIEW-PROVIDER-HYSTRIX-PAYMENT")
public interface PaymentHystrixService {
    @GetMapping("/payment/hystrix/ok/{id}")
    public String paymentInfo_OK(@PathVariable("id") Integer id);

    @GetMapping("/payment/hystrix/timeout/{id}")
    public String paymentInfo_TimeOut(@PathVariable("id") Integer id);
}
```

```java
@RestController
@Slf4j
public class OrderHystirxController {

    @Autowired
    private PaymentHystrixService paymentHystrixService;

    @GetMapping("/consumer/payment/hystrix/ok/{id}")
    public String paymentInfo_OK(@PathVariable("id") Integer id) {
        return paymentHystrixService.paymentInfo_OK(id);
    }

    @GetMapping("/consumer/payment/hystrix/timeout/{id}")
    public String paymentInfo_TimeOut(@PathVariable("id") Integer id) {
        return paymentHystrixService.paymentInfo_TimeOut(id);
    }

}
```

6.正常测试

http://localhost/consumer/payment/hystrix/ok/1

7.高并发测试

2W个线程压8003

消费端80微服务再去访问正常的Ok微服务8003地址

http://localhost/consumer/payment/hystrix/ok/32

消费者80被拖慢

原因：8003同一层次的其它接口服务被困死，因为tomcat线程池里面的工作线程已经被挤占完毕。

正因为有上述故障或不佳表现才有我们的降级/容错/限流等技术诞生。

### 降级容错解决的维度要求

超时导致服务器变慢(转圈) - 超时不再等待

出错(宕机或程序运行出错) - 出错要有兜底

解决：

- 对方服务(8003)超时了，调用者(80)不能一直卡死等待，必须有服务降级。
- 对方服务(8003)down机了，调用者(80)不能一直卡死等待，必须有服务降级。
- 对方服务(8003)OK，调用者(80)自己出故障或有自我要求(自己的等待时间小于服务提供者)，自己处理降级。

### Hystrix之服务降级支付侧fallback（！！服务降级 fallback 既可以放在服务端，也可以放在客户端，但是我们一般放在客户端）

降级配置 - @HystrixCommand

8003先从自身找问题

设置自身调用超时时间的峰值，峰值内可以正常运行，超过了需要有兜底的方法处埋，作服务降级fallback。

8003fallback

业务类启用 - @HystrixCommand报异常后如何处理

—旦调用服务方法失败并抛出了错误信息后，会自动调用@HystrixCommand标注好的fallbackMethod调用类中的指定方法

```java
	@HystrixCommand(fallbackMethod = "paymentInfo_TimeOutHandler"/*指定善后方法名*/, commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"/*"5000"*/)
    })
    public String paymentInfo_TimeOut(Integer id) {
        int businessTime = 5000;   // 超时测试
        // 异常测试
        /*int businessTime = 3000;
        int age = 10/0;*/
        try {
            TimeUnit.MILLISECONDS.sleep(businessTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "线程池:  " + Thread.currentThread().getName() + " id:  " + id + "\t" + "O(∩_∩)O哈哈~" + "  耗时(秒): 3";
    }

    // 用来善后的方法
    public String paymentInfo_TimeOutHandler(Integer id) {
        return "线程池:  " + Thread.currentThread().getName() + "  8001系统繁忙或者运行报错，请稍后再试,id:  " + id + "\t" + "o(╥﹏╥)o";
    }
```



上面故意制造两种异常:

1. int age = 10/0，计算异常
2. 我们能接受3秒钟，它运行5秒钟，超时异常。

当前服务不可用了，做服务降级，兜底的方案都是paymentInfo_TimeOutHandler

**主启动类激活**

添加新注解@EnableCircuitBreaker  （这里可以直接用@EnableHystrix）

```java
@SpringBootApplication
@EnableEurekaClient
@EnableHystrix
public class PaymentHystrixMain8003 {
    public static void main(String[] args) {
        SpringApplication.run(PaymentHystrixMain8003.class, args);
    }

}
```

### Hystrix之服务降级订单侧fallback

YML

```yaml
feign:
  client:
    config:
      default:
        # 指的是建立连接后从服务器读取到可用资源所用的时间
        connect-timeout: 5000
        # 指的是建立连接所用的时间，适用于网络状况正常的情况下,两端连接所用的时间
        read-timeout: 5000
  # 开启feign对hystrix支持
  hystrix:
    enabled: true
```

主启动

```java
@SpringBootApplication
@EnableFeignClients
@EnableHystrix
public class OrderHystrixMain80 {
    public static void main(String[] args) {
        SpringApplication.run(OrderHystrixMain80.class, args);
    }
}
```

业务类

```java
	@HystrixCommand(fallbackMethod = "paymentTimeOutFallbackMethod", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000")
    })
    @GetMapping("/consumer/payment/hystrix/timeout/{id}")
    public String paymentInfo_TimeOut(@PathVariable("id") Integer id) {
        int age = 10/0;  // 异常测试
        return paymentHystrixService.paymentInfo_TimeOut(id);
    }

    // 善后方法
    public String paymentTimeOutFallbackMethod(Integer id) {
        return "我是消费者80,对方支付系统繁忙请10秒钟后再试或者自己运行出错请检查自己,o(╥﹏╥)o";
    }
```

### Hystrix之全局服务降级DefaultProperties

目前问题1 每个业务方法对应一个兜底的方法，代码膨胀

解决方法

1:1每个方法配置一个服务降级方法，技术上可以，但是不聪明

1:N除了个别重要核心业务有专属，其它普通的可以通过@DefaultProperties(defaultFallback = “”)统一跳转到统一处理结果页面

通用的和独享的各自分开，避免了代码膨胀，合理减少了代码量

```java
@RestController
@Slf4j
@DefaultProperties(defaultFallback = "payment_Global_FallbackMethod")  // 2 全局默认fallback
public class OrderHystirxController {

    @Autowired
    private PaymentHystrixService paymentHystrixService;

    @GetMapping("/consumer/payment/hystrix/ok/{id}")
    public String paymentInfo_OK(@PathVariable("id") Integer id) {
        return paymentHystrixService.paymentInfo_OK(id);
    }

// 1.单个对应fallback
//    @HystrixCommand(fallbackMethod = "paymentTimeOutFallbackMethod", commandProperties = {
//            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1500"/*5000*/)
//    })
    @HystrixCommand   // 2 全局默认fallback
    @GetMapping("/consumer/payment/hystrix/timeout/{id}")
    public String paymentInfo_TimeOut(@PathVariable("id") Integer id) {
        //int age = 10/0;  // 异常测试
        return paymentHystrixService.paymentInfo_TimeOut(id);
    }

    // 1.单个对应fallback  善后方法
//    public String paymentTimeOutFallbackMethod(Integer id) {
//        return "我是消费者80,对方支付系统繁忙请10秒钟后再试或者自己运行出错请检查自己,o(╥﹏╥)o";
//    }


    // 2 全局默认fallback 全局fallback方法
    public String payment_Global_FallbackMethod() {
        return "Global异常处理信息，请稍后再试，/(ㄒoㄒ)/~~";
    }

}
```

yml可以全局设置Hystrix的默认超时时间

```yaml
hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            # hystrix默认超时时间设置（超时后调用fallback）
            timeoutInMilliseconds: 5000
```

### Hystrix之通配服务降级FeignFallback

目前问题2 统一和自定义的分开，代码混乱

服务降级，客户端去调用服务端，碰上服务端宕机或关闭

本次案例服务降级处理是在客户端80实现完成的，与服务端8003没有关系，只需要为Feign客户端定义的接口添加一个服务降级处理的实现类即可实现解耦

未来我们要面对的异常

- 运行

- 超时
- 宕机

修改cloudreview-consumer-feign-hystrix-order80

根据cloudreview-consumer-feign-hystrix-order80已经有的PaymentHystrixService接口，
重新新建一个类(AaymentFallbackService)实现该接口，统一为接口里面的方法进行异常处理

PaymentFallbackService类实现PaymentHystrixService接口

```java
@Component
public class PaymentFallbackService implements PaymentHystrixService {
    @Override
    public String paymentInfo_OK(Integer id) {
        return "-----PaymentFallbackService fall back-paymentInfo_OK ,o(╥﹏╥)o";
    }

    @Override
    public String paymentInfo_TimeOut(Integer id) {
        return "-----PaymentFallbackService fall back-paymentInfo_TimeOut ,o(╥﹏╥)o";
    }
}
```

YML

```yaml
#开启
feign:
  hystrix:
    enabled: true
```

PaymentHystrixService接口

```java
@Component
public class PaymentFallbackService implements PaymentHystrixService {
    @Override
    public String paymentInfo_OK(Integer id) {
        return "-----PaymentFallbackService fall back-paymentInfo_OK ,o(╥﹏╥)o";
    }

    @Override
    public String paymentInfo_TimeOut(Integer id) {
        return "-----PaymentFallbackService fall back-paymentInfo_TimeOut ,o(╥﹏╥)o";
    }
}
```

**测试**

启动7001 7002

PaymentHystrixMain8003启动

正常访问测试 - http://localhost/consumer/payment/hystrix/ok/1

故意关闭微服务8003

客户端自己调用提示 - 此时服务端provider已经down了，但是我们做了服务降级处理，让客户端在服务端不可用时也会获得提示信息而不会挂起耗死服务器。

测试超时----yml设置超时时间

http://localhost/consumer/payment/hystrix/timeout/1



### Hystrix之服务熔断理论

断路器，相当于保险丝。

熔断机制概述

熔断机制是应对雪崩效应的一种微服务链路保护机制。当扇出链路的某个微服务出错不可用或者响应时间太长时，会进行服务的降级，进而熔断该节点微服务的调用，快速返回错误的响应信息。当检测到该节点微服务调用响应正常后，恢复调用链路。

在Spring Cloud框架里，熔断机制通过Hystrix实现。Hystrix会监控微服务间调用的状况，当失败的调用到一定阈值，缺省是5秒内20次调用失败，就会启动熔断机制。熔断机制的注解是@HystrixCommand。

![](https://img-blog.csdnimg.cn/img_convert/84d60234d01c4b7e9cae515066eb711b.png)

**断路器在什么情况下开始起作用**

```java
//=====服务熔断
@HystrixCommand(fallbackMethod = "paymentCircuitBreaker_fallback",commandProperties = {
    @HystrixProperty(name = "circuitBreaker.enabled",value = "true"),// 是否开启断路器
    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"),// 请求次数
    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"), // 时间窗口期
    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60"),// 失败率达到多少后跳闸
})
public String paymentCircuitBreaker(@PathVariable("id") Integer id) {
    ...
}
```

涉及到断路器的三个重要参数：

- 快照时间窗：断路器确定是否打开需要统计一些请求和错误数据，而统计的时间范围就是快照时间窗，默认为最近的10秒。
- 请求总数阀值：在快照时间窗内，必须满足请求总数阀值才有资格熔断。默认为20，意味着在10秒内，如果该hystrix命令的调用次数不足20次,即使所有的请求都超时或其他原因失败，断路器都不会打开。
- 错误百分比阀值：当请求总数在快照时间窗内超过了阀值，比如发生了30次调用，如果在这30次调用中，有15次发生了超时异常，也就是超过50%的错误百分比，在默认设定50%阀值情况下，这时候就会将断路器打开。

**断路器开启或者关闭的条件**

- 到达以下阀值，断路器将会开启：

  - 当满足一定的阀值的时候（默认10秒内超过20个请求次数)
  - 当失败率达到一定的时候（默认10秒内超过50%的请求失败)
- 当开启的时候，所有请求都不会进行转发
- 一段时间之后（默认是5秒)，这个时候断路器是半开状态，会让其中一个请求进行转发。如果成功，断路器会关闭，若失败，继续开启。


**断路器打开之后**

1：再有请求调用的时候，将不会调用主逻辑，而是直接调用降级fallback。通过断路器，实现了自动地发现错误并将降级逻辑切换为主逻辑，减少响应延迟的效果。

2：原来的主逻辑要如何恢复呢？

对于这一问题，hystrix也为我们实现了自动恢复功能。

当断路器打开，对主逻辑进行熔断之后，hystrix会启动一个休眠时间窗，在这个时间窗内，降级逻辑是临时的成为主逻辑，当休眠时间窗到期，断路器将进入半开状态，释放一次请求到原来的主逻辑上，如果此次请求正常返回，那么断路器将继续闭合，主逻辑恢复，如果这次请求依然有问题，断路器继续进入打开状态，休眠时间窗重新计时。

**All配置**

```java
@HystrixCommand(fallbackMethod = "fallbackMethod", 
                groupKey = "strGroupCommand", 
                commandKey = "strCommand", 
                threadPoolKey = "strThreadPool",
                
                commandProperties = {
                    // 设置隔离策略，THREAD 表示线程池 SEMAPHORE：信号池隔离
                    @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
                    // 当隔离策略选择信号池隔离的时候，用来设置信号池的大小（最大并发数）
                    @HystrixProperty(name = "execution.isolation.semaphore.maxConcurrentRequests", value = "10"),
                    // 配置命令执行的超时时间
                    @HystrixProperty(name = "execution.isolation.thread.timeoutinMilliseconds", value = "10"),
                    // 是否启用超时时间
                    @HystrixProperty(name = "execution.timeout.enabled", value = "true"),
                    // 执行超时的时候是否中断
                    @HystrixProperty(name = "execution.isolation.thread.interruptOnTimeout", value = "true"),
                    
                    // 执行被取消的时候是否中断
                    @HystrixProperty(name = "execution.isolation.thread.interruptOnCancel", value = "true"),
                    // 允许回调方法执行的最大并发数
                    @HystrixProperty(name = "fallback.isolation.semaphore.maxConcurrentRequests", value = "10"),
                    // 服务降级是否启用，是否执行回调函数
                    @HystrixProperty(name = "fallback.enabled", value = "true"),
                    // 是否启用断路器
                    @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),
                    // 该属性用来设置在滚动时间窗中，断路器熔断的最小请求数。例如，默认该值为 20 的时候，如果滚动时间窗（默认10秒）内仅收到了19个请求， 即使这19个请求都失败了，断路器也不会打开。
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "20"),
                    
                    // 该属性用来设置在滚动时间窗中，表示在滚动时间窗中，在请求数量超过 circuitBreaker.requestVolumeThreshold 的情况下，如果错误请求数的百分比超过50, 就把断路器设置为 "打开" 状态，否则就设置为 "关闭" 状态。
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
                    // 该属性用来设置当断路器打开之后的休眠时间窗。 休眠时间窗结束之后，会将断路器置为 "半开" 状态，尝试熔断的请求命令，如果依然失败就将断路器继续设置为 "打开" 状态，如果成功就设置为 "关闭" 状态。
                    @HystrixProperty(name = "circuitBreaker.sleepWindowinMilliseconds", value = "5000"),
                    // 断路器强制打开
                    @HystrixProperty(name = "circuitBreaker.forceOpen", value = "false"),
                    // 断路器强制关闭
                    @HystrixProperty(name = "circuitBreaker.forceClosed", value = "false"),
                    // 滚动时间窗设置，该时间用于断路器判断健康度时需要收集信息的持续时间
                    @HystrixProperty(name = "metrics.rollingStats.timeinMilliseconds", value = "10000"),
                    
                    // 该属性用来设置滚动时间窗统计指标信息时划分"桶"的数量，断路器在收集指标信息的时候会根据设置的时间窗长度拆分成多个 "桶" 来累计各度量值，每个"桶"记录了一段时间内的采集指标。
                    // 比如 10 秒内拆分成 10 个"桶"收集这样，所以 timeinMilliseconds 必须能被 numBuckets 整除。否则会抛异常
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "10"),
                    // 该属性用来设置对命令执行的延迟是否使用百分位数来跟踪和计算。如果设置为 false, 那么所有的概要统计都将返回 -1。
                    @HystrixProperty(name = "metrics.rollingPercentile.enabled", value = "false"),
                    // 该属性用来设置百分位统计的滚动窗口的持续时间，单位为毫秒。
                    @HystrixProperty(name = "metrics.rollingPercentile.timeInMilliseconds", value = "60000"),
                    // 该属性用来设置百分位统计滚动窗口中使用 “ 桶 ”的数量。
                    @HystrixProperty(name = "metrics.rollingPercentile.numBuckets", value = "60000"),
                    // 该属性用来设置在执行过程中每个 “桶” 中保留的最大执行次数。如果在滚动时间窗内发生超过该设定值的执行次数，
                    // 就从最初的位置开始重写。例如，将该值设置为100, 滚动窗口为10秒，若在10秒内一个 “桶 ”中发生了500次执行，
                    // 那么该 “桶” 中只保留 最后的100次执行的统计。另外，增加该值的大小将会增加内存量的消耗，并增加排序百分位数所需的计算时间。
                    @HystrixProperty(name = "metrics.rollingPercentile.bucketSize", value = "100"),
                    
                    // 该属性用来设置采集影响断路器状态的健康快照（请求的成功、 错误百分比）的间隔等待时间。
                    @HystrixProperty(name = "metrics.healthSnapshot.intervalinMilliseconds", value = "500"),
                    // 是否开启请求缓存
                    @HystrixProperty(name = "requestCache.enabled", value = "true"),
                    // HystrixCommand的执行和事件是否打印日志到 HystrixRequestLog 中
                    @HystrixProperty(name = "requestLog.enabled", value = "true"),

                },
                threadPoolProperties = {
                    // 该参数用来设置执行命令线程池的核心线程数，该值也就是命令执行的最大并发量
                    @HystrixProperty(name = "coreSize", value = "10"),
                    // 该参数用来设置线程池的最大队列大小。当设置为 -1 时，线程池将使用 SynchronousQueue 实现的队列，否则将使用 LinkedBlockingQueue 实现的队列。
                    @HystrixProperty(name = "maxQueueSize", value = "-1"),
                    // 该参数用来为队列设置拒绝阈值。 通过该参数， 即使队列没有达到最大值也能拒绝请求。
                    // 该参数主要是对 LinkedBlockingQueue 队列的补充,因为 LinkedBlockingQueue 队列不能动态修改它的对象大小，而通过该属性就可以调整拒绝请求的队列大小了。
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "5"),
                }
               )
public String doSomething() {
	...
}

```



### 服务熔断例子

新增cloudreview-provider-hystrix-payment8003 中的代码

service

```java
	@HystrixCommand(commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),// 是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),// 请求次数 默认20
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"), // 时间窗口期 默认5000
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60"),// 失败率达到多少后跳闸 默认50
    })
    public String paymentCircuitBreaker(@PathVariable("id") Integer id) {
        if (id < 0) {
            throw new RuntimeException("******id 不能负数");
        }
        String serialNumber = IdUtil.simpleUUID();

        return Thread.currentThread().getName() + "\t" + "调用成功，流水号: " + serialNumber;
    }
```

controller

```java
    // ====服务熔断
    @GetMapping("/payment/circuit/{id}")
    public String paymentCircuitBreaker(@PathVariable("id") Integer id) {
        String result = paymentService.paymentCircuitBreaker(id);
        log.info("****result: " + result);
        return result + "端口-" + serverPort;
    }
```

新增cloudreview-consumer-feign-hystrix-order80 中的代码

service

```java
	@GetMapping("/payment/circuit/{id}")
    public String paymentCircuitBreaker(@PathVariable("id") Integer id);
```

service impl  （降级fallback）

```java
    @Override
    public String paymentCircuitBreaker(Integer id) {
        return "id 不能负数，请稍后再试，/(ㄒoㄒ)/~~   id: " +id;
    }
```

controller

```java
	@GetMapping("/consumer/payment/circuit/{id}")
    public String paymentCircuitBreaker(@PathVariable("id") Integer id) {
        return paymentHystrixService.paymentCircuitBreaker(id);
    }
```

测试

正确 - http://localhost/consumer/payment/circuit/8

错误 - http://localhost/consumer/payment/circuit/-1

多次错误，再来次正确，但错误得显示

**！！！重点测试 - 多次错误，然后慢慢正确，发现刚开始不满足条件，就算是正确的访问地址也不能进行**



### Hystrix图形化Dashboard搭建

除了隔离依赖服务的调用以外，Hystrix还提供了准实时的调用监控(Hystrix Dashboard)，Hystrix会持续地记录所有通过Hystrix发起的请求的执行信息，并以统计报表和图形的形式展示给用户，包括每秒执行多少请求多少成功，多少失败等。

Netflix通过hystrix-metrics-event-stream项目实现了对以上指标的监控。Spring Cloud也提供了Hystrix Dashboard的整合，对监控内容转化成可视化界面。

仪表盘6001

1新建cloudreview-consumer-hystrix-dashboard6001

2.POM

```xml
    <dependencies>

        <!-- SpringCloud hystrix-dashboard -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
        </dependency>

        <!-- SpringBoot 指标监控 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- SpringBoot lombok 依赖 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- SpringBoot Spring测试 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

    </dependencies>
```

3.YML

```yaml
server:
  port: 6001
```

4.HystrixDashboardMain6001+新注解@EnableHystrixDashboard

```java
@SpringBootApplication
@EnableHystrixDashboard
public class HystrixDashboardMain6001 {
    public static void main(String[] args) {
        SpringApplication.run(HystrixDashboardMain6001.class, args);
    }
}
```

5.所有Provider微服务提供类(8001/8002/8003)都需要监控依赖配置

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

```

6.启动cloudreview-consumer-hystrix-dashboard6001该微服务后续将监控微服务8003

浏览器输入http://localhost:6001/hystrix



### Hystrix图形化Dashboard监控实战

**修改cloudreview-provider-hystrix-payment8003**

注意：新版本Hystrix需要在主启动类PaymentHystrixMain8003中指定监控路径

```java
	/**
     * 此配置是为了服务监控而配置，与服务容错本身无关，springcloud升级后的坑
     * ServletRegistrationBean因为springboot的默认路径不是"/hystrix.stream"，
     * 只要在自己的项目里配置上下面的servlet就可以了
     * 否则，Unable to connect to Command Metric Stream 404
     */
    @Bean
    public ServletRegistrationBean getServlet() {
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/hystrix.stream");
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }
```

监控测试

启动2个eureka

启动8003，6001

启动80

观察监控窗口

6001监控8003 - 填写监控地址 - http://localhost:8003/hystrix.stream 到 http://localhost:6001/hystrix页面的输入框。

测试地址

http://localhost/consumer/payment/circuit/8

http://localhost/consumer/payment/circuit/-1

测试通过

先访问正确地址，再访问错误地址，再正确地址，会发现图示断路器都是慢慢放开的。





-----------------------------------------------------------------------------------------Hystrix 结束------------------------------------------------------------------------------------------------------------





## GateWay



Gateway是在Spring生态系统之上构建的API网关服务，基于Spring 5，Spring Boot 2和Project Reactor等技术。

Gateway旨在提供一种简单而有效的方式来对API进行路由，以及提供一些强大的过滤器功能，例如:熔断、限流、重试等。

SpringCloud Gateway是Spring Cloud的一个全新项目，基于Spring 5.0+Spring Boot 2.0和Project Reactor等技术开发的网关，它旨在为微服务架构提供—种简单有效的统一的API路由管理方式。

SpringCloud Gateway作为Spring Cloud 生态系统中的网关，目标是替代Zuul，在Spring Cloud 2.0以上版本中，没有对新版本的Zul 2.0以上最新高性能版本进行集成，仍然还是使用的Zuul 1.x非Reactor模式的老版本。而为了提升网关的性能，SpringCloud Gateway是基于WebFlux框架实现的，而WebFlux框架底层则使用了高性能的Reactor模式通信框架Netty。

Spring Cloud Gateway的目标提供统一的路由方式且基于 Filter链的方式提供了网关基本的功能，例如:安全，监控/指标，和限流。

**作用**

- 反向代理
- 鉴权
- 流量控制
- 熔断
- 日志监控
- …

**微服务架构中网关的位置**

![](https://img-blog.csdnimg.cn/img_convert/5877d4b9035ead9cd2d037609dceb442.png)

三大核心概念

1. Route(路由) - 路由是构建网关的基本模块,它由ID,目标URI,一系列的断言和过滤器组成,如断言为true则匹配该路由；
2. Predicate(断言) - 参考的是Java8的java.util.function.Predicate，开发人员可以匹配HTTP请求中的所有内容(例如请求头或请求参数),如果请求与断言相匹配则进行路由；
3. Filter(过滤) - 指的是Spring框架中GatewayFilter的实例,使用过滤器,可以在请求被路由前或者之后对请求进行修改。



![](https://img-blog.csdnimg.cn/img_convert/70da1eecc951a338588356ee2db3fa1f.png)

web请求，通过一些匹配条件，定位到真正的服务节点。并在这个转发过程的前后，进行一些精细化控制。

predicate就是我们的匹配条件；而fliter，就可以理解为一个无所不能的拦截器。有了这两个元素，再加上目标uri，就可以实现一个具体的路由了

![](https://img-blog.csdnimg.cn/img_convert/62be54501c6e2b95620b79cc918a2e9a.png)

客户端向Spring Cloud Gateway发出请求。然后在Gateway Handler Mapping 中找到与请求相匹配的路由，将其发送到GatewayWeb Handler。

Handler再通过指定的过滤器链来将请求发送到我们实际的服务执行业务逻辑，然后返回。

过滤器之间用虚线分开是因为过滤器可能会在发送代理请求之前(“pre”)或之后(“post"）执行业务逻辑。

Filter在“pre”类型的过滤器可以做参数校验、权限校验、流量监控、日志输出、协议转换等，在“post”类型的过滤器中可以做响应内容、响应头的修改，日志的输出，流量监控等有着非常重要的作用。

**核心逻辑：**路由转发 + 执行过滤器链。



### Gateway9527搭建

1.新建Module - cloudreview-provider-payment-gateway9527

2.POM

```xml
    <dependencies>

        <!-- SpringCloud gateway 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>

        <!-- SpringCloud eureka client 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

        <!-- 项目本身api通用整合 依赖 -->
        <dependency>
            <groupId>org.elianacc</groupId>
            <artifactId>cloudreview-api-commons</artifactId>
            <version>${project.version}</version>
        </dependency>

        <!-- SpringBoot lombok 依赖 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- SpringBoot Spring测试 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

    </dependencies>
```

3.YML

```yaml
server:
  port: 9527

spring:
  application:
    name: cloudreview-provider-payment-gateway

eureka:
  client: #服务提供者provider注册进eureka服务列表内
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka
  instance:
    # 显示主机名称修改
    instance-id: geteway9527
    # 访问信息IP信息提示
    prefer-ip-address: true
    # Eureka客户端向服务端发送心跳的时间间隔，单位为秒(默认是30秒)
    lease-renewal-interval-in-seconds: 3
    # Eureka服务端在收到最后一次心跳后等待时间上限，单位为秒(默认是90秒)，超时将剔除服务
    lease-expiration-duration-in-seconds: 5
```

4.业务类

无

5.主启动类

```java
@SpringBootApplication
@EnableEurekaClient
public class GateWayMain9527 {
    public static void main(String[] args) {
        SpringApplication.run(GateWayMain9527.class, args);
    }
}
```

6.9527网关如何做路由映射?

我们目前不想暴露8001端口，希望在8001外面套一层9527

7.YML新增网关配置

```yaml
server:
  port: 9527

spring:
  application:
    name: cloudreview-provider-payment-gateway
  cloud:
    gateway:
      # 网关路由设置
      routes:
        - id: provider-payment
          #uri: http://localhost:8001   #单机使用
          uri: lb://cloudreview-provider-payment
          predicates:
            - Path=/**
            # 这个时间后才能起效
            - After=2021-11-02T09:25:01.889+08:00[Asia/Shanghai]

eureka:
  client: #服务提供者provider注册进eureka服务列表内
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka
  instance:
    # 显示主机名称修改
    instance-id: geteway9527
    # 访问信息IP信息提示
    prefer-ip-address: true
    # Eureka客户端向服务端发送心跳的时间间隔，单位为秒(默认是30秒)
    lease-renewal-interval-in-seconds: 3
    # Eureka服务端在收到最后一次心跳后等待时间上限，单位为秒(默认是90秒)，超时将剔除服务
    lease-expiration-duration-in-seconds: 5
```

8.测试

启动7001 7002

启动8001 8002

启动9527网关

访问说明

浏览器输入 - http://localhost:9527/payment/get/19

结果

不停刷新页面，8001/8002两个端口切换。

默认情况下Gateway会根据注册中心注册的服务列表，以注册中心上微服务名为路径创建**动态路由进行转发，从而实现动态路由的功能**（不写死一个地址）。

需要注意的是uri的协议为lb，表示启用Gateway的负载均衡功能。

lb://serviceName是spring cloud gateway在微服务中自动为我们创建的负载均衡uri。



### GateWay常用的Predicate

Spring Cloud Gateway将路由匹配作为Spring WebFlux HandlerMapping基础架构的一部分。

Spring Cloud Gateway包括许多内置的Route Predicate工厂。所有这些Predicate都与HTTP请求的不同属性匹配。多个RoutePredicate工厂可以进行组合。

Spring Cloud Gateway创建Route 对象时，使用RoutePredicateFactory 创建 Predicate对象，Predicate 对象可以赋值给Route。Spring Cloud Gateway包含许多内置的Route Predicate Factories。
所有这些谓词都匹配HTTP请求的不同属性。多种谓词工厂可以组合，并通过逻辑and。

常用的Route Predicate Factory

The After Route Predicate Factory
The Before Route Predicate Factory
The Between Route Predicate Factory
The Cookie Route Predicate Factory
The Header Route Predicate Factory
The Host Route Predicate Factory
The Method Route Predicate Factory
The Path Route Predicate Factory
The Query Route Predicate Factory
The RemoteAddr Route Predicate Factory
The weight Route Predicate Factory

**讨论几个Route Predicate Factory**

**The After Route Predicate Factory**

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: after_route
        uri: https://example.org
        predicates:
        # 这个时间后才能起效
        - After=2017-01-20T17:42:47.789-07:00[America/Denver]

```

可以通过下述方法获得上述格式的时间戳字符串

```java
import java.time.ZonedDateTime;


public class T2
{
    public static void main(String[] args)
    {
        ZonedDateTime zbj = ZonedDateTime.now(); // 默认时区
        System.out.println(zbj);

       //2021-02-22T15:51:37.485+08:00[Asia/Shanghai]
    }
}
```



### GateWay的Filter

路由过滤器可用于修改进入的HTTP请求和返回的HTTP响应，路由过滤器只能指定路由进行使用。Spring Cloud Gateway内置了多种路由过滤器，他们都由GatewayFilter的工厂类来产生。

Spring Cloud Gateway的Filter:

生命周期：

pre
post
种类（具体看官方文档）：

GatewayFilter - 有31种
GlobalFilter - 有10种
常用的GatewayFilter：AddRequestParameter GatewayFilter

自定义全局GlobalFilter：

两个主要接口介绍：

GlobalFilter
Ordered
能干什么：

全局日志记录
统一网关鉴权
…

代码案例：

GateWay9527项目添加MyLogGateWayFilter类：



```java
@Component
@Slf4j
public class LogGateWayFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("***********come in MyLogGateWayFilter:  " + new Date());

        String uname = exchange.getRequest().getQueryParams().getFirst("uname");

        if (uname == null) {
            log.info("*******用户名为null，非法用户，o(╥﹏╥)o");
            exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
```

访问  http://localhost:9527/payment/get/19   报错

http://localhost:9527/payment/get/19?uname=123      正确





--------------------------------------------------------------------------------------- GateWay 结束----------------------------------------------------------------------------------------------------------





## Nacos



为什么叫Nacos

前四个字母分别为Naming和Configuration的前两个字母，最后的s为Service。
是什么

一个更易于构建云原生应用的动态服务发现、配置管理和服务管理平台。
Nacos: Dynamic Naming and Configuration Service
Nacos就是注册中心＋配置中心的组合 -> Nacos = Eureka+Config+Bus
能干嘛

替代Eureka做服务注册中心
替代Config做服务配置中心
去哪下

https://github.com/alibaba/nacos/releases



**各中注册中心比较**

| 服务注册与发现框架 | CAP模型 | 控制台管理 | 社区活跃度      |
| ------------------ | ------- | ---------- | --------------- |
| Eureka             | AP      | 支持       | 低(2.x版本闭源) |
| Zookeeper          | CP      | 不支持     | 中              |
| consul             | CP      | 支持       | 高              |
| Nacos              | AP      | 支持       | 高              |

据说Nacos在阿里巴巴内部有超过10万的实例运行，已经过了类似双十一等各种大型流量的考验。



### Nacos安装



- 解压安装包，直接运行bin目录下的startup.cmd（或者以单机模式启动--进入bin目录，在当前目录打开cmd，运行startup.cmd -m standalone命令，以单机模式启动nacos服务）

- 命令运行成功后直接访问http://localhost:8848/nacos，默认账号密码都是nacos



### Nacos之服务提供者注册

新建Module - cloudreview-alibaba-provider-payment9001

同样创建一个   cloudreview-alibaba-provider-payment9002

代码详情见  --------  cloudreview-alibaba  ->>  cloudreview-alibaba-provider-payment9001  和   cloudreview-alibaba-provider-payment9002

测试

- http://localhost:9001/payment/get/18
- nacos控制台
- nacos服务注册中心+服务提供者9001  9002都OK了



### Nacos之服务消费者注册和负载



这里和open feign结合  gateway使用

为什么nacos支持负载均衡？因为spring-cloud-starter-alibaba-nacos-discovery内含netflix-ribbon包。

代码详情见  --------  cloudreview-alibaba  ->> cloudreview-alibaba-consumer-feign-order80

​								cloudreview-alibaba-provider-payment-gateway    ->>    cloudreview-alibaba-provider-payment-gateway9627  和 

​								cloudreview-alibaba-provider-payment-gateway9628

测试

- 启动nacos控制台
- http://localhost/consumer/payment/get/19   
  - 80访问9001/9002，配置的随机负载OK

使用post工具 http://localhost/consumer/payment/create   也ok

### **Nacos和CAP**

Nacos与其他注册中心特性对比

![](https://img-blog.csdnimg.cn/img_convert/62d5a8566a2dc588a5ed52346049a054.png)

Nacos支持AP和CP模式的切换

C是所有节点在同一时间看到的数据是一致的;而A的定义是所有的请求都会收到响应。

何时选择使用何种模式?

—般来说，如果不需要存储服务级别的信息且服务实例是通过nacos-client注册，并能够保持心跳上报，那么就可以选择AP模式。当前主流的服务如Spring cloud和Dubbo服务，都适用于AP模式，AP模式为了服务的可能性而减弱了一致性，因此AP模式下只支持注册临时实例。

如果需要在服务级别编辑或者存储配置信息，那么CP是必须，K8S服务和DNS服务则适用于CP模式。CP模式下则支持注册持久化实例，此时则是以Raft协议为集群运行模式，该模式下注册实例之前必须先注册服务，如果服务不存在，则会返回错误。

切换命令：

curl -X PUT '$NACOS_SERVER:8848/nacos/v1/ns/operator/switches?entry=serverMode&value=CP



### Nacos之服务配置中心



代码详情见 --------  cloudreview-alibaba  ->>  cloudreview-alibaba-config-nacos-client3377



**在Nacos中添加配置信息**

Nacos中的dataid的组成格式及与SpringBoot配置文件中的匹配规则

[官方文档](https://nacos.io/zh-cn/docs/quick-start-spring-cloud.html)

说明：之所以需要配置spring.application.name，是因为它是构成Nacos配置管理dataId 字段的一部分。

在 Nacos Spring Cloud中,dataId的完整格式如下：

```
${prefix}-${spring-profile.active}.${file-extension}
```

prefix默认为spring.application.name的值，也可以通过配置项spring.cloud.nacos.config.prefix来配置。
spring.profile.active即为当前环境对应的 profile，详情可以参考 Spring Boot文档。注意：当spring.profile.active为空时，对应的连接符 - 也将不存在，datald 的拼接格式变成${prefix}.${file-extension}
file-exetension为配置内容的数据格式，可以通过配置项spring .cloud.nacos.config.file-extension来配置。目前只支持properties和yaml类型。
通过Spring Cloud 原生注解@RefreshScope实现配置自动更新。
最后公式：

```
${spring.application.name)}-${spring.profiles.active}.${spring.cloud.nacos.config.file-extension}
```

配置新增

![](https://img-blog.csdnimg.cn/img_convert/05d45948bf637614dbd70e2bc8ce992d.png)

Nacos界面配置对应 - 设置DataId

![](https://img-blog.csdnimg.cn/img_convert/c61619bbe5ea16f34efca8103b0f90ba.png)

配置小结

![](https://img-blog.csdnimg.cn/img_convert/b3bffc4a646b30f9bf64fc649bf26f7d.png)

测试

启动前需要在nacos客户端-配置管理-配置管理栏目下有对应的yaml配置文件
运行cloud-config-nacos-client3377的主启动类
调用接口查看配置信息 - http://localhost:3377/config/info



### Nacos之命名空间分组和DataID三者关系



问题 - 多环境多项目管理

问题1:

实际开发中，通常一个系统会准备

dev开发环境
test测试环境
prod生产环境。
如何保证指定环境启动时服务能正确读取到Nacos上相应环境的配置文件呢?

问题2:

一个大型分布式微服务系统会有很多微服务子项目，每个微服务项目又都会有相应的开发环境、测试环境、预发环境、正式环境…那怎么对这些微服务配置进行管理呢?

Nacos的图形化管理界面

![](https://img-blog.csdnimg.cn/img_convert/3a7d1ad9bea8356742997ed3ebbe9be3.png)



![](https://img-blog.csdnimg.cn/img_convert/fe336f99f44c4b0aefddf0ae38d1c470.png)

**Namespace+Group+Data lD三者关系？为什么这么设计？**

1是什么

类似Java里面的package名和类名最外层的namespace是可以用于区分部署环境的，Group和DatalD逻辑上区分两个目标对象。

2三者情况

![](https://img-blog.csdnimg.cn/img_convert/60712abd615dd86ac6c119bf132a28d6.png)

默认情况：Namespace=public，Group=DEFAULT_GROUP，默认Cluster是DEFAULT

Nacos默认的Namespace是public，Namespace主要用来实现隔离。
比方说我们现在有三个环境：开发、测试、生产环境，我们就可以创建三个Namespace，不同的Namespace之间是隔离的。
Group默认是DEFAULT_GROUP，Group可以把不同的微服务划分到同一个分组里面去
Service就是微服务:一个Service可以包含多个Cluster (集群)，Nacos默认Cluster是DEFAULT，Cluster是对指定微服务的一个虚拟划分。
比方说为了容灾，将Service微服务分别部署在了杭州机房和广州机房，这时就可以给杭州机房的Service微服务起一个集群名称(HZ) ，给广州机房的Service微服务起一个集群名称(GZ)，还可以尽量让同一个机房的微服务互相调用，以提升性能。
最后是Instance，就是微服务的实例。



### Nacos之DataID配置



指定spring.profile.active和配置文件的DatalD来使不同环境下读取不同的配置

默认空间+默认分组+新建dev和test两个DatalD

- 新建dev配置DatalD

![img](https://img-blog.csdnimg.cn/img_convert/5ea4b3fd5ca8cb6e7de6f0d9ac98f051.png)

- 新建test配置DatalD

![img](https://img-blog.csdnimg.cn/img_convert/b41fe36b41fa2d5abc6e5e492ee3625d.png)

通过spring.profile.active属性就能进行多环境下配置文件的读取

![img](https://img-blog.csdnimg.cn/img_convert/281a70d387cb48ce82e94421adf17747.png)

**测试**

- http://localhost:3377/config/info
- 配置是什么就加载什么 test/dev



### Nacos之Group分组方案

通过Group实现环境区分 - 新建Group

![](https://img-blog.csdnimg.cn/img_convert/bdf592aa566fe50f7f454118a70ca03c.png)

在nacos图形界面控制台上面新建配置文件DatalD

![](https://img-blog.csdnimg.cn/img_convert/28aee2b45901bbb9a6776d5c4398a6bb.png)

bootstrap+application

在config下增加一条group的配置即可。可配置为DEV_GROUP或TEST GROUP

![](https://img-blog.csdnimg.cn/img_convert/342a167a8bd948d8ba5cbfd760cf66a6.png)

### Nacos之Namespace空间方案

新建dev/test的Namespace

![](https://img-blog.csdnimg.cn/img_convert/a10c71978c75c214aca5fa7057bb2834.png)

回到服务管理-服务列表查看

![](https://img-blog.csdnimg.cn/img_convert/2a9f3fa415f5cead0219d404a47131a0.png)

按照域名配置填写

![](https://img-blog.csdnimg.cn/img_convert/2177c126090c0db553a8ce77e838a7c9.png)





### Nacos集群



**官网架构图**

翻译，真实情况

![](https://img-blog.csdnimg.cn/img_convert/681c3dc16a69f197896cbff482f2298e.png)

按照上述，**我们需要mysql数据库**

默认Nacos使用嵌入式数据库实现数据的存储。所以，如果启动多个默认配置下的Nacos节点，数据存储是存在一致性问题的。为了解决这个问题，Nacos采用了集中式存储的方式来支持集群化部署，目前只支持MySQL的存储。

Nacos支持三种部署模式

- 单机模式-用于测试和单机试用。
- 集群模式-用于生产环境，确保高可用。
- 多集群模式-用于多数据中心场景。

Nacos默认自带的是嵌入式数据库derby，[nacos的pom.xml](https://blog.csdn.net/u011863024/article/details/github.com/alibaba/nacos/blob/develop/config/pom.xml)中可以看出。

derby到mysql切换配置步骤：

### 导入nacos-mysql.sql

将下载的nacos压缩包解压，在conf目录下找到nacos-mysql.sql文件。新建mysql数据库，并将其导入。

![](https://img-blog.csdnimg.cn/20201222143251129.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3Vua25vd25h,size_16,color_FFFFFF,t_70)

### 修改nacos配置

在conf目录下复制application.properties.example文件，重命名为：application.properties，修改使用mysql数据源

```
spring.datasource.platform=mysql

db.num=1
db.url.0=jdbc:mysql://127.0.0.1:3306/nacos_config?serverTimezone=GMT%2B8&useSSL=false&useUnicode=true&characterEncoding=utf8&autoReconnect=true
db.user=root
db.password=123456
```

**启动Nacos，可以看到是个全新的空记录界面，以前是记录进derby。**



### 搭建nacos集群

由于nacos集群会涉及投票选举机制，又叫过半提交，少数服从多数。所以nacos集群节点最少需要三个。

将nacos目录文件复制多份

**注意，接下来的操作三个nacos目录中都要执行**

#### **修改applicatioin.properties配置文件中的nacos端口号**

![](https://img-blog.csdnimg.cn/20201230111128595.png)

复制cluster.conf.example文件并重命名为：cluster.conf。新增三个nacos服务的：IP:Port
**注意:win10不要配置localhost:port，不然nacos会在cluster.conf中新增本机IP:port的配置，最终影响集群选举。一定要配置IP:port。三个cluster.conf中的配置一致。**

![](https://img-blog.csdnimg.cn/20201230112115480.png)

#### 在bin目录下打开cmd，运行：**startup.cmd** 命令分别启动

启动成功

![](https://img-blog.csdnimg.cn/20201230112459140.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3Vua25vd25h,size_16,color_FFFFFF,t_70)

#### 验证集群是否搭建成功

随便登录一个nacos控制台，在集群管理==》节点列表 处我们可以看到我们的三个集群节点，并且可以看到选举得出的leader节点和Follower节点，集群搭建成功。

![](https://img-blog.csdnimg.cn/20201230112905166.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3Vua25vd25h,size_16,color_FFFFFF,t_70)



现在已经有三个nacos，使用nginx做负载均衡--分发给3个nacos

配置nginx   8999

```
#user  nobody;
worker_processes  1;

#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

#pid        logs/nginx.pid;


events {
    worker_connections  1024;
}


http {
    include       mime.types;
    default_type  application/octet-stream;

    sendfile        on;

    keepalive_timeout  65;

    upstream cluster {
      server 127.0.0.1:8848 weight=1;
      server 127.0.0.1:8846 weight=1;
      server 127.0.0.1:8850 weight=1;
    }

    server {
      listen       8999;
      server_name  127.0.0.1;
      location / {
        #root  html;
        #index  index.html index.htm;
        proxy_pass http://cluster;
      }
      error_page   500 502 503 504  /50x.html;
      location = /50x.html {
        root   D:/nginx-1.20.1-nacos/html;
      }
    }

}
```

**截止到此处，1个Nginx+3个nacos注册中心+1个mysql**

访问    http://localhost:8999/nacos

- 让微服务cloudreview-alibaba-provider-payment9001启动注册进nacos集群 - 修改配置文件

```yaml
spring:
  application:
    name: cloudreview-alibaba-provider-payment
  cloud:
    nacos:
      discovery:
        #server-addr: localhost:8848
        # 使用nginx反向代理8999端口，做集群
        server-addr: 192.168.1.40:8999
      config:
        # Nacos作为配置中心地址
        server-addr: localhost:8848
        # 指定yml格式的配置
        file-extension: yml
        # 指定分组
        group: PAYMENT_GROUP
        # 指定命名空间
        namespace: eb582f4a-7412-4cf3-8897-a0c4a9d3f75e
```

注意有坑！！！！！！

SpringCloud (Hoxton.SR12)+Springboot(2.3.12.RELEASE)+Spring Cloud Alibaba(2.2.8.RELEASE)
但是.会出现NacosException: Client not connected,current status:STARTING的异常，所以必须将Spring Cloud Alibaba的版本换成2.2.1.RELEASE或者2.1.2.RELEASE或者2.2.6.RELEASE，不能使用官方推荐版本

| 依赖                 | 版本号         |
| -------------------- | -------------- |
| SpringCloud          | Hoxton.SR12    |
| Springboot           | 2.3.12.RELEASE |
| Spring Cloud Alibaba | 2.2.6.RELEASE  |
| Nacos                | 2.0.3          |





--------------------------------------------------------------------------------------- Nacos 结束----------------------------------------------------------------------------------------------------------





## Sentinel



Hystrix与Sentinel比较：

Hystrix
需要我们程序员自己手工搭建监控平台
没有一套web界面可以给我们进行更加细粒度化得配置流控、速率控制、服务熔断、服务降级
Sentinel
单独一个组件，可以独立出来。
直接界面化的细粒度统一配置。
约定 > 配置 > 编码

都可以写在代码里面



服务使用中的各种问题：

- 服务雪崩

- 服务降级
- 服务熔断
- 服务限流

Sentinel 分为两个部分：

- 核心库（Java 客户端）不依赖任何框架/库，能够运行于所有 Java 运行时环境，同时对 Dubbo / Spring Cloud 等框架也有较好的支持。

- 控制台（Dashboard）基于 Spring Boot 开发，打包后可以直接运行，不需要额外的 Tomcat 等应用容器。

安装步骤：

下载

https://github.com/alibaba/Sentinel/releases
下载到本地sentinel-dashboard-1.8.1.jar

运行命令

前提

- Java 8 环境
- 8080端口不能被占用

命令

- java -jar sentinel-dashboard-1.8.1.jar

访问Sentinel管理界面

- localhost:8080
- 登录账号密码均为sentinel



### Sentinel初始化监控



**新建工程 - cloudreview-alibaba-sentinel-service8401**



代码详情见 --------  cloudreview-alibaba  ->>    cloudreview-alibaba-sentinel-service8401



**启动Sentinel8086 - `java -jar sentinel-dashboard-1.8.1.jar`**

**启动微服务8401**

**启动8401微服务后查看sentienl控制台**

- 刚启动，空空如也，啥都没有

- Sentinel采用的懒加载说明
  - 执行一次访问即可
    - http://localhost:8401/testA
    - http://localhost:8401/testB
  - 效果 - sentinel8080正在监控微服务8401



### Sentinel流控规则简介

基本介绍

![](https://img-blog.csdnimg.cn/img_convert/d8ae2bea252af0bb278332b3aeb8fb77.png)

进一步解释说明：

资源名：唯一名称，默认请求路径。
针对来源：Sentinel可以针对调用者进行限流，填写微服务名，默认default（不区分来源）。
阈值类型/单机阈值：
QPS(每秒钟的请求数量)︰当调用该API的QPS达到阈值的时候，进行限流。
线程数：当调用该API的线程数达到阈值的时候，进行限流。
是否集群：不需要集群。
流控模式：
直接：API达到限流条件时，直接限流。
关联：当关联的资源达到阈值时，就限流自己。
链路：只记录指定链路上的流量（指定资源从入口资源进来的流量，如果达到阈值，就进行限流)【API级别的针对来源】。
流控效果：
快速失败：直接失败，抛异常。
Warm up：根据Code Factor（冷加载因子，默认3）的值，从阈值/codeFactor，经过预热时长，才达到设置的QPS阈值。
排队等待：匀速排队，让请求以匀速的速度通过，阈值类型必须设置为QPS，否则无效。



### Sentinel流控-QPS直接失败

**直接 -> 快速失败（系统默认）**

**配置及说明**

表示1秒钟内查询1次就是OK，若超过次数1，就直接->快速失败，报默认错误

![](https://img-blog.csdnimg.cn/img_convert/56642cc2b7dd5b0d1252235c84f69173.png)



**测试**

快速多次点击访问http://localhost:8401/testA

**结果**

返回页面 Blocked by Sentinel (flow limiting)



### Sentinel流控-线程数直接失败

线程数：当调用该API的线程数达到阈值的时候，进行限流。

### Sentinel流控-关联

**设置testA**

当关联资源/testB的QPS阀值超过1时，就限流/testA的Rest访问地址，**当关联资源到阈值后限制配置好的资源名**。

![](https://img-blog.csdnimg.cn/img_convert/12cd41ae91ba50fe3b5525bab7bc3805.png)

### Sentinel流控-预热

Warm Up

Warm Up（RuleConstant.CONTROL_BEHAVIOR_WARM_UP）方式，即预热/冷启动方式。当系统长期处于低水位的情况下，当流量突然增加时，直接把系统拉升到高水位可能瞬间把系统压垮。通过"冷启动"，让通过的流量缓慢增加，在一定时间内逐渐增加到阈值上限，给冷系统一个预热的时间，避免冷系统被压垮。详细文档可以参考 流量控制 - Warm Up 文档，具体的例子可以参见 WarmUpFlowDemo。

通常冷启动的过程系统允许通过的 QPS 曲线如下图所示：

![](https://img-blog.csdnimg.cn/img_convert/ede9b7e029c54840e3b40b69c4f371b5.png)



默认coldFactor为3，即请求QPS 从 threshold / 3开始，经预热时长逐渐升至设定的QPS阈值。

**WarmUp配置**

案例，阀值为10+预热时长设置5秒。

系统初始化的阀值为10/ 3约等于3,即阀值刚开始为3;然后过了5秒后阀值才慢慢升高恢复到10

![](https://img-blog.csdnimg.cn/img_convert/c26846d68d79eae1e962f37942a2c99f.png)



**测试**

多次快速点击http://localhost:8401/testB - 刚开始不行，后续慢慢OK

**应用场景**

如：秒杀系统在开启的瞬间，会有很多流量上来，很有可能把系统打死，预热方式就是把为了保护系统，可慢慢的把流量放进来,慢慢的把阀值增长到设置的阀值



### Sentinel流控-排队等待



匀速排队，让请求以均匀的速度通过，阀值类型必须设成QPS，否则无效。

设置：/testA每秒1次请求，超过的话就排队等待，等待的超时时间为20000毫秒。

![](https://img-blog.csdnimg.cn/img_convert/0ddd217545dd0fe2b1f251dbea814ac2.png)

这种方式主要用于处理间隔性突发的流量，例如消息队列。想象一下这样的场景，在某一秒有大量的请求到来，而接下来的几秒则处于空闲状态，我们希望系统能够在接下来的空闲期间逐渐处理这些请求，而不是在第一秒直接拒绝多余的请求。

注意：匀速排队模式暂时不支持 QPS > 1000 的场景。



### Sentinel降级简介

熔断降级概述

除了流量控制以外，对调用链路中不稳定的资源进行熔断降级也是保障高可用的重要措施之一。一个服务常常会调用别的模块，可能是另外的一个远程服务、数据库，或者第三方 API 等。例如，支付的时候，可能需要远程调用银联提供的 API；查询某个商品的价格，可能需要进行数据库查询。然而，这个被依赖服务的稳定性是不能保证的。如果依赖的服务出现了不稳定的情况，请求的响应时间变长，那么调用服务的方法的响应时间也会变长，线程会产生堆积，最终可能耗尽业务自身的线程池，服务本身也变得不可用。

现代微服务架构都是分布式的，由非常多的服务组成。不同服务之间相互调用，组成复杂的调用链路。以上的问题在链路调用中会产生放大的效果。复杂链路上的某一环不稳定，就可能会层层级联，最终导致整个链路都不可用。因此我们需要对不稳定的弱依赖服务调用进行熔断降级，暂时切断不稳定调用，避免局部不稳定因素导致整体的雪崩。熔断降级作为保护自身的手段，通常在客户端（调用端）进行配置。

![](https://img-blog.csdnimg.cn/img_convert/6a002ef360a4e5f20ee2748a092f0211.png)



RT（平均响应时间，秒级）

平均响应时间 超出阈值 且 在时间窗口内通过的请求>=5，两个条件同时满足后触发降级。
窗口期过后关闭断路器。
RT最大4900（更大的需要通过-Dcsp.sentinel.statistic.max.rt=XXXX才能生效）。
异常比列（秒级）

QPS >= 5且异常比例（秒级统计）超过阈值时，触发降级;时间窗口结束后，关闭降级 。
异常数(分钟级)

异常数(分钟统计）超过阈值时，触发降级;时间窗口结束后，关闭降级
Sentinel熔断降级会在调用链路中某个资源出现不稳定状态时（例如调用超时或异常比例升高)，对这个资源的调用进行限制，让请求快速失败，避免影响到其它的资源而导致级联错误。

当资源被降级后，在接下来的降级时间窗口之内，对该资源的调用都自动熔断（默认行为是抛出 DegradeException）。

Sentinei的断路器是没有类似Hystrix半开状态的。(Sentinei 1.8.0 已有半开状态)

半开的状态系统自动去检测是否请求有异常，没有异常就关闭断路器恢复使用，有异常则继续打开断路器不可用。



### Sentinel降级-RT

平均响应时间(DEGRADE_GRADE_RT)：当1s内持续进入5个请求，对应时刻的平均响应时间（秒级）均超过阈值（ count，以ms为单位），那么在接下的时间窗口（DegradeRule中的timeWindow，以s为单位）之内，对这个方法的调用都会自动地熔断(抛出DegradeException )。注意Sentinel 默认统计的RT上限是4900 ms，超出此阈值的都会算作4900ms，若需要变更此上限可以通过启动配置项-Dcsp.sentinel.statistic.max.rt=xxx来配置。

注意：Sentinel 1.7.0才有平均响应时间（DEGRADE_GRADE_RT），Sentinel 1.8.0的没有这项，取而代之的是慢调用比例 (SLOW_REQUEST_RATIO)。

慢调用比例 (SLOW_REQUEST_RATIO)：选择以慢调用比例作为阈值，需要设置允许的慢调用 RT（即最大的响应时间），请求的响应时间大于该值则统计为慢调用。当单位统计时长（statIntervalMs）内请求数目大于设置的最小请求数目，并且慢调用的比例大于阈值，则接下来的熔断时长内请求会自动被熔断。经过熔断时长后熔断器会进入探测恢复状态（HALF-OPEN 状态），若接下来的一个请求响应时间小于设置的慢调用 RT 则结束熔断，若大于设置的慢调用 RT 则会再次被熔断。



**测试**

```java
	@GetMapping("/testD")
    public String testD() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("testD 测试RT");
        return "------testD";
    }
```



### Sentinel降级-异常比例



异常比例(DEGRADE_GRADE_EXCEPTION_RATIO)：当资源的每秒请求量 >= 5，并且每秒异常总数占通过量的比值超过阈值（ DegradeRule中的 count）之后，资源进入降级状态，即在接下的时间窗口( DegradeRule中的timeWindow，以s为单位）之内，对这个方法的调用都会自动地返回。异常比率的阈值范围是[0.0, 1.0]，代表0% -100%。

注意，与Sentinel 1.8.0相比，有些不同（Sentinel 1.8.0才有的半开状态），Sentinel 1.8.0的如下：

异常比例 (ERROR_RATIO)：当单位统计时长（statIntervalMs）内请求数目大于设置的最小请求数目，并且异常的比例大于阈值，则接下来的熔断时长内请求会自动被熔断。经过熔断时长后熔断器会进入探测恢复状态（HALF-OPEN 状态），若接下来的一个请求成功完成（没有错误）则结束熔断，否则会再次被熔断。异常比率的阈值范围是 [0.0, 1.0]，代表 0% - 100%。



### Sentinel降级-异常数



异常数( DEGRADE_GRADF_EXCEPTION_COUNT )：当资源近1分钟的异常数目超过阈值之后会进行熔断。注意由于统计时间窗口是分钟级别的，若timeWindow小于60s，则结束熔断状态后码可能再进入熔断状态。

注意，与Sentinel 1.8.0相比，有些不同（Sentinel 1.8.0才有的半开状态），Sentinel 1.8.0的如下：

异常数 (ERROR_COUNT)：当单位统计时长内的异常数目超过阈值之后会自动进行熔断。经过熔断时长后熔断器会进入探测恢复状态（HALF-OPEN 状态），若接下来的一个请求成功完成（没有错误）则结束熔断，否则会再次被熔断。



### Sentinel热点key

何为热点？热点即经常访问的数据。很多时候我们希望统计某个热点数据中访问频次最高的 Top K 数据，并对其访问进行限制。比如：

商品 ID 为参数，统计一段时间内最常购买的商品 ID 并进行限制
用户 ID 为参数，针对一段时间内频繁访问的用户 ID 进行限制
热点参数限流会统计传入参数中的热点参数，并根据配置的限流阈值与模式，对包含热点参数的资源调用进行限流。热点参数限流可以看做是一种特殊的流量控制，仅对包含热点参数的资源调用生效。

承上启下复习start

兜底方法，分为系统默认和客户自定义，两种

之前的case，限流出问题后，都是用sentinel系统默认的提示: Blocked by Sentinel (flow limiting)

我们能不能自定？类似hystrix，某个方法出问题了，就找对应的兜底降级方法?

结论 - 从HystrixCommand到@SentinelResource

```java
@RestController
@Slf4j
public class FlowLimitController {

    ...

    @GetMapping("/testHotKey")
    @SentinelResource(value = "testHotKey",blockHandler/*兜底方法*/ = "deal_testHotKey")
    public String testHotKey(@RequestParam(value = "p1",required = false) String p1,
                             @RequestParam(value = "p2",required = false) String p2) {
        //int age = 10/0;
        return "------testHotKey";
    }
    
    /*兜底方法*/
    public String deal_testHotKey (String p1, String p2, BlockException exception) {
        return "------deal_testHotKey,o(╥﹏╥)o";  //sentinel系统默认的提示：Blocked by Sentinel (flow limiting)
    }

}
```

一

@SentinelResource(value = "testHotKey")    这里注意 配置热点资源名不能用路径！！！！！！！！因为使用了@SentinelResource(value = "testHotKey")
异常打到了前台用户界面看到，不友好
二

@SentinelResource(value = "testHotKey", blockHandler = "dealHandler_testHotKey")
方法testHotKey里面第一个参数只要QPS超过每秒1次，马上降级处理
异常用了我们自己定义的兜底方法
测试

error
http://localhost:8401/testHotKey?p1=abc
http://localhost:8401/testHotKey?p1=abc&p2=33
right
http://localhost:8401/testHotKey?p2=abc

上述案例演示了第一个参数p1，当QPS超过1秒1次点击后马上被限流。

**参数例外项**

- 普通 - 超过1秒钟一个后，达到阈值1后马上被限流
- **我们期望p1参数当它是某个特殊值时，它的限流值和平时不一样**
- 特例 - 假如当p1的值等于5时，它的阈值可以达到200

![](https://img-blog.csdnimg.cn/img_convert/3aa08b15109cd346a6083f080a0468fa.png)



测试

right - http://localhost:8401/testHotKey?p1=5
error - http://localhost:8401/testHotKey?p1=3
当p1等于5的时候，阈值变为200
当p1不等于5的时候，阈值就是平常的1
前提条件 - 热点参数的注意点，参数必须是基本类型或者String

其它

在方法体抛异常

```java
@RestController
@Slf4j
public class FlowLimitController{

    ...

    @GetMapping("/testHotKey")
    @SentinelResource(value = "testHotKey",blockHandler/*兜底方法*/ = "deal_testHotKey")
    public String testHotKey(@RequestParam(value = "p1",required = false) String p1,
                             @RequestParam(value = "p2",required = false) String p2) {
        int age = 10/0;//<----------------------------会抛异常的地方
        return "------testHotKey";
    }
    
    /*兜底方法*/
    public String deal_testHotKey (String p1, String p2, BlockException exception) {
        return "------deal_testHotKey,o(╥﹏╥)o";  //sentinel系统默认的提示：Blocked by Sentinel (flow limiting)
    }

}
```

将会抛出Spring Boot 2的默认异常页面，而不是兜底方法。

@SentinelResource - 处理的是sentinel控制台配置的违规情况，有blockHandler方法配置的兜底处理;

RuntimeException int age = 10/0，这个是java运行时报出的运行时异常RunTimeException，@SentinelResource不管

**总结 - @SentinelResource主管配置出错，运行出错该走异常走异常**



### Sentinel持久化规则



一旦我们重启应用，sentinel规则将消失，生产环境需要将配置规则进行持久化。



将限流配置规则持久化进Nacos保存，只要刷新8401某个rest地址，sentinel控制台的流控规则就能看到，只要Nacos里面的配置不删除，针对8401上sentinel上的流控规则持续有效。

pom

```xml
		<!-- SpringCloud AliBaBa sentinel持久化 依赖 -->
        <dependency>
            <groupId>com.alibaba.csp</groupId>
            <artifactId>sentinel-datasource-nacos</artifactId>
        </dependency>
```

yml

```yaml
sentinel:
      transport:
        dashboard: localhost:8086 # 配置Sentinel dashboard地址
        port: 8719
      datasource: #添加Nacos数据源配置,sentinel持久化
        # 自定义的流控规则数据源名称
        flow:
          nacos:
            server-addr: localhost:8848
            dataId: cloudreview-alibaba-sentinel-service
            groupId: DEFAULT_GROUP
            data-type: json
            rule-type: flow
        # 自定义的降级规则数据源名称
        degrade:
          nacos:
            server-addr: localhost:8848
            dataId: cloudreview-alibaba-sentinel-service
            groupId: DEFAULT_GROUP
            data-type: json
            rule-type: degrade
```

#### 添加自定义的流控规则

`向naccos中添加自定义的流控规则`：Nacos Server中添加对应规则配置集`（public命名空间—>DEFAULT_GROUP中添加）

流控规则所有属性来⾃源码FlowRule类

#### 添加自定义的降级规则配置集

降级规则配置集的所有属性来⾃源码DegradeRule类

注意！！！！！！

1. ⼀个资源可以同时有多个限流规则和降级规则，所以配置集中是⼀个json 数组
2. `Sentinel控制台中修改规则`，仅是内存中⽣效，不会修改Nacos中的配置值，重启后恢复原来的值；
3. `Nacos控制台中修改规则`，不仅内存中⽣效，Nacos中持久化规则也⽣效，重启后规则依然保持

```java
public String toString() {
        return "FlowRule{resource=" + this.getResource() + ", limitApp=" + this.getLimitApp() + ", grade=" + this.grade + ", count=" + this.count + ", strategy=" + this.strategy + ", refResource=" + this.refResource + ", controlBehavior=" + this.controlBehavior + ", warmUpPeriodSec=" + this.warmUpPeriodSec + ", maxQueueingTimeMs=" + this.maxQueueingTimeMs + ", clusterMode=" + this.clusterMode + ", clusterConfig=" + this.clusterConfig + ", controller=" + this.controller + '}';
    }

public String toString() {
        return "DegradeRule{resource=" + this.getResource() + ", grade=" + this.grade + ", count=" + this.count + ", limitApp=" + this.getLimitApp() + ", timeWindow=" + this.timeWindow + ", minRequestAmount=" + this.minRequestAmount + ", slowRatioThreshold=" + this.slowRatioThreshold + ", statIntervalMs=" + this.statIntervalMs + '}';
    }
```

格式

```json
[{
	"resource": "/testA",
	"IimitApp": "default",
	"grade": 1,
	"count": 1,
	"strategy": 0,
	"controlBehavior": 0,
	"clusterMode": false
}]

```

```json
[{
	"resource": "/testD",
	"grade": 0,
	"count": 500,
	"slowRatioThreshold": 0.5,
	"timeWindow": 1,
	"minRequestAmount": 5,
	"statIntervalMs": 10000
}]
```

流控规则说明

resource：资源名称；
limitApp：来源应用；
grade：阈值类型，0表示线程数, 1表示QPS；
count：单机阈值；
strategy：流控模式，0表示直接，1表示关联，2表示链路；
controlBehavior：流控效果，0表示快速失败，1表示Warm Up，2表示排队等待；
clusterMode：是否集群

refResource  	关联资源       warmUpPeriodSec  预热时长    maxQueueingTimeMs   排队等待超时时间

降级规制说明

Field	说明	默认值
resource	资源名，即规则的作用对象	 
grade	熔断策略，支持慢调用比例/异常比例/异常数策略	慢调用比例
count	慢调用比例模式下为慢调用临界 RT（超出该值计为慢调用）；异常比例/异常数模式下为对应的阈值	 
timeWindow	熔断时长，单位为 s	 
minRequestAmount	熔断触发的最小请求数，请求数小于该值时即使异常比率超出阈值也不会熔断（1.7.0 引入）	5
statIntervalMs	统计时长（单位为 ms），如 60*1000 代表分钟级（1.8.0 引入）	1000 ms
slowRatioThreshold	慢调用比例阈值，仅慢调用比例模式有效（1.8.0 引入）	   



到这里只实现了nacos 配置 持久化到sentinel

下面要实现nacos sentinel 双向绑定



sentinel 的持久化，我们希望这样：

可以在 sentinel 控制台中编辑 限流配置，并且同步到 nacos 做持久化
在 nacos 中修改了限流配置，也可以同步到 sentinel 控制台
要实现上述第一个功能需要对 sentinel 控制台的源码有所了解，并加依改造。

具体修改步骤

https://www.dounaite.com/article/62c368a5f4ab41be486c0509.html

yml设置

```yaml
	sentinel:
      transport:
        dashboard: localhost:8086 # 配置Sentinel dashboard地址
        port: 8719
      datasource: #添加Nacos数据源配置,sentinel持久化
        # 自定义的流控规则数据源名称
        flow:
          nacos:
            server-addr: localhost:8848
            dataId: sentinel.rule.flow
            groupId: ${spring.application.name}
            data-type: json
            rule-type: flow
        # 自定义的降级规则数据源名称
        degrade:
          nacos:
            server-addr: localhost:8848
            dataId: sentinel.rule.degrade
            groupId: ${spring.application.name}
            data-type: json
            rule-type: degrade
```



### Sentinel  结合  nacos 及 open feign使用   



代码详情见 --------  cloudreview-alibaba  ->>   cloudreview-alibaba-provider-sentinel-payment9003  cloudreview-alibaba-provider-sentinel-payment9004

​																		cloudreview-alibaba-consumer-feign-sentinel-order80







--------------------------------------------------------------------------------------- Sentinel 结束----------------------------------------------------------------------------------------------------------





## Seata





### seata-server搭建



下载 seata-server  （这里用1.3）

https://github.com/seata/seata/releases/download/v1.3.0/seata-server-1.3.0.zip

修改seata-server配置  registry.conf , 把注册中心和配置中心都指向你的nacos！！！！

```
registry {
  # file 、nacos 、eureka、redis、zk、consul、etcd3、sofa
  type = "nacos"

  nacos {
    application = "seata-server"
    serverAddr = "127.0.0.1:8848"
    group = "SEATA_GROUP"
    namespace = "eb582f4a-7412-4cf3-8897-a0c4a9d3f75e"
    cluster = "cloudreview-alibaba"
    username = "nacos"
    password = "nacos"
  }
  eureka {
    serviceUrl = "http://localhost:8761/eureka"
    application = "default"
    weight = "1"
  }
  redis {
    serverAddr = "localhost:6379"
    db = 0
    password = ""
    cluster = "default"
    timeout = 0
  }
  zk {
    cluster = "default"
    serverAddr = "127.0.0.1:2181"
    sessionTimeout = 6000
    connectTimeout = 2000
    username = ""
    password = ""
  }
  consul {
    cluster = "default"
    serverAddr = "127.0.0.1:8500"
  }
  etcd3 {
    cluster = "default"
    serverAddr = "http://localhost:2379"
  }
  sofa {
    serverAddr = "127.0.0.1:9603"
    application = "default"
    region = "DEFAULT_ZONE"
    datacenter = "DefaultDataCenter"
    cluster = "default"
    group = "SEATA_GROUP"
    addressWaitTime = "3000"
  }
  file {
    name = "file.conf"
  }
}

config {
  # file、nacos 、apollo、zk、consul、etcd3
  type = "nacos"

  nacos {
    serverAddr = "127.0.0.1:8848"
    namespace = "eb582f4a-7412-4cf3-8897-a0c4a9d3f75e"
    group = "SEATA_GROUP"
    username = "nacos"
    password = "nacos"
  }
  consul {
    serverAddr = "127.0.0.1:8500"
  }
  apollo {
    appId = "seata-server"
    apolloMeta = "http://192.168.1.204:8801"
    namespace = "application"
  }
  zk {
    serverAddr = "127.0.0.1:2181"
    sessionTimeout = 6000
    connectTimeout = 2000
    username = ""
    password = ""
  }
  etcd3 {
    serverAddr = "http://localhost:2379"
  }
  file {
    name = "file.conf"
  }
}
```



下载 seata-server源码包   https://github.com/seata/seata/archive/refs/tags/v1.3.0.zip



在包内找到    \seata-1.3.0\script\server\db\mysql.sql

新建seate数据库

执行上述sql，生成3张seata-server使用的表



找到路径    \seata-1.3.0\script\config-center  参考原来的config.txt 编写初始化  seata-server的配置

```
service.vgroupMapping.my_test_tx_group=cloudreview-alibaba
store.mode=db
store.db.datasource=druid
store.db.dbType=mysql
store.db.driverClassName=com.mysql.cj.jdbc.Driver
store.db.url=jdbc:mysql://127.0.0.1:3306/seata?serverTimezone=GMT%2B8&useSSL=false&useUnicode=true&characterEncoding=utf-8
store.db.user=root
store.db.password=123456
```



在此目录下找到nacos文件夹，进入，用git命令执行sh脚本

```
sh nacos-config.sh -h 你的nacosIP -p 你的nacos端口 -t 推送目标namespace -g 推送目标group -u nacos用户名 -w nacos密码
```

如下

```
sh nacos-config.sh -h localhost -p 8848 -t eb582f4a-7412-4cf3-8897-a0c4a9d3f75e -g SEATA_GROUP -u nacos -w nacos
```

为了方便可以把config.txt 和 nacos-config.sh 复制到 bin或者conf目录下（从源码包放到seata-server解压的包下面）



执行后查看nacos配置是否新增seata-server配置

![](https://img2020.cnblogs.com/blog/543306/202010/543306-20201005172124784-1611920160.png)



另外需要查看store.db.url中参数是否丢失，如果丢失要重新配置一下



最后运行  seata-server解压的包下面 bin 中的 seata-server.bat

提示：Server started, listen port: 8091
到nacos里可以看到，名为seata-server的服务已经注册进来了，至此seata-server搭建完成





### seata整合nacos-sentinel-openfeign





代码详情见 --------  cloudreview-alibaba  ->>   seata-order-service2001   seata-storage-service2002     seata-account-service2003

​																		 seata-business-service80







​																		

--------------------------------------------------------------------------------------- Seata结束----------------------------------------------------------------------------------------------------------





根据   https://www.bilibili.com/video/BV18E411x7eT?spm_id_from=333.999.0.0  教程 及   https://blog.csdn.net/u011863024/article/details/114298270 笔记









































