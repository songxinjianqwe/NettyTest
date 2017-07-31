# Netty

## 打成可执行jar包
需要在pom.xml中加入maven-jar-plugin，并设置主程序入口
加入maven-dependency-plugin，用于将依赖的jar包打包至/lib文件夹下
运行时需要将jar包和lib文件夹放到同一目录下，并在命令行中执行java -jar命令
```
<resources>
    <!-- 控制资源文件的拷贝 -->
    <resource>
        <directory>src/main/resources</directory>
        <targetPath>${project.build.directory}/classes</targetPath>
    </resource>
</resources>

<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-jar-plugin</artifactId>
        <version>2.4</version>
        <configuration>
            <archive>
                <manifest>
                    <addClasspath>true</addClasspath>
                    <classpathPrefix>lib/</classpathPrefix>
                    <mainClass>cn.sinjinsong.netty.echo.server.Server</mainClass>
                </manifest>
            </archive>
        </configuration>
    </plugin>
    <!-- 拷贝依赖的jar包到lib目录 -->
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-dependency-plugin</artifactId>
        <executions>
            <execution>
                <id>copy</id>
                <phase>package</phase>
                <goals>
                    <goal>copy-dependencies</goal>
                </goals>
                <configuration>
                    <outputDirectory>
                        ${project.build.directory}/lib
                    </outputDirectory>
                </configuration>
            </execution>
        </executions>
    </plugin>
```

### TCP粘包、分包问题
TCP是面向流的协议，每次发送数据之间没有间隔，如需将每次发送的数据分隔开来，那么需要使用一些特殊的方法

1. 消息定长
FixedLengthFrameDecoder定长解码器可以帮助我们轻松实现第一种解决方案，定长解码报文。
如果原始数据的长度不够定长，需要增加空格来达到定长。

2. 特殊分隔符

3. 消息头和消息体，消息头放总长度


### 编解码技术（序列化）
ProtoStuff

### 连接保持&心跳检测
心跳机制的工作原理是: 在服务器和客户端之间一定时间内没有数据交互时, 即处于 idle 状态时, 
客户端或服务器会发送一个特殊的数据包给对方, 当接收方收到这个数据报文后, 
也立即发送一个特殊的数据报文, 回应发送方, 此即一个 PING-PONG 交互. 
自然地, 当某一端收到心跳消息后, 就知道了对方仍然在线, 这就确保 TCP 连接的有效性.


使用 Netty 实现心跳机制的关键就是利用 IdleStateHandler 来产生对应的 idle 事件.
一般是客户端负责发送心跳的 PING 消息, 因此客户端注意关注 ALL_IDLE 事件, 在这个事件触发后, 客户端需要向服务器发送 PING 消息, 告诉服务器"我还存活着".
服务器是接收客户端的 PING 消息的, 因此服务器关注的是 READER_IDLE 事件, 并且服务器的 READER_IDLE 间隔需要比客户端的 ALL_IDLE 事件间隔大(例如客户端ALL_IDLE 是5s 没有读写时触发, 因此服务器的 READER_IDLE 可以设置为10s)
当服务器收到客户端的 PING 消息时, 会发送一个 PONG 消息作为回复. 一个 PING-PONG 消息对就是一个心跳交互.


