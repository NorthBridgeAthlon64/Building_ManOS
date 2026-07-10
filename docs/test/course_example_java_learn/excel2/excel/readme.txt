mvn archetype:generate -DgroupId=edu.dlut.excel -DartifactId=excel -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.5 -DinteractiveMode=false

然后编辑
App.java

测试项目中的
AppTest.java


修改pom.xml文件，添加依赖包
修改pom.xml文件，修改版本，1.0

启动mysql server

复制list.xlsx文件到excel文件夹下，也就是当前文件夹，current folder

mvn clean
mvn compile
mvn package
mvn test

一定是编译之后才能运行，需要exec plugin的支持

<plugin>
          <groupId>org.codehaus.mojo</groupId>
          <artifactId>exec-maven-plugin</artifactId>
          <version>3.5.1</version>
          <executions>
            <execution>
              <goals>
                <goal>java</goal>
              </goals>
            </execution>
          </executions>

          <configuration>
            <includeProjectDependencies>true</includeProjectDependencies>
            <includePluginDependencies>true</includePluginDependencies>
         
            <mainClass>edu.dlut.excel.App</mainClass>

            <systemProperties>
              <systemProperty>
                <key>myproperty</key>
                <value>myvalue</value>
              </systemProperty>
            </systemProperties>
          </configuration>
        </plugin>

mvn clean compile exec:java
mvn exec:java

接下来进行“运行”的处理过程，比如：打包成一个jar文件，并指明主类
需要添加：maven-assembly-plugin
需要添加打包文件：assembly.xml



mvn clean package assembly:single   

然后运行：
java -jar target/excel-1.0-jar-with-dependencies.jar


