<style>
.codey { background-color:#CCCCCC; }
.level-2 {
    padding-left: 5em;
}

.level-3 {
    padding-left: 7em;
}
.level-4 {
    padding-left: 8em;
    background: #333333;
    color: white;
}
</style>

<a name="TOC">

**Create a Project With**  
</a>
- [Spring Security](#SpringSecurity)  
- [REST Services](#RestServices)  
**Want Access To**  
- [Session Factory](#SessionFactory)  
- [Data Source](#DataSource)  
- [Data Services](#DataServices)  
**Application.Properties**  
- [Change Database Information](#ChangeDB)  
- [Rename File](#AppPropName)  
- [Change URL](#AppURL)  
- [Change Password](#AppPW)  



# WHAT NEEDS TO BE DONE

## Create A Project With
<section class="level-2">
<a name="SpringSecurity">

### Spring Security
</a>

<section class="level-2">
#### Starting with Spring Initializr

For all Spring applications, you should start with the [Spring Initializr](https://start.spring.io/). The Initializr offers a fast way to pull in all the dependencies you need for an application and does a lot of the setup for you. This example needs the Spring Web and Thymeleaf dependencies. The following image shows the Initializr set up for this sample project:

The following listing shows the <span class="codey">pom.xml</span> file that is created when you choose Maven:

<section class="level-4">
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.2.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.example</groupId>
    <artifactId>securing-web</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>securing-web</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```
</section>

The following listing shows the <span class="codey">build.gradle</span> file that is created when you choose Gradle:

<section class="level-4">
```
plugins {
    id 'org.springframework.boot' version '2.2.2.RELEASE'
    id 'io.spring.dependency-management' version '1.0.8.RELEASE'
    id 'java'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '1.8'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    testImplementation('org.springframework.boot:spring-boot-starter-test') {
        exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
    }
}

test {
    useJUnitPlatform()
}
```
</section>

### Set up Spring Security

Suppose that you want to prevent unauthorized users from viewing the greeting page at /hello. As it is now, if visitors click the link on the home page, they see the greeting with no barriers to stop them. You need to add a barrier that forces the visitor to sign in before they can see that page.

You do that by configuring Spring Security in the application. If Spring Security is on the classpath, Spring Boot [automatically secures all HTTP endpoints](https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/reference/htmlsingle/#boot-features-security) with "basic" authentication. However, you can further customize the security settings. The first thing you need to do is add Spring Security to the classpath.

With Gradle, you need to add two lines (one for the application and one for testing) in the <span class="codey">dependencies</span> closure in <span class="codey">build.gradle</span>, as the following listing shows:

<section class="level-4">
```
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'org.springframework.security:spring-security-test'
```
</section>

The following listing shows the finished <span class="codey">build.gradle</span> file:\

<section class="level-4">
```
plugins {
    id 'org.springframework.boot' version '2.2.2.RELEASE'
    id 'io.spring.dependency-management' version '1.0.8.RELEASE'
    id 'java'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '1.8'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.security:spring-security-test'
    testImplementation('org.springframework.boot:spring-boot-starter-test') {
        exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
    }
}

test {
    useJUnitPlatform()
}
```
</section>

With Maven, you need to add two extra entries (one for the application and one for testing) to the <span class="codey"><dependencies></span> element in <span class="codey">pom.xml</span>, as the following listing shows:

<section class="level-4">
```
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-test</artifactId>
  <scope>test</scope>
</dependency>
```
</section>

The following listing shows the finished <span class="codey">pom.xml</span> file:

<section class="level-4">
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.2.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.example</groupId>
    <artifactId>securing-web</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>securing-web</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```
</section>

The following security configuration (from <span class="codey">src/main/java/com/example/securingweb/WebSecurityConfig.java</span>) ensures that only authenticated users can see the secret greeting:

<section class="level-4">
```
package com.example.securingweb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/home").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
                .permitAll();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails user =
             User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}
```
</section>

We display the username by using Spring Security's integration with <span class="codey">HttpServletRequest#getRemoteUser()</span>. The "Sign Out" form submits a POST to <span class="codey">/logout</span>. Upon successfully logging out, it redirects the user to <span class="codey">/login?logout</span>.

### Run the Application

The Spring Initializr creates an application class for you. In this case, you need not modify the class. The following listing (from <span class="codey">src/main/java/com/example/securingweb/SecuringWebApplication.java</span>) shows the application class:

<section class="level-4">
```
package com.example.securingweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecuringWebApplication {

    public static void main(String[] args) throws Throwable {
        SpringApplication.run(SecuringWebApplication.class, args);
    }

}
```
</section>

####Build an executable JAR

You can run the application from the command line with Gradle or Maven. You can also build a single executable JAR file that contains all the necessary dependencies, classes, and resources and run that. Building an executable jar so makes it easy to ship, version, and deploy the service as an application throughout the development lifecycle, across different environments, and so forth.

If you use Gradle, you can run the application by using <span class="codey">./gradlew bootRun</span>. Alternatively, you can build the JAR file by using <span class="codey">./gradlew</span> build and then run the JAR file, as follows:

<section class="level-4">
```
java -jar build/libs/gs-securing-web-0.1.0.jar
```
</section>

If you use Maven, you can run the application by using <span class="codey">./mvnw spring-boot:run</span>. Alternatively, you can build the JAR file with <span class="codey">./mvnw clean</span> package and then run the JAR file, as follows:

<section class="level-4">
```
java -jar target/gs-securing-web-0.1.0.jar
```
</section>

Once the application starts up, point your browser to <span class="codey">http://localhost:8080</span>. You should see the home page..

When you click on the link, it attempts to take you to the greeting page at <span class="codey">/hello</span>. However, because that page is secured and you have not yet logged in, it takes you to the login page, as the following image shows:

At the login page, sign in as the test user by entering <span class="codey">user</span> and <span class="codey">password</span> for the username and password fields, respectively. Once you submit the login form, you are authenticated and then taken to the greeting page, as the following image shows:

If you click on the **Sign Out** button, your authentication is revoked, and you are returned to the login page with a message indicating that you are logged out.

**[Source to this madness](https://spring.io/guides/gs/securing-web/)**
</section>

- Authentication
- Authorization

[TOC](#TOC)

</section>

<section class="level-2">
<a name="RestServices">

### REST Services
</a>
<section class="level-2">

#### Understanding REST in Spring

The Spring framework supports two ways of creating RESTful services:

- using MVC with <span class="codey">ModelAndView</span>
- using HTTP message converters

The <span class="codey">ModelAndView</span> approach is older and much better documented, but also more verbose and configuration heavy. It tries to shoehorn the REST paradigm into the old model, which is not without problems. The Spring team understood this and provided first-class REST support starting with Spring 3.0.

**The new approach, based on <span class="codey">HttpMessageConverter</span> and annotations, is much more lightweight and easy to implement**. Configuration is minimal, and it provides sensible defaults for what you would expect from a RESTful service.

#### The Java Configuration

<section class="level-4">
```
@Configuration
@EnableWebMvc
public class WebConfig{
   //
}
```
</section>

The new <span class="codey">@EnableWebMvc</span> annotation does some useful things - specifically, in the case of REST, it detects the existence of Jackson and JAXB 2 on the classpath and automatically creates and registers default JSON and XML converters. The functionality of the annotation is equivalent to the XML version:

<span class="codey"><mvc:annotation-driven /></span>

This is a shortcut, and though it may be useful in many situations, it's not perfect. When more complex configuration is needed, remove the annotation and extend <span class="codey">WebMvcConfigurationSupport</span> directly.


###### Using Spring Boot

If we're using the <span class="codey">@SpringBootApplication</span> annotation and the <span class="codey">spring-webmvc</span> library is on the classpath, then the <span class="codey">@EnableWebMvc</span> annotation is added automatically with a default autoconfiguration.

We can still add MVC functionality to this configuration by implementing the <span class="codey">WebMvcConfigurer</span> interface on a <span class="codey">@Configuration</span> annotated class. We can also use a <span class="codey">WebMvcRegistrationsAdapter</span> instance to provide our own <span class="codey">RequestMappingHandlerMapping</span>, <span class="codey">RequestMappingHandlerAdapter</span>, or <span class="codey">ExceptionHandlerExceptionResolver</span> implementations.

Finally, if we want to discard Spring Boot's MVC features and declare a custom configuration, we can do so by using the <span class="codey">@EnableWebMvc</span> annotation.

#### Testing the Spring Context

Starting with Spring 3.1, we get first-class testing support for <span class="codey">@Configuration</span> classes:

<section class="level-4">
```
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( 
  classes = {WebConfig.class, PersistenceConfig.class},
  loader = AnnotationConfigContextLoader.class)
public class SpringContextIntegrationTest {
 
   @Test
   public void contextLoads(){
      // When
   }
}
```
</section>

We're specifying the Java configuration classes with the <span class="codey">@ContextConfiguration</span> annotation. The new <span class="codey">AnnotationConfigContextLoader</span> loads the bean definitions from the <span class="codey">@Configuration</span> classes.

Notice that the <span class="codey">WebConfig</span> configuration class was not included in the test because it needs to run in a Servlet context, which is not provided.

##### Using Spring Boot

Spring Boot provides several annotations to set up the Spring <span class="codey">ApplicationContext</span> for our tests in a more intuitive way.

We can load only a particular slice of the application configuration, or we can simulate the whole context startup process.

For instance, we can use the <span class="codey">@SpringBootTest</span> annotation if we want the entire context to be created without starting the server.

With that in place, we can then add the <span class="codey">@AutoConfigureMockMvc</span> to inject a <span class="codey">MockMvc</span> instance and send HTTP requests:

<section class="level-4">
```
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FooControllerAppIntegrationTest {
 
    @Autowired
    private MockMvc mockMvc;
 
    @Test
    public void whenTestApp_thenEmptyResponse() throws Exception {
        this.mockMvc.perform(get("/foos")
            .andExpect(status().isOk())
            .andExpect(...);
    }
 
}
```
</section>

You may have noticed I'm using a straightforward, Guava style <span class="codey">RestPreconditions</span> utility:

<section class="level-4">
```
    public class RestPreconditions {
    public static <T> T checkFound(T resource) {
        if (resource == null) {
            throw new MyResourceNotFoundException();
        }
        return resource;
    }
}
```
</section>
**The Controller implementation is non-public - this is because it doesn't need to be.**

Usually, the controller is the last in the chain of dependencies. It receives HTTP requests from the Spring front controller (the <span class="codey">DispatcherServlet</span>) and simply delegates them forward to a service layer. If there's no use case where the controller has to be injected or manipulated through a direct reference, then I prefer not to declare it as public.

The request mappings are straightforward. **As with any controller, the actual <span class="codey">value</span> of the mapping, as well as the HTTP method, determine the target method for the request**. <span class="codey">@RequestBody</span> will bind the parameters of the method to the body of the HTTP request, whereas <span class="codey">@ResponseBody</span> does the same for the response and return type.

The @RestController is a shorthand to include both the @ResponseBody and the @Controller annotations in our class.

They also ensure that the resource will be marshalled and unmarshalled using the correct HTTP converter. Content negotiation will take place to choose which one of the active converters will be used, based mostly on the <span class="codey">Accept</span> header, although other HTTP headers may be used to determine the representation as well.

#### Mapping the HTTP Response Codes

The status codes of the HTTP response are one of the most important parts of the REST service, and the subject can quickly become very complicated. Getting these right can be what makes or breaks the service.

##### Unmapped Requests

If Spring MVC receives a request which doesn't have a mapping, it considers the request not to be allowed and returns a 405 METHOD NOT ALLOWED back to the client.

It's also a good practice to include the <span class="codey">Allow</span> HTTP header when returning a <span class="codey">405</span> to the client, to specify which operations are allowed. This is the standard behavior of Spring MVC and doesn't require any additional configuration.

##### Valid Mapped Requests

For any request that does have a mapping, Spring MVC considers the request valid and responds with 200 OK if no other status code is specified otherwise.

It's because of this that the controller declares different <span class="codey">@ResponseStatus</span> for the <span class="codey">create, update</span> and <span class="codey">delete</span> actions but not for <span class="codey">get</span>, which should indeed return the default 200 OK.

##### Client Error

<span class="codey">In the case of a client error, custom exceptions are defined and mapped to the appropriate error codes.</span>

Simply throwing these exceptions from any of the layers of the web tier will ensure Spring maps the corresponding status code on the HTTP response:

<section class="level-4">
```
    @ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
   //
}
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
   //
}
```
</section>

These exceptions are part of the REST API and, as such, should only be used in the appropriate layers corresponding to REST; if for instance, a DAO/DAL layer exists, it should not use the exceptions directly.

Note also that these are not checked exceptions but runtime exceptions - in line with Spring practices and idioms.

##### Using <span class="codey">@ExceptionHandler</span>

Another option to map custom exceptions on specific status codes is to use the <span class="codey">@ExceptionHandler</span> annotation in the controller. The problem with that approach is that the annotation only applies to the controller in which it's defined. This means that we need to declares in each controller individually.

Of course, there are more ways to handle errors in both Spring and Spring Boot that offer more flexibility.

#### Additional Maven Dependencies

In addition to the <span class="codey">spring-webmvc</span> dependency required for the standard web application, we'll need to set up content marshalling and unmarshalling for the REST API:

<section class="level-4">
```
<dependencies>
   <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
      <version>2.9.8</version>
   </dependency>
   <dependency>
      <groupId>javax.xml.bind</groupId>
      <artifactId>jaxb-api</artifactId>
      <version>2.3.1</version>
      <scope>runtime</scope>
   </dependency>
</dependencies>
```
</section>

These are the libraries used to convert the representation of the REST resource to either JSON or XML.

##### Using Spring Boot

If we want to retrieve JSON-formatted resources, Spring Boot provides support for different libraries, namely Jackson, Gson and JSON-B.

Auto-configuration is carried out by just including any of the mapping libraries in the classpath.

Usually, if we're developing a web application, **we'll just add the <span class="codey">spring-boot-starter-web</span> dependency and rely on it to include all the necessary artifacts to our project**:

<section class="level-4">
```
    <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.1.2.RELEASE</version>
</dependency>
```
</section>

Spring Boot uses Jackson by default.

If we want to serialize our resources in an XML format, we'll have to add the Jackson XML extension (<span class="codey">jackson-dataformat-xml</span>) to our dependencies, or fallback to the JAXB implementation (provided by default in the JDK) by using the <span class="codey">@XmlRootElement</span> annotation on our resource.

</section>
</section>

<section class="level-2">
### File Uploading Service

<section class="level-2">

The following listing shows the <span class="codey">pom.xml</span> file that is created when you choose Maven:

<section class="level-4">
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.0.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.example</groupId>
    <artifactId>uploading-files</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>uploading-files</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```
</section>

The following listing shows the <span class="codey">build.gradle</span> file that is created when you choose Gradle:

<section class="level-4">
```
plugins {
    id 'org.springframework.boot' version '2.2.0.RELEASE'
    id 'io.spring.dependency-management' version '1.0.8.RELEASE'
    id 'java'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '1.8'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    testImplementation('org.springframework.boot:spring-boot-starter-test') {
        exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
    }
}

test {
    useJUnitPlatform()
}
```
</section>

#### Create an Application Class

To start a Spring Boot MVC application, you first need a starter. In this sample, <span class="codey">spring-boot-starter-thymeleaf</span> and <span class="codey">spring-boot-starter-web</span> are already added as dependencies. To upload files with Servlet containers, you need to register a <span class="codey">MultipartConfigElement</span> class (which would be <span class="codey"><multipart-config></span> in web.xml). Thanks to Spring Boot, everything is auto-configured for you!

All you need to get started with this application is the following <span class="codey">UploadingFilesApplication</span> class (from <span class="codey">src/main/java/com/example/uploadingfiles/UploadingFilesApplication.java</span>):

<section class="level-4">
```
package com.example.uploadingfiles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UploadingFilesApplication {

    public static void main(String[] args) {
        SpringApplication.run(UploadingFilesApplication.class, args);
    }

}
```
</section>

As part of auto-configuring Spring MVC, Spring Boot will create a <span class="codey">MultipartConfigElement</span> bean and make itself ready for file uploads.

#### Create a File Upload Controller

The initial application already contains a few classes to deal with storing and loading the uploaded files on disk. They are all located in the <span class="codey">com.example.uploadingfiles.storage</span> package. You will use those in your new <span class="codey">FileUploadController</span>. The following listing (from <span class="codey">src/main/java/com/example/uploadingfiles/FileUploadController.java</span>) shows the file upload controller:

<section class="level-4">
```
package com.example.uploadingfiles;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.uploadingfiles.storage.StorageFileNotFoundException;
import com.example.uploadingfiles.storage.StorageService;

@Controller
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
```
</section>

The <span class="codey">FileUploadController</span> class is annotated with <span class="codey">@Controller</span> so that Spring MVC can pick it up and look for routes. Each method is tagged with <span class="codey">@GetMapping</span> or <span class="codey">@PostMapping</span> to tie the path and the HTTP action to a particular controller action.

In this case:

- <span class="codey">GET /</span>: Looks up the current list of uploaded files from the <span class="codey">StorageService</span> and loads it into a Thymeleaf template. It calculates a link to the actual resource by using <span class="codey">MvcUriComponentsBuilder</span>.
- <span class="codey">GET /files/{filename}</span>: Loads the resource (if it exists) and sends it to the browser to download by using a <span class="codey">Content-Disposition</span> response header.
- <span class="codey">POST /</span>: Handles a multi-part message <span class="codey">file</span> and gives it to the <span class="codey">StorageService</span> for saving.

You will need to provide a <span class="codey">StorageService</span> so that the controller can interact with a storage layer (such as a file system). The following listing (from <span class="codey">src/main/java/com/example/uploadingfiles/storage/StorageService.java</span>) shows that interface:

<section class="level-4">
```
package com.example.uploadingfiles.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void store(MultipartFile file);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

}
```
</section>

#### Creating an HTML Template

The following Thymeleaf template (from <span class="codey">src/main/resources/templates/uploadForm.html</span>) shows an example of how to upload files and show what has been uploaded:

<section class="level-4">
```
<html xmlns:th="https://www.thymeleaf.org">
<body>

    <div th:if="${message}">
        <h2 th:text="${message}"/>
    </div>

    <div>
        <form method="POST" enctype="multipart/form-data" action="/">
            <table>
                <tr><td>File to upload:</td><td><input type="file" name="file" /></td></tr>
                <tr><td></td><td><input type="submit" value="Upload" /></td></tr>
            </table>
        </form>
    </div>

    <div>
        <ul>
            <li th:each="file : ${files}">
                <a th:href="${file}" th:text="${file}" />
            </li>
        </ul>
    </div>

</body>
</html>
```
</section>

This template has three parts:
- An optional message at the top where Spring MVC writes a flash-scoped message.
- A form that lets the user upload files.
- A list of files supplied from the backend.

#### Tuning File Upload Limits

When configuring file uploads, it is often useful to set limits on the size of files. Imagine trying to handle a 5GB file upload! With Spring Boot, we can tune its auto-configured <span class="codey">MultipartConfigElement</span> with some property settings.

Add the following properties to your existing properties settings (in <span class="codey">src/main/resources/application.properties</span>):

<section class="level-4">
```
spring.servlet.multipart.max-file-size=128KB
spring.servlet.multipart.max-request-size=128KB
```
</section>

The multipart settings are constrained as follows:
- <span class="codey">spring.http.multipart.max-file-size</span> is set to 128KB, meaning total file size cannot exceed 128KB.
- <span class="codey">spring.http.multipart.max-request-size</span> is set to 128KB, meaning total request size for a <span class="codey">multipart/form-data</span> cannot exceed 128KB.

#### Run the Application

You want a target folder to which to upload files, so you need to enhance the basic <span class="codey">UploadingFilesApplication</span> class that Spring Initializr created and add a Boot <span class="codey">CommandLineRunner</span> to delete and re-create that folder at startup. The following listing (from <span class="codey">src/main/java/com/example/uploadingfiles/UploadingFilesApplication.java</span>) shows how to do so:

<section class="level-4">
```
package com.example.uploadingfiles;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.example.uploadingfiles.storage.StorageProperties;
import com.example.uploadingfiles.storage.StorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class UploadingFilesApplication {

    public static void main(String[] args) {
        SpringApplication.run(UploadingFilesApplication.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.deleteAll();
            storageService.init();
        };
    }
}
```
</section>

<span class="codey">@SpringBootApplication</span> is a convenience annotation that adds all of the following:
- <span class="codey">@Configuration</span>: Tags the class as a source of bean definitions for the application context.
- <span class="codey">@EnableAutoConfiguration</span>: Tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings. For example, if <span class="codey">spring-webmvc</span> is on the classpath, this annotation flags the application as a web application and activates key behaviors, such as setting up a <span class="codey">DispatcherServlet</span>.
- <span class="codey">@ComponentScan</span>: Tells Spring to look for other components, configurations, and services in the <span class="codey">com/example</span> package, letting it find the controllers.

The <span class="codey">main()</span> method uses Spring Boot's <span class="codey">SpringApplication.run()</span> method to launch an application. Did you notice that there was not a single line of XML? There is no <span class="codey">web.xml</span> file, either. This web application is 100% pure Java and you did not have to deal with configuring any plumbing or infrastructure.

#### Build an executable JAR

You can run the application from the command line with Gradle or Maven. You can also build a single executable JAR file that contains all the necessary dependencies, classes, and resources and run that. Building an executable jar makes it easy to ship, version, and deploy the service as an application throughout the development lifecycle, across different environments, and so forth.

If you use Gradle, you can run the application by using <span class="codey">./gradlew bootRun</span>. Alternatively, you can build the JAR file by using <span class="codey">./gradlew build</span> and then run the JAR file, as follows:

<section class="level-4">
```
java -jar build/libs/gs-uploading-files-0.1.0.jar
```
</section>

If you use Maven, you can run the application by using <span class="codey">./mvnw spring-boot:run</span>. Alternatively, you can build the JAR file with <span class="codey">./mvnw clean package</span> and then run the JAR file, as follows:

<section class="level-4">
```
java -jar target/gs-uploading-files-0.1.0.jar
```
</section>



That runs the server-side piece that receives file uploads. Logging output is displayed. The service should be up and running within a few seconds.

With the server running, you need to open a browser and visit <span class="codey">http://localhost:8080/</span> to see the upload form. Pick a (small) file and press **Upload**. You should see the success page from the controller. If you choose a file that is too large, you will get an ugly error page.

You should then see a line resembling the following in your browser window:

"You successfully uploaded <name of your file>!"

#### Testing Your Application

There are multiple ways to test this particular feature in our application. The following listing (from <span class="codey">src/test/java/com/example/uploadingfiles/FileUploadTests.java</span>) shows one example that uses <span class="codey">MockMvc</span> so that it does not require starting the servlet container:

<section class="level-4">
```
package com.example.uploadingfiles;

import java.nio.file.Paths;
import java.util.stream.Stream;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.uploadingfiles.storage.StorageFileNotFoundException;
import com.example.uploadingfiles.storage.StorageService;

@AutoConfigureMockMvc
@SpringBootTest
public class FileUploadTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private StorageService storageService;

    @Test
    public void shouldListAllFiles() throws Exception {
        given(this.storageService.loadAll())
                .willReturn(Stream.of(Paths.get("first.txt"), Paths.get("second.txt")));

        this.mvc.perform(get("/")).andExpect(status().isOk())
                .andExpect(model().attribute("files",
                        Matchers.contains("http://localhost/files/first.txt",
                                "http://localhost/files/second.txt")));
    }

    @Test
    public void shouldSaveUploadedFile() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", "Spring Framework".getBytes());
        this.mvc.perform(multipart("/").file(multipartFile))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/"));

        then(this.storageService).should().store(multipartFile);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should404WhenMissingFile() throws Exception {
        given(this.storageService.loadAsResource("test.txt"))
                .willThrow(StorageFileNotFoundException.class);

        this.mvc.perform(get("/files/test.txt")).andExpect(status().isNotFound());
    }

}
```
</section>

In those tests, you use various mocks to set up the interactions with your controller and the <span class="codey">StorageService</span> but also with the Servlet container itself by using <span class="codey">MockMultipartFile</span>.

For an example of an integration test, see the <span class="codey">FileUploadIntegrationTests</span> class (which is in <span class="codey">src/test/java/com/example/uploadingfiles)</span>.

[TOC](#TOC)

</section> </section>

## Want Access To

<section class="level-2">
<a name="SessionFactory">

### Session Factory
</a>
</section>

<section class="level-3">
For using Hibernate 5 with Spring, little has changed since Hibernate 4: we have to use <em>LocalSessionFactoryBean</em> from the package <em>org.springframework.orm.hibernate5</em> instead of <em>org.springframework.orm.hibernate4.</em>

Like with Hibernate 4 before, we have to define beans for <em>LocalSessionFactoryBean</em>, DataSource, and <em>PlatformTransactionManager</em>, as well as some Hibernate-specific properties.

Let's create our <em>HibernateConfig</em> class to **configure Hibernate 5 with Spring**:

<section class="level-4">

```
@Configuration
@EnableTransactionManagement
public class HibernateConf {
 
    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(
          {"com.baeldung.hibernate.bootstrap.model" });
        sessionFactory.setHibernateProperties(hibernateProperties());
 
        return sessionFactory;
    }
 
    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");
 
        return dataSource;
    }
 
    @Bean
    public PlatformTransactionManager hibernateTransactionManager() {
        HibernateTransactionManager transactionManager
          = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }
 
    private final Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty(
          "hibernate.hbm2ddl.auto", "create-drop");
        hibernateProperties.setProperty(
          "hibernate.dialect", "org.hibernate.dialect.H2Dialect");
 
        return hibernateProperties;
    }
}
```
</section>

[TOC](#TOC)

</section> </section>

<section class="level-2">
<a name="DataSource">

### Data Source
</a>
</section>

<section class="level-3">
Now, if we stick with Spring Boot's automatic <em>DataSource</em> configuration and run our project in its current state, it will just work as expected.

**Spring Boot will do all the heavy infrastructure plumbing for us**. This includes creating an H2 <em>DataSource</em> implementation, which will be automatically handled by HikariCP, Apache Tomcat, or Commons DBCP, and setting up an in-memory database instance.

Additionally, we won't even need to create an <em>application.properties</em> file, as Spring Boot will provide some default database settings as well.

As we mentioned before, at times we'll need a higher level of customization, hence we'll have to configure programmatically our own <em>DataSource</em> implementation.

**The simplest way to accomplish this is by defining a DataSource factory method, and placing it within a class annotated with the <em>@Configuration</em> annotation:**

<section class="level-4">

```
@Configuration
public class DataSourceConfig {
    
    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:mem:test");
        dataSourceBuilder.username("SA");
        dataSourceBuilder.password("");
        return dataSourceBuilder.build();
    }
}
```
</section>

In this case, **we used the convenience <em>DataSourceBuilder class</em>** - a non-fluent version of Joshua Bloch's builder pattern - **to create programmatically our custom <em>DataSource</em> object.**

This approach is really nice because the builder makes it easy to configure a <em>DataSource</em> using some common properties. Additionally, it uses the underlying connection pool as well.

[TOC](#TOC)

</section>

<section class="level-2">
<a name="DataServices">

### Data Services
</a>
</section>

<section class="level-3">
As always, we can go to https://start.spring.io to create our maven project. For the example project that we are going to create, we must include the following dependencies: **Spring Data Rest, H2, JPA**, and **Lombok**

Once we have imported the project into our preferred IDE, we will have to modify the application.properties file to define certain parameters:

<section class="level-4">

```
#H2
spring.h2.console.enabled=true
spring.h2.console.path=/h2
#JPA
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=create-drop
# Datasource
spring.datasource.url=jdbc:h2:file:~/test
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver
# Data Rest
spring.data.rest.base-path: /api
```
</section>

From this file, the only entry related to the Data Rest library is the last one. Here, we specify the address where REST calls should be implemented to access the different tables that our application have. In our case, it will be accessed through the URL: http://localhost: 8080/api

The other lines configure the H2 database that we will use, as well as certain JPA properties. Of course, we will let JPA create the structure of the database through the defined entities, thanks to the sentence:  <em>spring.jpa.hibernate.ddl-auto = create-drop</em>

We will define two tables (entities), which are: **City** and **Customer**. We also define the corresponding **CustomerRepository** and **CityRepository** repositories.

The **CityEntity.java** class is the following:

<section class="level-4">

```
@Entity
@Data
@RestResource(rel="customers", path="customer")
public class CustomerEntity {
  @Id
  long id;
  @Column
  String name;
  @Column
  String address;
  @Column
  String telephone;
      @Column @JsonIgnore
  String secret;
  @OneToOne
  CityEntity city;    
}
```
</section>

The particularities of this class are the following:

1. The **@RestResource** line where with the **rel** parameter we specify how the object should be called in the JSON output. The **path** parameter indicates where the request should be made.

2. The annotation **@JsonIgnore** is applied to the <em>secret column</em>. With this label, that field will be ignored in such a way that neither will be shown in the outputs nor will it be updated, even if included in the requests.

If we did not put the tag **@RestResource**, Spring would present the resource to access the entity at **http://localhost:8080/api/customerEntities** because, by default, Spring use the name of the class, putting it in plural form for which it adds 'es'.

The repository of this entity is in CustomerRepository and contains only these lines:

<section class="level-4">
```
public interface CustomerRepository  extends CrudRepository<CustomerEntity, Long>  {
    public List<CustomerEntity> findByNameIgnoreCaseContaining(@Param("name") String name);
}
```
</section>

The function **findByNameIgnoreCaseContaining** that I have defined will allow searching the clients, ignoring case, whose name contains the string sent. Later, I will explain how to make a query through this call with **Spring Data Rest**.

You have more documentation on how to create custom queries in Spring in this entry http://www.profesor-p.com/2018/08/25/jpa-hibernate-en-spring/ (in Spanish.. What? Okay..)

The **CityEntity.java** class contains the following lines:

<section class="level-4">
```
@Entity
@Data
@RestResource(exported=false)
public class CityEntity {
  @Id 
  int id;
  @Column
  String name;
  @Column
  String province;
}
```
</section>

In this case, the **@RestResource** tag has the <em>exported</em> property set to <em>false</em> so that this entity is not treated by **Data Rest** and will not be accessible through any API.

**[For Accessing the Data Reset Api Go Here](https://dzone.com/articles/easy-access-to-data-with-spring-rest-data)**

## Application.Properties

<section class="level-2">
<a name="ChangeDB">

### Change Database Information
</a>
<a name="AppPropName">

- **Change Name of File**
</a>

<section class="level-3">

Spring boot provides command line configuration called spring.config.name using that we can change the name of  application.properties.

Here properties file name will be my-config.properties which should be available proper location, guild line for properties file location is defined here,

<section class="level-4">

```
-spring.config.name=my-config.properties is wrong configuration no need to pass extension of file.
```
  
```

> java -jar spring-boot-example-1.0-SNAPSHOT.jar --spring.config.name=my-config
```

</section></section>

<a name="AppURL">

- **Change URL**
</a>

<section class="level-3">
You can secure specific URLs of your application and make them accessible by users of a specific Role only. For example, in the application.properties file above, we have configured the Role of a default user to be a manager. Let's now configure access for a specific URL in our application, so that only user with a role "manager" can access it.

In your Spring Boot project create a new Java class and:

- Annotate it with @EnableWebSecurity annotation, 
- Make this Java class extend WebSecurityConfigurerAdapter,
- Override the configure(HttpSecurity http) method like in the example below;

<section class="level-4">
```
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity

public class WebSecurity extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception { 
        http
        .cors().and()
        .csrf().disable().authorizeRequests()
        .antMatchers("/users").hasRole("manager")
        .anyRequest().authenticated()
        .and()
        .formLogin();
    }
}
```

</section></section>

<a name="AppPW">

- **Change Password**
</a>
  
<section class="level-3">
To configure the default username, password and role, open application.properties file of your Spring Boot project and add the following three properties with the values you prefer.

<section class="level-4">
```
spring.security.user.name=sergey
spring.security.user.password=sergey
spring.security.user.roles=manager
```
</section></section>

[TOC](#TOC)

</section>
