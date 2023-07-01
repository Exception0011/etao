# etao

#### 指引

在本次的项目中，pojo和项目的公共接口（service包里）是放在common里面的，mapper是放在不同的生成者当中的。接口的具体实现类是写在生产者当中的。

在前后端分离项目中，我们要给前端人员后端的API（包括发送方式，url，参数，返回值类型），对后端人员来说就是控制中的参数返回值url定义好。这是前后端分离。后端不需要返回页面，所以我们将得到的信息以json数据返回给前端。

后台项目端口是8080

前台项目端口是8081，但是在这个前台项目中代理了8002，8003，8004。。。到8007，分别是代理不同的端口，通过访问后端项目来获取数据
### 父项目

安装好Zookeeper，Dubbo-Admin。192.168.31.66

创建好父项目，父工程为普通的Maven项目，子项目都是springboot项目，只要父项目导入了SpringBoot为父项目，那他的子项目都是SpringBoot的项目，定义好父项目的打包方式为pom，因为父项目是不参与打包的，定义好maven插件，定义好版本。可以在父项目中定义依赖，定好版本，在子项目中就可以直接引用）。



### 创建通用模块：shopping_common

模块和模块之间是可以相互引用的，使用Dubbo时，服务的生产者和消费者都要引入服务接口（生产者和消费者是不能互相引用的，引用的话是耦合度很高，而且这样做就不是使用Dubbo，Dubbo是使用注册中心），所以我们构建一个通用模块，在通用模块中存放Dubbo服务接口，服务的生产者和消费者都会引用该模块。除了服务接口，我们还会存放一些实体类、工具类等通用功能，每个模块都会引用通用模块。添加完之后记得加上父项目是谁，在父项目里面要引入子项目。之后加入pojo、service、util。

### 创建shopping_goods_service

引入依赖，加上父工程，在父工程里面加上子工程，再编写配置文件。生产者不能被访问，所以我们一般将端口设置为9开头

#### 根据id查广告

一般流程，pojo，mapper，接口，实现类（要想项目能扫描到mapper，要在启动类上加上@MapperScanner（“到mapper的包名”））

在本次分布式项目中，pojo写好，所以先写mapper，在将公共接口写在common中，再将实现类写回生产者里并将其注册进dubbo中使用@DubboService，



接口实现类，第二个注解是如果有事务需要回滚就可以用到


### 创建shopping_manager_api 

这是一个消费者模块，也是先写依赖（zookeeper，dubbo，springMVC），再写配置文件，不过本次有一点要注意的是，本项目依赖了common模块，common有有mybatisplus依赖，但是在我们的配置文件中没有写连接数据库的配置源，所以直接启动是会报错的，所以要在启动类上忽略配置，然后配置controlelr就行。



#### 配置Idea

在IDEA项目栏中会显示很多和开发无关的文件，我们可以通过配置 隐藏这些文件： File->Settings->Editor->File Types->Ignore Files and Folders

```
*.md;*.gitignore;.mvn;.idea;
```

#### IDEA开启Dashboard

普通的IDEA面板只能管理一个服务，而分布式项目中，服务非常 多，开启Dashboard可以更方便的管理服务。在项目文件夹idea中即在项目路径中的 .idea/workspace.xml 中添加

```
<component name="RunDashboard">
    <option name="ruleStates">
        <list>
            <RuleState>
                <option name="name" value="ConfigurationTypeDashboardGroupingRule" />
            </RuleState>
            <RuleState>
                <option name="name" value="StatusDashboardGroupingRule" />
            </RuleState>
        </list>
    </option>
    <option name="configurationTypes">
        <set>
            <option value="SpringBootApplicationConfigurationType" />
        </set>
    </option>
</component>
```

#### 设置同一返回格式

在前后端分离的项目中，为了方便前后端交互，后端往往需要给前 端返回固定的数据格式，但不同的实体类返回格式不同，所以在真 实开发中，我们将所有API接口设置返回统一的格式。

```
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResult<T> {
    // 状态码(成功:200 失败:其他)
    private Integer code;
    // 提示消息
    private String message;
    // 返回数据
    private T data;


    // 构建成功结果
    public static <T> BaseResult<T> ok() {
        return new BaseResult(CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getMessage(), null);
    }
    // 构建带有数据的成功结果
    public static <T> BaseResult<T> ok(T data) {
        return new BaseResult(CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getMessage(), data);
    }
}
```

```
@Getter
@AllArgsConstructor
public enum  CodeEnum {
    SUCCESS(200, "OK");
    // 系统异常
    SYSTEM_ERROR(500,"系统异常"),
    // 业务异常
    PARAMETER_ERROR(601,"参数异常");
    private final Integer code;
    private final String message;

}
```

将格式同一好我们的控制器一般就使用这个类作为返回值。

#### 统一异常处理

在前后端分离项目中，系统抛出异常时，不论是自定义异常还是程 序异常，都要返回给前端一段JSON数据，以便其对用户进行提示， 且JSON数据的格式和正常结果相同。

```
package com.huangkai.shopping_common.exception;

import com.huangkai.shopping_common.result.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Huangkai on 2022/9/12
 */
@Data
@AllArgsConstructor
public class BusException extends RuntimeException{
    // 状态码（成功：200，失败：其他）
    private Integer code;
    // 异常信息
    private String message;
    public BusException(CodeEnum codeEnum) {
        this.code = codeEnum.getCode();
        this.message = codeEnum.getMessage();
    }

}
```

#### 还要加上全局异常处理类

```
@RestControllerAdvice
public class GlobalExceptionHandler {
    //处理业务异常
    @ExceptionHandler(BusException.class)
    public BaseResult defaultExceptionHandler(HttpServletRequest req, HttpServletResponse resp,BusException e){
        BaseResult baseResult = new BaseResult(e.getCode(), e.getMessage(), null);
        return baseResult;
    }
    //系统异常
    @ExceptionHandler(Exception.class)
    public BaseResult defaultExceptionHandler(HttpServletRequest req, HttpServletResponse resp,Exception e){
        e.printStackTrace();
        BaseResult baseResult = new BaseResult(CodeEnum.SYSTEM_ERROR.getCode(), CodeEnum.SYSTEM_ERROR.getMessage(), null);
        return baseResult;
    }
}
```

异常一般在service里面抛出，所以要通过dubbo来传递，因此要实现Serializable接口，而且在BusException类里面还要有空参的构造方法。

在common里面的类和接口，别的项目是可以使用。但是不会将它放进别的项目的容器中，但是我们如果想在别的项目中都可以用这个统一异常处理器，因该让这个统一异常处理器自动放到别的项目的容器中，如果没有的话就会出现一个不友好的界面，就是平时报500等这些已成的页面，在通用模块创建文件resources > META-INF > spring.factories添加如下内容：

#### 在别的容器中使用全局异常处理类：

```
# 启动时自动扫描全局异常处理类
org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.huangkai.shopping_common.exception.GlobalExceptionHandler
```

### shopping_admin_service模块

后台用户也称为管理员，每个管理员能在后台进行的操作不同，所以不同的管理员有不同的权限。在项目中，权限表的设计为用户—角色多对多， 角色—权限多对多，既一个用户有多个角色，一个角色有多个权限。所以网站后台首先要拥有用户管理、角色管理、权限管理的功能。这个模块需要操作有关权限的三个表。

引入依赖，和shopping_goods_service的依赖一样，是一个生产者模块，所以核心配置文件是一样的。只需要改好端口和名字就行。在启动类上加上@MapperScan注解。

#### 对管理员的增删改查

同样的在生产者（即本模块）中添加好mapper，在公共模块里面添加好接口，再回到生产者中实例化接口@DubboService，最后只要再消费者中编写好controller@DubboReference就行。

##### 删

注意，由于一用户对应多角色，一角色对应多权限，所以当我们删除管理员的时候，需要连同角色和权限表的中间表一起删除，不然会一直增加。MybatisPlus中mapper没有提供这一方法，所以我们要自己构建。在mapper中添加方法，在resource中添加和mapper一样的包结构，添加映射文件，映射文件名和接口名相同。

```
public interface AdminMapper extends BaseMapper<Admin> {
    //删除管理员所有角色
    void deleteAdminAllRole(Long aid);
}

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.huangkai.shopping_admin_service.mapper.AdminMapper">
    <delete id="deleteAdminAllRole" parameterType="long">
        DELETE
        FROM bz_admin_role
        WHERE aid = #{aid}
    </delete>
</mapper>

 @Override
    public void delete(Long id) {
        //删除用户的所有角色
        adminMapper.deleteAdminAllRole(id);
        //删除用户
        adminMapper.deleteById(id);
    }
    
    @DeleteMapping("/delete")
    public BaseResult delete(Long aid){
        adminService.delete(aid);
        return BaseResult.ok();
    }
```

##### 查

查询某个管理员时，需要查询管理员对应的角色、权限，方便前端展示，需要自定义多表查询管理员对应的角色、权限方法（五表关联查询）：

```
//查询管理员和管理员相关信息
    Admin findById(Long id);
    
<resultMap id="adminMapper" type="com.huangkai.shopping_common.pojo.Admin">
    <id property="aid" column="aid"></id>
    <result property="username" column="username"></result>
    <collection property="roles" column="aid" ofType="com.huangkai.shopping_common.pojo.Role">
        <id property="rid" column="rid"></id>
        <result property="roleName" column="roleName"></result>
        <result property="roleDesc" column="roleDesc"></result>
        <collection property="permissions" column="rid" ofType="com.huangkai.shopping_common.pojo.Permission">
            <id property="pid" column="pid"></id>
            <result property="permissionName" column="permissionName"></result>
            <result property="url" column="url"></result>
        </collection>
    </collection>
    </resultMap>
    <select id="findById" parameterType="Long" resultMap="adminMapper">
        SELECT * FROM bz_admin
   LEFT JOIN bz_admin_role
   ON bz_admin.aid = bz_admin_role.aid
   LEFT JOIN bz_role
   ON bz_admin_role.rid = bz_role.rid
   LEFT JOIN bz_role_permission
   ON bz_role.rid = bz_role_permission.rid
   LEFT JOIN bz_permission
   ON bz_role_permission.pid = bz_permission.pid
   WHERE bz_admin.aid = #{aid}
    </select>
    
@Override
    public Admin findById(Long id) {

        return adminMapper.findById(id);
    }
    
@GetMapping("/findById")
    public BaseResult<Admin> findById(Long aid){
        Admin admin = adminService.findById(aid);
        return BaseResult.ok(admin);
    }
```

##### 使用分页插件

启动类也相当于一个配置文件，在配置文件中开启插件，写在生产者中。插件是MyBatiesPlus的。

```
@SpringBootApplication
@MapperScan("com.huangkai.shopping_admin_service.mapper")
public class ShoppingAdminServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingAdminServiceApplication.class, args);
    }
    //分页插件
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

###### 分页插件的写法

```
// 分页查询管理员
    Page<Admin> search(int page, int size);
@Override
    public Page<Admin> search(int page, int size) {
        //第二个参数是分页条件对象
        return  adminMapper.selectPage(new Page(page,size),null);

    }
 @GetMapping("/search")
    public BaseResult<Page<Admin>> search(int page, int size){
        Page<Admin> adminPage = adminService.search(page, size);
        return BaseResult.ok(adminPage);
    }    
```

##### 修改管理员角色

管理员可以对其角色进行修改，即修改 bz_admin_role 表中的记录。修改 管理员角色时，先将管理员的所有角色删除（mapper中已经有，之前写删除时添加），再将其新角色添加到 bz_admin_role 表中（需要自己编写）。

```
// 修改管理员角色
    void updateRoleToAdmin(Long aid, Long[] rids);
//给管理添加角色
    void addRoleToAdmin(@Param("aid") Long aid,@Param("rid") Long rid);
//删除管理员所有角色
    void deleteAdminAllRole(Long aid);
<insert id="addRoleToAdmin">
        INSERT INTO bz_admin_role VALUES(#{aid},#{rid});
    </insert>
@Override
    public void updateRoleToAdmin(Long aid, Long[] rids) {
        //删除管理员所有角色
        adminMapper.deleteAdminAllRole(aid);
        //给管理员添加角色
        for (Long rid:rids) {
            adminMapper.addRoleToAdmin(aid,rid);
        }
    }
@PutMapping("/updateRoleToAdmin")
    public BaseResult<Page<Admin>> updateRoleToAdmin(Long aid,Long[] rids){
        adminService.updateRoleToAdmin(aid,rids);
        return BaseResult.ok();
    }
```

#### 连接前端工程进行测试

在前后端分离项目中，作为后端开发者，一般接口测试成功后即可 认为开发完成。之后将开发好的接口告知前端开发者，前端开发者 即可访问接口，获取数据，完成展示。此处我们使用已经开发好的 前端工程，展示项目功能。

 1 解压前端项目。

 2 修改前端项目，将配置文件 vue.config.js 中后端项目的路径改为管理 员API模块的路径

 3 修改前端项目， /src/utils/axios.js ，注释掉项目的登录认证

 4 安装nodeJS

 5 在**前端项目解压路径**安装yarn

```
npm install -g yarn
```

 6 使用yarn下载前端项目依赖

```
yarn install
```

 7 使用yarn运行前端项目

```
 yarn serve
```

 8 访问前端项目 http://localhost:8080/#/user/admin

#### 对角色的增删改查

这特殊的五张表，在删除和修改的时候一定记得还要操作关联的表

先写好公共接口，再写mapper，再写接口实现类，最后写上控制器

```
public interface RoleService {
    // 新增角色
    void add(Role role);
    // 修改角色
    void update(Role role);
    // 删除角色
    void delete(Long id);
    // 根据id查询角色
    Role findById(Long id);
    // 查询所有角色
    List<Role> findAll();
    // 分页查询角色
    Page<Role> search(int page, int size);
    // 修改角色的权限
    void addPermissionToRole(Long rid, Long[] pids);
}
public interface RoleMapper extends BaseMapper<Role> {
    // 根据id查询角色，包括权限
    Role findById(Long id);
    // 删除角色的所有权限
    void deleteRoleAllPermission(Long rid);
    // 删除用户_角色表的相关数据
    void deleteRoleAllAdmin(Long rid);
    // 给角色添加权限
    void addPermissionToRole(@Param("rid") Long rid, @Param("pid")Long pid);
}
<?xml version="1.0" encoding="UTF-8"?>
        <!DOCTYPE mapper
                PUBLIC "-//mybatis.org//DTD Mapper3.0//EN"
                "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.huangkai.shopping_admin_service.mapper.RoleMapper">
    <resultMap id="roleMapper" type="com.huangkai.shopping_common.pojo.Role">
    <id property="rid" column="rid"></id>
    <result property="roleName" column="roleName"></result>
        <result property="roleDesc" column="roleDesc"></result>
        <collection property="permissions" column="rid" ofType="com.huangkai.shopping_common.pojo.Permission">
            <id property="pid" column="pid"></id>
            <result property="permissionName" column="permissionName"></result>
            <result property="url" column="url"></result>
        </collection>
    </resultMap>
    <select id="findById" parameterType="long" resultMap="roleMapper">
       SELECT *
       FROM bz_role
        LEFT JOIN bz_role_permission
        ON bz_role.rid = bz_role_permission.rid
        LEFT JOIN bz_permission
        ON bz_role_permission.pid = bz_permission.pid
       WHERE bz_role.rid = #{rid}
    </select>
    <delete id="deleteRoleAllPermission" parameterType="long">
       DELETE FROM bz_role_permission WHERE rid = #{rid}
    </delete>

    <delete id="deleteRoleAllAdmin" parameterType="long">
       DELETE FROM bz_admin_role where rid = #{rid}
    </delete>
    <insert id="addPermissionToRole">
       INSERT INTO bz_role_permission VALUES (#{rid}, #{pid});
    </insert>
</mapper>
@DubboService
@Transactional
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleMapper roleMapper;
    @Override
    public void add(Role role) {
        roleMapper.insert(role);

    }

    @Override
    public void update(Role role) {
        roleMapper.updateById(role);
    }

    @Override
    public void delete(Long id) {
        // 删除角色
        roleMapper.deleteById(id);
        // 删除角色的所有权限

        roleMapper.deleteRoleAllPermission(id);
        // 删除用户_角色中间表的相关数据
        roleMapper.deleteRoleAllAdmin(id);
    }

    @Override
    public Role findById(Long id) {
        return roleMapper.findById(id);
    }

    @Override
    public List<Role> findAll() {
        return roleMapper.selectList(null);
    }

    @Override
    public Page<Role> search(int page, int size) {
        return roleMapper.selectPage(new Page(page,size),null);
    }

    @Override
    public void addPermissionToRole(Long rid, Long[] pids) {
        // 删除角色的所有权限
        roleMapper.deleteRoleAllPermission(rid);
        // 给角色添加权限
        for (Long pid :pids) {
            roleMapper.addPermissionToRole(rid,pid);
        }
    }
}
@RestController
@RequestMapping("/role")
public class RoleController {
    @DubboReference
    private RoleService roleService;
    @PostMapping("/add")
    public BaseResult add(@RequestBody Role role) {
        roleService.add(role);
        return BaseResult.ok();
    }
    @PutMapping("/update")
    public BaseResult update(@RequestBody Role role) {
        roleService.update(role);
        return BaseResult.ok();
    }
    @DeleteMapping("/delete")
    public BaseResult delete(Long rid) {
        roleService.delete(rid);
        return BaseResult.ok();
    }
    @GetMapping("/findById")
    public BaseResult<Role> findById(Long rid) {
        Role role = roleService.findById(rid);
        return BaseResult.ok(role);
    }
    @GetMapping("/search")
    public BaseResult<Page<Role>> search(int page, int size) {
        Page<Role> page1 = roleService.search(page, size);
        return BaseResult.ok(page1);
    }

    @GetMapping("/findAll")
    public BaseResult<List<Role>> findAll()
    {
        List<Role> all = roleService.findAll();
        return BaseResult.ok(all);
    }
    @PutMapping("/updatePermissionToRole")
    public BaseResult updatePermissionToRole(Long rid, Long[] pids) {
        roleService.addPermissionToRole(rid,pids);
        return BaseResult.ok();
    }
}
```

#### 对权限的增删改查

方法同上

```
public interface PermissionService {
    // 新增权限
    void add(Permission permission);
    // 修改权限
    void update(Permission permission);
    // 删除权限
    void delete(Long id);
    // 根据id查询权限
    Permission findById(Long id);
    // 分页查询权限
    Page<Permission> search(int page, int
            size);
    // 查询所有权限
    List<Permission> findAll();
}
public interface PermissionMapper extends BaseMapper<Permission> {
    // 删除角色_权限表中的相关数据
    void deletePermissionAllRole(Long pid);
}
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper
        namespace="com.huangkai.shopping_admin_service.mapper.PermissionMapper">

    <delete id="deletePermissionAllRole" parameterType="long">
        DELETE
        FROM bz_role_permission
        WHERE pid = #{pid}
    </delete>
</mapper>
@DubboService
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private PermissionMapper permissionMapper;
    @Override
    public void add(Permission permission) {
        permissionMapper.insert(permission);
    }
    @Override
    public void update(Permission permission) {
        permissionMapper.updateById(permission);
    }
    @Override
    public void delete(Long id) {
        // 删除权限
        permissionMapper.deleteById(id);
        // 删除角色_权限表中的相关数据
        permissionMapper.deletePermissionAllRole(id);
    }
    @Override
    public Permission findById(Long id) {
        return permissionMapper.selectById(id);

    }

    @Override
    public Page<Permission> search(int page, int size) {
        return permissionMapper.selectPage(new Page(page,size),null);
    }
    @Override
    public List<Permission> findAll() {
        return permissionMapper.selectList(null);
    }
}

```

### SpringSecurity

使用Spring Security编写管理员认证和授权功能。 Spring Security在访问接口时进行认证和授权，所以Spring Security的相关代码编写在管理员API模块。 之前使用Spring Security时，登录后会配置跳转页面。但百战商城是前后端分离项目，所有认证和授权的结果，只是返回json字符串让前端去处理。所以我们要创建认证成功处理器 、 认证失败处理器 、 未登录处理器 、 权限不足处理器 、 登出成功处理器处理不同的结果，Spring Security通过实现接口编写结果处理器。

编写的配置类有这些MyAccessDeniedHandler（权限不足） MyAuthenticationEntryPoint（未登录） MyLoginFailureHandler（登陆失败） MyLoginSuccessHandler（）登录成功 MyLogoutSuccessHandler（退出成功），在继承各自方法后，在方法体里面配置为，不同的控制器返回不一样的返回值和信息：

#### 控制器

```
response.setContentType("text/json;charset=utf-8");
BaseResult result = new BaseResult(200, "注销成功", null);
response.getWriter().write(JSON.toJSONString(result));
```

在后台管理API模块编写

#### Spring Security配置类

```
@Configuration
//开启鉴权配置注解
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //自定义表单登录
        http.formLogin()
                //用户名项
                .usernameParameter("username")
                //密码项
                .passwordParameter("password")
                //登录提交路径
                .loginProcessingUrl("/admin/login")
                //登录成功处理器
                .successHandler(new MyLoginSuccessHandler())
                //登录失败处理器
                .failureHandler(new MyLoginFailureHandler());

        //权限拦截配置
        http.authorizeRequests()
                //登录页面不需要认证
                .antMatchers("/login").permitAll()
                //登录请求不需要认证
                .antMatchers("/admin/login").permitAll()
                //其余请求都要认证
                .anyRequest().authenticated();

        //退出登录配置
        http.logout()
                //注销路径
                .logoutUrl("/admin/logout")
                //退出成功处理器
                .logoutSuccessHandler(new MyLogoutSuccessHandler())
                //清楚认证数据
                .clearAuthentication(true)
                // 清除session
                .invalidateHttpSession(true);


        //异常处理
        http.exceptionHandling()
                //未登录处理器
                .authenticationEntryPoint(new MyAuthenticationEntryPoint())
                //权限不足处理器
                .accessDeniedHandler(new MyAccessDeniedHandler());


        //关闭csrf防护
        http.csrf().disable();


        //开启跨域访问
        http.cors();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
```

因为要在UserDetails里面使用权限查询所以：

#### 编写认证授权相关的服务方法

```
// 根据名字查询管理员，这里是公共接口
    Admin findByAdminName(String username);
    // 根据名字查询管理员所有权限
    List<Permission> findAllPermission(String username);
    
    
    // 根据管理员名查询权限，这里是mapper接口
    List<Permission> findAllPermission(String username);
    
    //这里是mapper配置类
    <select id="findAllPermission" resultType="com.huangkai.shopping_common.pojo.Permission" parameterType="string">
        SELECT DISTINCT bz_permission.*
   FROM
       bz_admin
           LEFT JOIN bz_admin_role
                     ON bz_admin.aid =bz_admin_role.aid
           LEFT JOIN bz_role
                     ON bz_admin_role.rid= bz_role.rid
           LEFT JOIN bz_role_permission
                     ON bz_role.rid =bz_role_permission.rid
           LEFT JOIN bz_permission
                     ON bz_role_permission.pid = bz_permission.pid
   WHERE bz_admin.username = #{username}

    </select>
    
    //这里是接口实现
    @Override
    public Admin findByAdminName(String username) {
        QueryWrapper<Admin> wrapper = new QueryWrapper();
        wrapper.eq("username", username);
        Admin admin = adminMapper.selectOne(wrapper);
        return admin;
    }

    @Override
    public List<Permission> findAllPermission(String username) {
        return adminMapper.findAllPermission(username);
    }
```

编写好生产者里面的查询方法，就可以在生产者里面使用，完成认证逻辑

#### 编写认证授权逻辑 

在后台管理API模块编写认证和授权逻辑

```
@Service
public class MyUserDetailService implements UserDetailsService {
    @DubboReference
    private AdminService adminService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //认证
        Admin admin = adminService.findByAdminName(username);
        if (admin == null){
            throw new UsernameNotFoundException("用户不存在");
        }
        //授权
        List<Permission> allPermission = adminService.findAllPermission(username);
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Permission permission:allPermission){
            grantedAuthorities.add(new SimpleGrantedAuthority(permission.getUrl()));
        }
        //封装为UserDetails对象
        UserDetails userDetails = User.withUsername(admin.getUsername()).password(admin.getPassword()).authorities(grantedAuthorities).build();
        return userDetails;
    }
}
```

#### 编写接口鉴权配置 

我们要对接口进行鉴权配置，即用户拥有权限才能访问接口。

1.开启鉴权配置注解

2.在需要鉴权的接口上方添加鉴权注解

3.使用不同权限的用户登录，查看他们是否能访问这些接口

测试时，当用户权限不足时，系统会抛出500异常，这是由于全 局异常处理器先处理了异常，使得异常没有交由 AccessDeniedHandler 。此时我们需要在管理员API模块（消费者模块）添加异常处理器，当捕获到 AccessDeniedException 异常时，直接抛出，此时异常就 会交给 AccessDeniedHandler 处理。和之前的异常处理器一样的写法，加上这个 @PreAuthorize("hasAnyAuthority('/role/search')")的意思就是如果用户有这个权限就让它使用这个控制器。

```
public class MyAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("text/json;charset=utf-8");
                BaseResult result = new BaseResult(403, "权限不足", null);

        response.getWriter().write(JSON.toJSONString(result));

    }
}
```

```
@GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('/admin/search')")
    public BaseResult<Page<Admin>> search(int page, int size){
        Page<Admin> adminPage = adminService.search(page, size);
        return BaseResult.ok(adminPage);
    }
    
@GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('/role/search')")
    public BaseResult<Page<Role>> search(int page, int size) {
        Page<Role> page1 = roleService.search(page, size);
        return BaseResult.ok(page1);
    }
```



#### 修改新增/修改管理员方法

接下来修改新增用户和修改用户方法，对密码进行加密：

```
@PostMapping("/add")
    public BaseResult add(@RequestBody Admin admin){
        String password = admin.getPassword();
        password = encoder.encode(password);
        admin.setPassword(password);
        adminService.add(admin);
        return BaseResult.ok();
    }
    @PutMapping("/update")
    public BaseResult update(@RequestBody Admin admin) {

        String password = admin.getPassword();
        if (StringUtils.hasText(password)){
        // 密码不为空加密
            password = encoder.encode(password);
            admin.setPassword(password);
        }

        adminService.update(admin);
        return BaseResult.ok();
    }
    
     @Override
    public void update(Admin admin) {
        if(!StringUtils.hasText(admin.getPassword())){
            // 查询原来的密码
            String password = adminMapper.selectById(admin.getAid()).getPassword();
            admin.setPassword(password);
        }
        adminMapper.updateById(admin);
    }
```

#### 编写获取登录管理员名方法

```
@GetMapping("/getUsername")
    public BaseResult<String> getUsername() {
        // 1.获取会话对象
        SecurityContext context = SecurityContextHolder.getContext();
        // 2.获取认证对象
        Authentication authentication = context.getAuthentication();
        // 3.获取登录用户信息
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        return BaseResult.ok(username);
    }
```

### 商品管理

#### 品牌管理

在管理商品时，除了商品名、价格、商品介绍等基本参数外。还需 要给商品添加品牌、商品类型、商品规格等参数。比如Iphone13的 品牌是苹果。商品类型属于手机通讯>手机>手机。规格有机身颜色: 星光色、版本:128G。品牌、商品类型、商品规格都需要我们在后 台进行管理。

和前面一样编写公共接口之后，编写mapper，再写实现类，最后写控制器。掌握@requestBady的使用？？？？？？

#### 商品类型管理

编写流程同上，值得注意的是。查询都是通过父id来查询，查询到的话加一，没查到就是父类型，这边是添加类型，所以只能是通过父id来判断。如果父id在0~3之外，还设计了两个异常抛出。

```
@DubboService
public class ProductTypeServiceImpl implements ProductTypeService {
    @Autowired
    private ProductTypeMapper productTypeMapper;

    @Override
    public void add(ProductType productType) {
        // 查询父类型,看当前夫类型是几级类型
        ProductType productTypeParent = productTypeMapper.selectById(productType.getParentId());

        if (productTypeParent == null){ // 如果没有父类型，则为1级类型
            productType.setLevel(1);
        }else if(productTypeParent.getLevel() < 3){ // 如果父类型级别<3，则级别为父级别+1
            productType.setLevel(productTypeParent.getLevel()+1);
        }else if(productTypeParent.getLevel() >= 3){ // 如果父类型级别>=3，则不能添加子类型
            throw new BusException(CodeEnum.INSERT_PRODUCT_TYPE_ERROR);
        }
        productTypeMapper.insert(productType);
    }

    @Override
    public void update(ProductType productType) {
        // 查询父类型
        ProductType productTypeParent = productTypeMapper.selectById(productType.getParentId());

        if (productTypeParent == null){ // 如果没有父类型，则为1级类型
            productType.setLevel(1);
        }else if(productTypeParent.getLevel() < 3){ // 如果父类型级别<3，则级别为父级别+1
            productType.setLevel(productTypeParent.getLevel()+1);
        }else if(productTypeParent.getLevel() >= 3){ // 如果父类型级别>=3，则不能添加子类型
            throw new BusException(CodeEnum.INSERT_PRODUCT_TYPE_ERROR);
        }

        productTypeMapper.updateById(productType);
    }

    @Override
    public void delete(Long id) {
        // 查询该类型的子类型
        QueryWrapper<ProductType> queryWrapper = new QueryWrapper();
        queryWrapper.eq("parentId",id);
        List<ProductType> productTypes = productTypeMapper.selectList(queryWrapper);
        // 如果该类型有子类型，删除失败
        if (productTypes != null && productTypes.size() > 0){
            throw new BusException(CodeEnum.DELETE_PRODUCT_TYPE_ERROR);
        }
        productTypeMapper.deleteById(id);
    }

    @Override
    public ProductType findById(Long id) {
        return productTypeMapper.selectById(id);
    }

    @Override
    public Page<ProductType> search(ProductType productType,int page, int size) {
        QueryWrapper<ProductType> queryWrapper = new QueryWrapper();
        if (productType != null){
            // 类型名不为空时
            if (StringUtils.hasText(productType.getName())){
                queryWrapper.like("name",productType.getName());
            }
            // 上级类型id不为空
            if (productType.getParentId()!= null){
                queryWrapper.eq("parentId",productType.getParentId());
            }
        }
        return productTypeMapper.selectPage(new Page(page,size),queryWrapper);
    }

    @Override
    public List<ProductType> findProductType(ProductType productType) {
        QueryWrapper<ProductType> queryWrapper = new QueryWrapper();
        if (productType != null){
            // 类型名不为空时
            if (StringUtils.hasText(productType.getName())){
                queryWrapper.like("name",productType.getName());
            }
            // 上级类型id不为空
            if (productType.getParentId()!= null){
                queryWrapper.eq("parentId",productType.getParentId());
            }
        }
        List<ProductType> productTypes = productTypeMapper.selectList(queryWrapper);
        return productTypes;
    }
}
```

#### 商品规格管理

写法同上，这次注意数据库是怎么关联的。

#### 安装fastDFS和Nginx

下载安装gcc编译器

```
yum install gcc-c++ perl-devel pcre-devel openssl-devel zlib-devel wget
```

下载FastDFS和FastDFS依赖包

```
# 进入根目录
cd /
# 使用rz上传FastDFS(V6.06.tar.gz)和FastDFS依赖包(V1.0.43.tar.gz)
```

安装FastDFS依赖

```
# 解压FastDFS依赖包
tar -zxvf V1.0.43.tar.gz -C /usr/local


# 进入依赖解压包
cd /usr/local/libfastcommon-1.0.43/


# 编译依赖
./make.sh 


# 安装依赖
./make.sh install
```

安装FastDFS

```
# 解压FastDFS
cd /
tar -zxvf V6.06.tar.gz -C /usr/local


# 进入FastDFS解压包
cd /usr/local/fastdfs-6.06


# 编译FastDFS
./make.sh


# 安装FastDFS
./make.sh install


# 进入etc目录
cd /etc/fdfs/


# 复制配置文件
cp client.conf.sample client.conf
cp storage.conf.sample storage.conf
cp tracker.conf.sample tracker.conf
```

启动tracker服务（跟踪服务）

```
# 创建tracker目录
mkdir -p  /data/fastdfs/tracker


# 修改配置文件
vim /etc/fdfs/tracker.conf


disabled=false         #启用配置文件
port=22122           #设置tracker的端口号
base_path=/data/fastdfs/tracker #设置tracker的数据文件和日志目录
http.server_port=8888      #设置http端口号


# 启动tracker服务
/etc/init.d/fdfs_trackerd start


# 检查tracker服务
netstat -lntup |grep fdfs

```

启动storage服务（存储服务）

```
# 创建storage目录
mkdir -p /data/fastdfs/base
mkdir -p /data/fastdfs/storage


# 修改配置文件
vim /etc/fdfs/storage.conf


disabled=false            #启用配置文件
group_name=group1           #组名，根据实际情况修改
port=23000              #storage的端口号
base_path=/data/fastdfs/base     #storage的日志目录
store_path_count=1          #存储路径个数
store_path0=/data/fastdfs/storage   #存储路径
tracker_server=192.168.0.159:22122  #tracker服务器路径
http.server_port=8888         #设置http端口号


# 启动storage服务
/etc/init.d/fdfs_storaged start


# 查看storage服务
netstat -lntup |grep fdfs

```

配置客户端连接

```
# 创建日志目录
mkdir -p /data/fastdfs/client


# 修改Client配置文件
vim /etc/fdfs/client.conf


connect_timeout=30
network_timeout=60
base_path=/data/fastdfs/client　　　　　　# 日志路径
tracker_server=192.168.0.159:22122　　　# tracker服务器路径

```

解压FastDFS的Nginx模块包

```
cd /
# 使用rz上传FastDFS的Nginx模块包（V1.22.tar.gz）
# 解压FastDFS的Nginx模块包
tar -zxvf V1.22.tar.gz -C /usr/local

```

安装Nginx依赖文件

```
yum install -y gcc gcc-c++ zlib zlib-devel openssl openssl-devel pcre pcre-devel gd-devel epel-release

```

安装Nginx

```
# 使用rz上传Nginx（nginx-1.19.2.tar.gz）


# 解压Nginx
tar -xzvf nginx-1.19.2.tar.gz -C /usr/local
# 进入Nginx安装路径
cd /usr/local/nginx-1.19.2/
# 建立Makefile文件，检查Linux系统环境以及相关的关键属性。
./configure --add-module=/usr/local/fastdfs-nginx-module-1.22/src/
# 编译Nginx
make
# 安装Nginx
make install

```

拷贝配置文件

```
cp /usr/local/fastdfs-6.06/conf/mime.types /etc/fdfs/
cp /usr/local/fastdfs-6.06/conf/http.conf /etc/fdfs/
cp /usr/local/fastdfs-nginx-module-1.22/src/mod_fastdfs.conf /etc/fdfs/

```

进行FastDFS存储配置

```
# 编辑配置文件
vim /etc/fdfs/mod_fastdfs.conf


#保存日志目录
base_path=/data/fastdfs/storage   
#tracker服务器的IP地址以及端口号
tracker_server=192.168.0.159:22122 
#文件url中是否有group名
url_have_group_name = true      
#存储路径
store_path0=/data/fastdfs/storage  
#设置组的个数
group_count = 1           
#然后在末尾添加分组信息，目前只有一个分组，就只写一个
[group1]
group_name=group1
storage_server_port=23000
store_path_count=1
store_path0=/data/fastdfs/storage

```

配置Nginx

```
# 编辑Nginx配置文件
vim /usr/local/nginx/conf/nginx.conf

server {
  listen    80;
  server_name  localhost;
  
  location ~ /group[1-3]/M00 {
    alias /data/fastdfs/storage/data;
     ngx_fastdfs_module;
   }
}
```

启动Nginx

```
# 进入sbin目录
cd /usr/local/nginx/sbin/
# 启动服务
./nginx -c /usr/local/nginx/conf/nginx.conf
```

### shopping_file_service

接下来我们创建文件服务模块，编写文件上传和文件删除的服务。  

创建名为 shopping_file_service 的SpringBoot工程，添加相关依赖。

设置该工程的父工程为 shopping 。

给 shopping 工程设置子模块

编写配置文件 application.yml，由于不需要使用数据库，所以要在启动类上加上@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})

文件上传

接下来就是常规操作，由于不用使用数据库，所以就不用配置核心配置文件里面数据库这一块，也不用加上mapper，不过要注意一点，**SpringMVC里面有MultipartFile对象，可以直接接收前端传来的对象，不过在Dubbo中不能使用，以为要在Dubbo中传输的话，对象是要可序列化的**，MultipartFile对象是MVC自带的对象，是不能序列化的，所以在使用时，接收到MultipartFile对象之后，就要将MultipartFile对象转程字节数组，这样才能使用Dubbo传输。

```
@RestController
@RequestMapping("/file")
public class FileController {
    @DubboReference
    private FileService fileService;


    /**
     * 上传文件
     * @param file 文件
     * @return 文件路径
     * @throws
     */
    @PostMapping("/uploadImage")
    public BaseResult<String> upload(MultipartFile file) throws IOException {
        // MultipartFile对象不能再服务间传递，必须转为byte数组
        byte[] bytes = file.getBytes();
        String url = fileService.uploadImage(bytes, file.getOriginalFilename());
        return BaseResult.ok(url);
    }


    /**
     * 删除文件
     * @param filePath 文件路径
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    public BaseResult delete(String filePath){
        fileService.delete(filePath);
        return BaseResult.ok();
    }
}


public interface FileService {
    /**
     * 上传文件
     * @param fileBytes 图片文件转成的字节数组
     * @param fileName 文件名
     * @return 上传后的文件访问路径
     */
    String uploadImage(byte[] fileBytes,String fileName) throws IOException;

    /**
     * 删除文件
     * @param filePath 文件路径
     */
    void delete(String filePath);
}

  
    @DubboService
public class FileServiceImpl implements FileService {
    //要使用fastDFS，要fastDFS的客户端类
    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    @Value("${fdfs.fileUrl}")
    private String fileUrl; // Nginx访问FastDFS中文件的路径
    @Override
    public String uploadImage(byte[] fileBytes, String fileName) {
        if (fileBytes.length != 0) {
            try {
                // 1.将文件字节数组转为输入流
                InputStream inputStream = new ByteArrayInputStream(fileBytes);
                // 2.获取文件的后缀名
                String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                // 3.上传文件
                StorePath storePath = fastFileStorageClient.uploadFile(inputStream, inputStream.available(), fileSuffix, null);
                // 4.返回图片路径
                String imageUrl = fileUrl + "/"+storePath.getFullPath();
                return imageUrl;
            } catch (IOException ioException) {
                throw new BusException(CodeEnum.UPLOAD_FILE_ERROR);
            }
        } else {
            throw new BusException(CodeEnum.UPLOAD_FILE_ERROR);
        }
    }

    @Override
    public void delete(String filePath) {
        fastFileStorageClient.deleteFile(filePath);
    }
}
```

```
//实现类的写法
@DubboService
public class FileServiceImpl implements FileService {
    //要使用fastDFS，要fastDFS的客户端类
    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    @Value("${fdfs.fileUrl}")
    private String fileUrl; // Nginx访问FastDFS中文件的路径
    @Override
    public String uploadImage(byte[] fileBytes, String fileName) {
        if (fileBytes.length != 0) {
            try {
                // 1.将文件字节数组转为输入流
                InputStream inputStream = new ByteArrayInputStream(fileBytes);
                // 2.获取文件的后缀名
                String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                // 3.上传文件
                StorePath storePath = fastFileStorageClient.uploadFile(inputStream, inputStream.available(), fileSuffix, null);

                // 4.返回图片路径
                String imageUrl = fileUrl + "/"+storePath.getFullPath();
                System.out.println(imageUrl);
                return imageUrl;
            } catch (IOException ioException) {
                throw new BusException(CodeEnum.UPLOAD_FILE_ERROR);
            }
        } else {
            throw new BusException(CodeEnum.UPLOAD_FILE_ERROR);
        }
    }

    @Override
    public void delete(String filePath) {
        fastFileStorageClient.deleteFile(filePath);
    }
}
```

```
# 端口号
server:
  port: 9003
# 日志格式
logging:
  pattern:
    console: '%d{HH:mm:ss.SSS}%clr(%-5level) --- [%-15thread]%cyan(%-50logger{50}):%msg%n'
dubbo:
  application:
    name: shopping_file_service # 项目名
  registry:
    address: zookeeper://192.168.31.66 #注册中心地址
    port: 2181 # 注册中心端口号
    timeout: 10000 # 注册到zk上超市时间,ms
  protocol:
    name: dubbo # dubbo使用的协议db-config:
    port: -1 # 自动分配端口
  scan:
    base-packages:
      com.huangkai.shopping_file_service.service # 包扫描

fdfs:
  so-timeout: 3000
  connect-timeout: 6000
  tracker-list: # TrackerList路径
    - 192.168.31.66:22122
  fileUrl: http://192.168.31.66 # 自定义配置，文件访问路径
```

报错：

可将图片存进fastDFS中，但是不能在浏览器中显示，那就说明是nginx的配置出了问题，需要再次回头看看配置文件中的配置。

#### 对商品的增删改查

操作同上

#### 秒杀商品

操作同上，实际上就是将商品通过增删改查放到另一个地方显示。

### 广告管理

处理对数据库中的广告数据进行增删改查，还要将数据同步到redis中，因为数据库中关于广告的数据太多，当访问量上来了之后，数据库压力太大会导致奔溃，所以我们将数据转到redis中，如果redis中有数据，就从redis中查找，如果没有就从数据库中查找，并将数据同步到redis中。

#### 安装Redis

```
安装GCC
使用rz上传Redis压缩文件
 解压并安装Redis
        return BaseResult.ok(categories);
   }
}

 yum install -y gcc
# 解压Redis
tar -zxvf redis-6.2.6.tar.gz -C /usr/local
# 进入Redis解压目录
cd /usr/local/redis-6.2.6/src/
# 编译Redis
make
# 安装Redis
make install
# 启动Redis,关闭保护状态
./redis-server --protected-mode no
```



### shopping_category_service

主要是添加广告类的操作，redis的操作也是在里面执行。

```
# 端口号
server:
  port: 9004
# 日志格式
logging:
  pattern:
    console: '%d{HH:mm:ss.SSS}%clr(%-5level) --- [%-15thread]%cyan(%-50logger{50}):%msg%n'
# 配置Mybatis-plus
mybatis-plus:
  global-config:
    db-config:
      # 表名前缀
      table-prefix: bz_
      # 主键生成策略为自增
      id-type: auto
  configuration:
    # 关闭列名自动驼峰命名映射规则
    map-underscore-to-camel-case: false
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl # 开启sql日志
spring:
  # 数据源
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql:///baizhanshopping?serverTimezone=UTC
    username: root
    password: 123456
  redis:
    host: 192.168.31.66
    port: 6379
    timeout: 30000
    jedis:
      pool:
        max-idle: 8
        max-wait: -1
        max-active: 8
        min-idle: 0
dubbo:
  application:
    name: shopping_category_service # 项目名
  registry:
    address: zookeeper://192.168.31.66 #注册中心地址
    port: 2181 # 注册中心端口号
    timeout: 10000 # 注册到zk上超市时间,ms
  protocol:
    name: dubbo # dubbo使用的协议db-config:
    port: -1 # 自动分配端口
  scan:
    base-packages:
      com.huangkai.shopping_category_service.service # 包扫描
```



```
@DubboService
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    // 对象名必须叫redisTemplate，否则由于容器中有多个RedisTemplate对象造成无法注入
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void add(Category category) {

        categoryMapper.insert(category);
        refreshRedisCategory();
    }

    @Override
    public void update(Category category) {
        categoryMapper.updateById(category);
        refreshRedisCategory();
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Category category = categoryMapper.selectById(id);
        category.setStatus(status);
        categoryMapper.updateById(category);
        refreshRedisCategory();

    }

    @Override
    public void delete(Long[] ids) {
        //将数组转化为List集合
        categoryMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public Category findById(Long id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public Page<Category> search(int page, int size) {
        return categoryMapper.selectPage(new Page(page,size),null);

        }

    @Override
    public List<Category> findAll() {
        // 1.从redis中查询启用的广告
        // 1.1 获取操作redis中list数据的对象
        ListOperations<String,Category> listOperations = redisTemplate.opsForList();
        // 1.2 从redis中获取所有启用的广告
        List<Category> categoryList = listOperations.range("categories", 0, -1);
        if (categoryList != null && categoryList.size() > 0){
            // 2.如果查到结果，直接返回
            System.out.println("从redis中查询广告");
            return categoryList;
        }else{
            // 3.如果redis中没有数据，则从数据库查询广告，并同步到redis中
            System.out.println("从mysql中查询广告");
            // 从数据库查询广告
            QueryWrapper<Category> queryWrapper = new QueryWrapper();
            queryWrapper.eq("status",1);
            List<Category> categories = categoryMapper.selectList(queryWrapper);
            // 同步到redis中
            listOperations.leftPushAll("categories",categories);
            return categories;
        }
    }

    /**
     * 更新redis中的广告数据
     */
    public void refreshRedisCategory(){
        // 从数据库查询广告
        QueryWrapper<Category> queryWrapper = new QueryWrapper();
        queryWrapper.eq("status",1);
        List<Category> categories = categoryMapper.selectList(queryWrapper);

        // 删除redis中的原有广告数据
        redisTemplate.delete("categories");
        // 将新的广告数据同步到redis中
        ListOperations<String,Category> listOperations = redisTemplate.opsForList();
        listOperations.leftPushAll("categories",categories);
    }


}


```

### shopping_category_customer_api

只要将redis的操作放在操作数据库之后就能同步到redis中。添加控制器。

### 商品搜索

#### 安装ElasticSearch和Kibana

```
#打开系统文件：
vim /etc/sysctl.conf


#配置最大可创建文件数：
vm.max_map_count=655360


#配置生效：
sysctl -p
#解压：
tar -zxvf elasticsearch-7.17.0-linux-x86_64.tar.gz


#重命名：
mv elasticsearch-7.17.0 elasticsearch


#移动文件夹：
mv elasticsearch /usr/local/
# 解压ik分词器
unzip elasticsearch-analysis-ik-7.17.0.zip -d /usr/local/elasticsearch/plugins/analysis-ik


# 解压拼音分词器
unzip elasticsearch-analysis-pinyin-7.17.0.zip -d /usr/local/elasticsearch/plugins/analysis-pinyin
#创建一个非root用户
useradd es


#es用户取得ES文件夹权限：
chown -R es:es /usr/local/elasticsearch


#切换为es用户：
su es


#启动ES服务：
ES_JAVA_OPTS="-Xms512m -Xmx512m" /usr/local/elasticsearch/bin/elasticsearch -d


#查询ES服务是否启动成功
curl 127.0.0.1:9200

```

```
tar -zxvf kibana-7.17.0-linux-x86_64.tar.gz -C /usr/local/
# 修改配置文件
vim /usr/local/kibana-7.17.0-linux-x86_64/config/kibana.yml


# 加入以下内容
# kibana主机IP
server.host: "虚拟机IP"
# Elasticsearch路径
elasticsearch.hosts: ["http://127.0.0.1:9200"]
# 给es用户设置kibana目录权限
chown -R es:es /usr/local/kibana-7.17.0-linux-x86_64/


# 切换为es用户
su es


# 启动kibana
/usr/local/kibana-7.17.0-linux-x86_64/bin/kibana

```

在common中添加elasticSearch的相关依赖,使用的是SpringDataES框架来操作的

编写相关的实体类：

1.在ES中存储的商品实体类（这里面是对应在elasticSearch里面存的数据列）

```
//对应ES中的索引叫goods，不自动创建索引
@Document(indexName = "goods",createIndex = false)
@Data
public class GoodsES implements Serializable {
    @Field
    private Long id; // 商品id
    @Field
    private String goodsName; // 商品名称
    @Field
    private String caption; // 副标题
    @Field
    private BigDecimal price; // 价格
    @Field
    private String headerPic; // 头图
    @Field
    private String brand; // 品牌名称
    @CompletionField
    private List<String> tags; // 关键字
    @Field
    private List<String> productType; // 类目名
    @Field
    private Map<String,List<String>> specification; // 规格,键为规格项,值为规格值
}

```

2.商品搜索条件实体类（里面是搜索时可能用到的字段，对应数据库里面几个表的数据）

3.商品搜索结果实体类（收索出来的数据返回，生成一个搜索面板）

在kibana使用，添加（这里就是对应ES中存储的商品实体类）

```
PUT /goods
{
  "settings": {
  //分片和副本
    "number_of_shards": 5,
    "number_of_replicas": 1,
    //自定义两个分词器
    "analysis": {
      "analyzer": {
        "ik_pinyin": {
          "tokenizer": "ik_smart",
          "filter": "pinyin_filter"
         },
        "tag_pinyin": {
          "tokenizer": "keyword",
          "filter": "pinyin_filter"
         }
       },
      "filter": {
        "pinyin_filter": {
          "type": "pinyin",
          "keep_joined_full_pinyin": true,
          "keep_original": true,
          "remove_duplicated_term": true
         }
       }
     }
   },
  "mappings": {
    "properties": {
      "id": {
        "type": "long",
        //是否创建索引
        "index": true
       },
      "goodsName": {
        "type": "text",
        "index": true,
        //分词器
        "analyzer": "ik_pinyin",
        //搜索时候使用的分词器
        "search_analyzer": "ik_smart"
       },
      "caption": {
        "type": "text",
        "index": true,
        "analyzer": "ik_pinyin",
        "search_analyzer": "ik_smart"
       },
      "tags": {
        "type": "completion",
        "analyzer": "tag_pinyin",
        "search_analyzer": "tag_pinyin"
       },
      "price": {
        "type": "double",
        "index": true
       },
      "headerPic": {
        "type": "keyword",
        "index": true
       },
      "brand": {
        "type": "keyword",
        "index": true
       },
      "productType": {
        "type": "keyword",
        "index": true
       },
      "specification":{
        "properties": {
         "specificationName":{
          "type": "keyword",
          "index": true
          },
         "specificationOption":{
          "type": "keyword",
          "index": true
          }
         } 
       }
     }
   }
}
```

### shopping_search_service

这里面没有对数据库的操作，但是有对ES的操作包括将数据同步到ES中，分词操作等配置文件如下

```
# 端口号
server:
  port: 9008
# 日志格式
logging:
  pattern:
    console: '%d{HH:mm:ss.SSS}%clr(%-5level) --- [%-15thread]%cyan(%-50logger{50}):%msg%n'

spring:
  # elasticsearch
  elasticsearch:
    uris: http://192.168.31.66:9200
dubbo:
  application:
    name: shopping_search_service # 项目名
  registry:
    address: zookeeper://192.168.31.66 #注册中心地址
    port: 2181 # 注册中心端口号
    timeout: 10000 # 注册到zk上超市时间,ms
  protocol:
    name: dubbo # dubbo使用的协议db-config:
    port: -1 # 自动分配端口
  scan:
    base-packages:
      com.huangkai.shopping_search_service.service # 包扫描
```

在通用模块编写搜索服务接口：

```
public interface SearchService {
    /**
     * 自动补齐关键字
     * @param keyword 被补齐的词
     * @return 补齐的关键词集合
     */
    List<String> autoSuggest(String keyword);

    /**
     * 搜索商品
     * @param goodsSearchParam 搜索条件
     * @return 搜索结果
     */
    GoodsSearchResult search(GoodsSearchParam goodsSearchParam);

    /**
     * 向ES同步商品数据
     * @param goodsDesc 商品详情
     */
    void syncGoodsToES(GoodsDesc goodsDesc);

    /**
     * 删除ES中的商品数据
     * @param id 商品id
     */
    void delete(Long id);
}
```

### 向ES同步数据库商品数据

 我们要将数据库的商品数据同步到ES中才能进行搜索。之前编写 Goods 实体类没有品牌名，商品类目名等数据，所以我们需要再编写 一个商品详情实体类 GoodsDesc ，并编写查询所有商品详情方法。这个实体类对应ES中的数据。（话句话说因为goods里面的东西不能直接放进goodsES中所以就有了GoodsDESC，他就是负责做一个中间商，goods中的数据放进GoodsDESC中GoodsDESC再放进GoodsES中,GoodsES最后传进ES中，并且在测试类中放入ES中。这也是为什么要在GoodService中写findAll方法，就是为了在同步的时候使用）

Java想连接ES要加上配置

```
vim /usr/local/elasticsearch/config/elasticsearch.yml、

# 单体ES环境
discovery.type: single-node
# 允许所有路径访问
network.host: 0.0.0.0
```

在商品服务中添加接口方法，编写这个方法主要是在测试类的时候将数据库的数据传到ES中，之所以在测试类中使用，是因为我们只需要将全部数据同步进去一次就行，其他在增删改的时候同步就行

```
List<GoodsDesc> findAll();
```

```
@DubboReference
    private GoodsService goodsService;
    @DubboReference
    private SearchService searchService;
    @Test
    void contextLoads() {
        List<GoodsDesc> all = goodsService.findAll();
        for (GoodsDesc goodDesc:all) {
            searchService.syncGoodsToES(goodDesc);
        }
        System.out.println(all);
    }
```

实现类，里面对于ES的操作大部分是使用原生方法，所以写法都是比较固定的。

```
@DubboService
public class SearchServiceImpl implements SearchService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private GoodsESRepository goodsESRepository;
    @Autowired
    private ElasticsearchRestTemplate template;
    /**
     * 分词fa'm
     * @param text 被分词的文本
     * @param analyzer 分词器
     * @return 分词结果
     */
    @SneakyThrows
    public List<String> analyze(String text,String analyzer){
        // 分词请求
        AnalyzeRequest request = AnalyzeRequest.withIndexAnalyzer("goods",analyzer, text);
        // 分词响应
        AnalyzeResponse response = restHighLevelClient.indices().analyze(request, RequestOptions.DEFAULT);
        // 分词结果集合
        List<String> words = new ArrayList<>();
        // 处理响应
        List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            String term = token.getTerm(); // 分出的词
            words.add(term);
        }
        return words;
    }
    //自动补全功能
    @Override
    public List<String> autoSuggest(String keyword) {
        // 1.创建补全条件
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        SuggestionBuilder suggestionBuilder = SuggestBuilders
                .completionSuggestion("tags")
                .prefix(keyword)
                //去重
                .skipDuplicates(true)
                //补10个
                .size(10);

        suggestBuilder.addSuggestion("prefix_suggestion", suggestionBuilder);

        // 2.发送请求
        SearchResponse response = template.suggest(suggestBuilder, IndexCoordinates.of("goods"));

        // 3.处理结果
        List<String> result = response
                .getSuggest()
                .getSuggestion("prefix_suggestion")
                .getEntries()
                .get(0)
                .getOptions()
                .stream()
                .map(Suggest.Suggestion.Entry.Option::getText)
                .map(Text::toString)
                .collect(Collectors.toList());

        return result;
    }

    @Override
    public GoodsSearchResult search(GoodsSearchParam goodsSearchParam) {
        return null;
    }
    //向es同步
    @Override
    public void syncGoodsToES(GoodsDesc goodsDesc) {
// 将商品详情对象转为GoodsES对象
        GoodsES goodsES = new GoodsES();
        goodsES.setId(goodsDesc.getId());
        goodsES.setGoodsName(goodsDesc.getGoodsName());
        goodsES.setCaption(goodsDesc.getCaption());
        goodsES.setPrice(goodsDesc.getPrice());
        goodsES.setHeaderPic(goodsDesc.getHeaderPic());
        goodsES.setBrand(goodsDesc.getBrand().getName());
        // 类型集合
        List<String> productType = new ArrayList();
        productType.add(goodsDesc.getProductType1().getName());
        productType.add(goodsDesc.getProductType2().getName());
        productType.add(goodsDesc.getProductType3().getName());
        goodsES.setProductType(productType);
        // 规格集合
        Map<String,List<String>> map = new HashMap();
        List<Specification> specifications = goodsDesc.getSpecifications();
        // 遍历规格
        for (Specification specification : specifications) {
            // 规格项集合
            List<SpecificationOption> options = specification.getSpecificationOptions();
            // 规格项名集合
            List<String> optionStrList = new ArrayList();
            for (SpecificationOption option : options) {
                optionStrList.add(option.getOptionName());
            }
            map.put(specification.getSpecName(),optionStrList);
        }
        goodsES.setSpecification(map);
        // 关键字
        List<String> tags = new ArrayList();
        tags.add(goodsDesc.getBrand().getName()); //品牌名是关键字
        tags.addAll(analyze(goodsDesc.getGoodsName(),"ik_smart"));//商品名分词后为关键词
        tags.addAll(analyze(goodsDesc.getCaption(),"ik_smart"));//副标题分词后为关键词
        goodsES.setTags(tags);
        goodsESRepository.save(goodsES);
    }

    @Override
    public void delete(Long id) {

    }
}
```

#### 商品搜索

在SearchServiceImpl中





### shopping_search_customer_api

暴露出来，让前端可以得到数据。控制器

```
/**
 * 用户商品搜索
 */
@RestController
@RequestMapping("/user/goodsSearch")
public class GoodsSearchController<GoodsESService> {
    @DubboReference
    private SearchService searchService;


    /**
     * 自动补齐关键字
     * @param keyword 被补齐的词
     * @return 补齐的关联词集合
     */
    @GetMapping("/autoSuggest")
    public BaseResult<List<String>> autoSuggest(String keyword){
        List<String> keywords = searchService.autoSuggest(keyword);
        return BaseResult.ok(keywords);
    }
}

```

#### 商品搜索功能

搜索功能的编写比较复杂，首先搜索条件繁多，有关键字、价格、 品牌、规格等。其次返回的结果除了搜索到的商品，还要返回搜索 面板，包含关键字对应的品牌、品类、规格等，还要将搜索条件回 显回去，供前端操作。接下来我们编写商品的搜索功能。

因为SearchServiceImpl会依赖GoodsService所以我们使用队列来解决这个问题，降低两个模块的耦合度。这样做的好处是降低耦合度，而且能加快请求速度提升用户体验，因为没有解耦之前，模块之间相互依赖，在进行添加商品的时候，两个模块要同时完成才能完成添加操作，解耦之后我们只需要等一个模块完成就行。

```
@DubboService
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private GoodsESRepository goodsESRepository;
    @Autowired
    private ElasticsearchRestTemplate template;
    // 监听同步商品队列
    @RabbitListener(queues = "sync_goods_queue")
    public void listenSyncQueue(GoodsDesc goodsDesc){
        syncGoodsToES(goodsDesc);
    }

    // 监听删除商品队列
    @RabbitListener(queues = "del_goods_queue")
    public void listenDelQueue(Long id){
        delete(id);
    }

    /**
     * 分词
     * @param text 被分词的文本
     * @param analyzer 分词器
     * @return 分词结果
     */
    @SneakyThrows
    public List<String> analyze(String text,String analyzer){
        // 分词请求
        AnalyzeRequest request = AnalyzeRequest.withIndexAnalyzer("goods",analyzer, text);
        // 分词响应
        AnalyzeResponse response = restHighLevelClient.indices().analyze(request, RequestOptions.DEFAULT);
        // 分词结果集合
        List<String> words = new ArrayList<>();
        // 处理响应
        List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            String term = token.getTerm(); // 分出的词
            words.add(term);
        }
        return words;
    }
    //自动补全功能
    @Override
    public List<String> autoSuggest(String keyword) {
        // 1.创建补全条件
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        SuggestionBuilder suggestionBuilder = SuggestBuilders
                .completionSuggestion("tags")
                .prefix(keyword)
                //去重
                .skipDuplicates(true)
                //补10个
                .size(10);

        suggestBuilder.addSuggestion("prefix_suggestion", suggestionBuilder);

        // 2.发送请求
        SearchResponse response = template.suggest(suggestBuilder, IndexCoordinates.of("goods"));

        // 3.处理结果
        List<String> result = response
                .getSuggest()
                .getSuggestion("prefix_suggestion")
                .getEntries()
                .get(0)
                .getOptions()
                .stream()
                .map(Suggest.Suggestion.Entry.Option::getText)
                .map(Text::toString)
                .collect(Collectors.toList());

        return result;
    }

    @Override
    public GoodsSearchResult search(GoodsSearchParam goodsSearchParam) {
        //构造Es搜索条件
        NativeSearchQuery query = buildQuery(goodsSearchParam);
        //搜索（这里并不是使用MybatisPlus搜索，而是使用Es，框架是SpringDataElasticSearch，MyBatisPlus是可自动将所搜对象封装成Page对象的，es那不行）
        SearchHits<GoodsES> search = template.search(query, GoodsES.class);
        //所以我们将查询结果封装为Page对象
        // 1 将SearchHits转为List
        List<GoodsES> content = new ArrayList();
        for (SearchHit<GoodsES> goodsESSearchHit : search) {
            GoodsES goodsES = goodsESSearchHit.getContent();
            content.add(goodsES);
        }
        // 2 将List转为MP的Page对象
        Page<GoodsES> page = new Page();
        page.setCurrent(goodsSearchParam.getPage()) // 当前页
                .setSize(goodsSearchParam.getSize()) // 每页条数
                .setTotal(search.getTotalHits()) // 总条数
                .setRecords(content); // 结果集
        //封装结果对象，包含查询结果，查询参数，查询面板
        // 1 查询结果
        GoodsSearchResult result = new GoodsSearchResult();
        result.setGoodsPage(page);
        // 2 查询参数
        result.setGoodsSearchParam(goodsSearchParam);
        // 3 查询面板
        buildSearchPanel(goodsSearchParam,result);
        return result;
    }

    /* 构造搜索条件
     * @param goodsSearchParam:查询条件对象
     * @return NativeSearchQuery
     * @author Admin
     * @description TODO
     * @date 2022/9/21 14:32
     */
    public NativeSearchQuery buildQuery(GoodsSearchParam goodsSearchParam){
        //创建复杂查询条件对象
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        //如果查询条件有关键词，关键词可以配置商品名、副标题、品牌字段；否者查询所有
        if (!StringUtils.hasText(goodsSearchParam.getKeyword())){
            MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
            builder.must(matchAllQueryBuilder);
        }else {
            String keyword = goodsSearchParam.getKeyword();
            //多字段查询，multiMatchQuery先分词再查询
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "goodsName", "caption", "brand");
            builder.must(multiMatchQueryBuilder);
        }
        //如果查询条件有品牌，则精准匹配品牌
        String brand = goodsSearchParam.getBrand();
        if (StringUtils.hasText(brand)){
            //termQuery不会对传入的词进行分词
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("brand", brand);
            builder.must(termQueryBuilder);
        }
        //如果查询条件有价格则匹配价格
        Double highPrice = goodsSearchParam.getHighPrice();
        Double lowPrice = goodsSearchParam.getLowPrice();
        if (highPrice != null && highPrice != 0){
            RangeQueryBuilder lte = QueryBuilders.rangeQuery("price").lte(highPrice);
            builder.must(lte);
        }
        if (lowPrice != null && lowPrice != 0){
            RangeQueryBuilder gte = QueryBuilders.rangeQuery("price").gte(lowPrice);
            builder.must(gte);
        }
        //如果查询条件有规格项，则精准匹配规格项
        Map<String, String> specificationOptions = goodsSearchParam.getSpecificationOption();
        if (specificationOptions != null && specificationOptions.size() > 0){
            Set<Map.Entry<String, String>> entries = specificationOptions.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (StringUtils.hasText(key)){
                    //如果字段是specification里面的一个字段，那么就要这样写，es的要求
                    TermQueryBuilder termQuery = QueryBuilders.termQuery("specification." + key + ".keyword", value);
                    builder.must(termQuery);
                }
            }
        }
        //添加分页条件，MyBatis是1开始，es从0开始
        PageRequest pageable = PageRequest.of(goodsSearchParam.getPage() - 1, goodsSearchParam.getSize());
        // 查询构造器，添加条件和分页
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(builder).withPageable(pageable);
        //如果查询条件有排序，则添加排序条件
        String sortFiled = goodsSearchParam.getSortFiled();
        String sort = goodsSearchParam.getSort();
        SortBuilder sortBuilder = null;
        if (StringUtils.hasText(sort) && StringUtils.hasText(sortFiled)){
            // 新品的正序是id的倒序，越新id越大
            if (sortFiled.equals("NEW")){
                sortBuilder = SortBuilders.fieldSort("id");
                if (sort.equals("ASC")){
                    sortBuilder.order(SortOrder.DESC);
                }
                if (sort.equals("DESC")){
                    sortBuilder.order(SortOrder.ASC);
                }
            }
            if (sortFiled.equals("PRICE")){
                sortBuilder = SortBuilders.fieldSort("price");
                if (sort.equals("ASC")){
                    sortBuilder.order(SortOrder.ASC);
                }
                if (sort.equals("DESC")){
                    sortBuilder.order(SortOrder.DESC);
                }
            }
            nativeSearchQueryBuilder.withSorts(sortBuilder);
        }
        //返回查询条件对象
        NativeSearchQuery query = nativeSearchQueryBuilder.build();
        return query;
    }
    /**
     * 封装查询面板，即根据查询条件，找到查询结果关联度前20名的商品进行封装
     * @param goodsSearchParam
     * @param goodsSearchResult
     */
    public void buildSearchPanel(GoodsSearchParam goodsSearchParam,GoodsSearchResult goodsSearchResult){
        // 1.构造搜索条件
        goodsSearchParam.setPage(1);
        goodsSearchParam.setSize(20);
        goodsSearchParam.setSort(null);
        goodsSearchParam.setSortFiled(null);
        NativeSearchQuery query = buildQuery(goodsSearchParam);
        // 2.搜索
        SearchHits<GoodsES> search = template.search(query, GoodsES.class);
        // 3.将结果封装为List对象
        List<GoodsES> content = new ArrayList();
        for (SearchHit<GoodsES> goodsESSearchHit : search) {
            GoodsES goodsES = goodsESSearchHit.getContent();
            content.add(goodsES);
        }
        // 4.遍历集合，封装查询面板
        // 商品相关的品牌列表
        Set<String> brands = new HashSet();
        // 商品相关的类型列表
        Set<String> productTypes = new HashSet();
        // 商品相关的规格列表
        Map<String, Set<String>> specifications = new HashMap();
        for (GoodsES goodsES : content) {
            // 获取品牌
            brands.add(goodsES.getBrand());
            // 获取类型
            List<String> productType = goodsES.getProductType();
            productTypes.addAll(productType);
            // 获取规格
            Map<String, List<String>> specification = goodsES.getSpecification();
            Set<Map.Entry<String, List<String>>> entries = specification.entrySet();
            for (Map.Entry<String, List<String>> entry : entries) {
                // 规格名
                String key = entry.getKey();
                // 规格值
                List<String> value = entry.getValue();
                // 如果没有遍历出该规格，新增键值对，如果已经遍历出该规格，则向规格中添加规格项
                if (!specifications.containsKey(key)){
                    specifications.put(key,new HashSet(value));
                }else{
                    specifications.get(key).addAll(value);
                }
            }
        }
        goodsSearchResult.setBrands(brands);
        goodsSearchResult.setProductType(productTypes);
        goodsSearchResult.setSpecifications(specifications);
    }

    //向es同步
    @Override
    public void syncGoodsToES(GoodsDesc goodsDesc) {
// 将商品详情对象转为GoodsES对象
        GoodsES goodsES = new GoodsES();
        goodsES.setId(goodsDesc.getId());
        goodsES.setGoodsName(goodsDesc.getGoodsName());
        goodsES.setCaption(goodsDesc.getCaption());
        goodsES.setPrice(goodsDesc.getPrice());
        goodsES.setHeaderPic(goodsDesc.getHeaderPic());
        goodsES.setBrand(goodsDesc.getBrand().getName());
        // 类型集合
        List<String> productType = new ArrayList();
        productType.add(goodsDesc.getProductType1().getName());
        productType.add(goodsDesc.getProductType2().getName());
        productType.add(goodsDesc.getProductType3().getName());
        goodsES.setProductType(productType);
        // 规格集合
        Map<String,List<String>> map = new HashMap();
        List<Specification> specifications = goodsDesc.getSpecifications();
        // 遍历规格
        for (Specification specification : specifications) {
            // 规格项集合
            List<SpecificationOption> options = specification.getSpecificationOptions();
            // 规格项名集合
            List<String> optionStrList = new ArrayList();
            for (SpecificationOption option : options) {
                optionStrList.add(option.getOptionName());
            }
            map.put(specification.getSpecName(),optionStrList);
        }
        goodsES.setSpecification(map);
        // 关键字
        List<String> tags = new ArrayList();
        tags.add(goodsDesc.getBrand().getName()); //品牌名是关键字
        tags.addAll(analyze(goodsDesc.getGoodsName(),"ik_smart"));//商品名分词后为关键词
        tags.addAll(analyze(goodsDesc.getCaption(),"ik_smart"));//副标题分词后为关键词
        goodsES.setTags(tags);
        goodsESRepository.save(goodsES);
    }

    @Override
    public void delete(Long id) {
        goodsESRepository.deleteById(id);
    }

}
```

#### 编写根据id查询商品详情功能

用户查询到商品列表后，再查询商品详情需要从数据库查询，因为 ES中的数据不全。所以我们需要在商品服务中添加根据id查询商品 详情功能：就是在这里两个模块发生耦合，查询是在GoodsService但是会在SearchService里面调用

#### 管理员操作商品后同步到ES中

管理员在数据库增删改商品后（GS中），需要将商品数据同步到ES中（SS中），这样 用户才能在第一时间搜索到最新数据。

#### 安装RabbitMQ

```
安装Erlang所需的依赖

yum install -y epel-release
添加存储库条目

wget https://packages.erlang-solutions.com/erlang-solutions-1.0-1.noarch.rpm 

rpm -Uvh erlang-solutions-1.0-1.noarch.rpm
安装Erlang

yum install erlang-24.2.1
查看Erlang是否安装成功

erl -version
RabbitMQ是通过主机名进行访问的，必须给虚拟机添加主机名

# 修改文件
vim /etc/sysconfig/network
# 添加如下内容
NETWORKING=yes
HOSTNAME=itbaizhan

# 修改文件
vim /etc/hosts
# 添加如下内容
服务器ip itbaizhan
使用rz命令上传RabbitMQ压缩文件

安装RabbitMQ

# 解压RabbitMQ
tar xf rabbitmq-server-generic-unix-3.9.13.tar.xz

# 重命名：
mv rabbitmq_server-3.9.13 rabbitmq

# 移动文件夹：
mv rabbitmq /usr/local/
配置环境变量

# 编辑/etc/profile文件
vim /etc/profile

#添加如下内容
export PATH=$PATH:/usr/local/rabbitmq/sbin

# 运行文件，让修改内容生效
source /etc/profile
配置允许使用guest远程访问

# 创建配置文件夹
mkdir -p /usr/local/rabbitmq/etc/rabbitmq

# 创建配置文件
vim /usr/local/rabbitmq/etc/rabbitmq/rabbitmq.conf

# 添加如下内容
loopback_users=none
开启管控台插件

rabbitmq-plugins enable rabbitmq_management
后台运行

#启动rabbitmq
rabbitmq-server -detached

#停止rabbitmq
rabbitmqctl stop
通过管控台访问RabbitMQ

路径：http://ip地址:15672，用户名：guest，密码：guest
```

配置文件，在GoodsService和SearchService里面都要添加，GS发送，SS监听

```
spring:
  # elasticsearch
  elasticsearch:
    uris: http://192.168.31.66:9200
  # rabbitmq
  rabbitmq:
    host: 192.168.31.66
    port: 5672
    username: guest
    password: guest
    virtual-host: /
```

在GoodsService中添加RabbitMQ的配置

```
@Configuration
public class RabbitConfig {
    // 交换机
    private final String GOODS_EXCHANGE = "goods_exchange";
    // 同步商品数据队列
    private final String SYNC_GOODS_QUEUE = "sync_goods_queue";
    // 删除商品数据队列
    private final String DEL_GOODS_QUEUE = "del_goods_queue";


    // 创建交换机
    @Bean(GOODS_EXCHANGE)
    public Exchange getExchange() {
        return ExchangeBuilder
                .topicExchange(GOODS_EXCHANGE) // 交换机类型
                .durable(true) // 是否持久化
                .build();
    }


    // 创建队列
    @Bean(SYNC_GOODS_QUEUE)
    public Queue getQueue1() {
        return new Queue(SYNC_GOODS_QUEUE); // 队列名
    }
    @Bean(DEL_GOODS_QUEUE)
    public Queue getQueue2() {
        return new Queue(DEL_GOODS_QUEUE); // 队列名
    }


    // 交换机绑定队列
    @Bean
    public Binding bindQueue1(@Qualifier(GOODS_EXCHANGE) Exchange exchange, @Qualifier(SYNC_GOODS_QUEUE) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("#.sync_goods.#")
                .noargs();
    }

    @Bean
    public Binding bindQueue2(@Qualifier(GOODS_EXCHANGE) Exchange exchange, @Qualifier(DEL_GOODS_QUEUE) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("#.del_goods.#")
                .noargs();
    }
}
@Configuration
public class RabbitConfig {
    // 交换机
    private final String GOODS_EXCHANGE = "goods_exchange";
    // 同步商品数据队列
    private final String SYNC_GOODS_QUEUE = "sync_goods_queue";
    // 删除商品数据队列
    private final String DEL_GOODS_QUEUE = "del_goods_queue";


    // 创建交换机
    @Bean(GOODS_EXCHANGE)
    public Exchange getExchange() {
        return ExchangeBuilder
                .topicExchange(GOODS_EXCHANGE) // 交换机类型
                .durable(true) // 是否持久化
                .build();
    }


    // 创建队列
    @Bean(SYNC_GOODS_QUEUE)
    public Queue getQueue1() {
        return new Queue(SYNC_GOODS_QUEUE); // 队列名
    }
    @Bean(DEL_GOODS_QUEUE)
    public Queue getQueue2() {
        return new Queue(DEL_GOODS_QUEUE); // 队列名
    }


    // 交换机绑定队列
    @Bean
    public Binding bindQueue1(@Qualifier(GOODS_EXCHANGE) Exchange exchange, @Qualifier(SYNC_GOODS_QUEUE) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("#.sync_goods.#")
                .noargs();
    }

    @Bean
    public Binding bindQueue2(@Qualifier(GOODS_EXCHANGE) Exchange exchange, @Qualifier(DEL_GOODS_QUEUE) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("#.del_goods.#")
                .noargs();
    }
}
```

### 用户管理

#### 添加shopping_user_service

#### 添加shopping_user_customer_api 

#### 添加shopping_message_service

在阿里云购买短信，里面有接口文档，我们只需要拿过来使用就行

```
/**
 * @author Huangkai on 2022/9/22
 */
@DubboService
public class MessageServiceImpl implements MessageService {
    @Value("${message.accessKeyId}")
    private String accessKeyId;
    @Value("${message.accessKeySecret}")
    private String accessKeySecret;

    /**
     * 使用AK&SK初始化账号Client
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    public com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new com.aliyun.dysmsapi20170525.Client(config);
    }


    @SneakyThrows
    @Override
    public BaseResult sendMessage(String phoneNumber, String code) {
        com.aliyun.dysmsapi20170525.Client client = createClient(accessKeyId, accessKeySecret);
        com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                .setSignName("阿里云短信测试")
                .setTemplateCode("SMS_154950909")
                .setPhoneNumbers(phoneNumber)
                .setTemplateParam("{\"code\":\"" + code + "\"}");
        RuntimeOptions runtime = new RuntimeOptions();
        // 复制代码运行请自行打印 API 的返回值
        SendSmsResponse sendSmsResponse = client.sendSmsWithOptions(sendSmsRequest, runtime);
        SendSmsResponseBody body = sendSmsResponse.getBody();
        if ("OK".equals(body.getCode())){
            return new BaseResult(200,body.getCode(),body.getMessage());
        }else{
            return new BaseResult(500,body.getCode(),body.getMessage());
        }
    }
}
```

在common中编写接口，再实现

```
public interface ShoppingUserService {
    // 注册时向redis保存手机号+验证码
    void saveRegisterCheckCode(String phone,String checkCode);
    // 注册时验证手机号
    void registerCheckCode(String phone,String checkCode);
    // 用户注册
    void register(ShoppingUser shoppingUser);
    // 用户名密码登录
    String loginPassword(String username,String password);
    // 登录时向redis保存手机号+验证码
    void saveLoginCheckCode(String phone,String checkCode);
    // 手机号验证码登录
    String loginCheckCode(String phone, String checkCode);
    // 获取登录用户名
    String getName(String token);
    // 获取登录用户
    ShoppingUser getLoginUser(String token);
}
```

```
@DubboService
public class ShoppingUserServiceImpl implements ShoppingUserService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ShoppingUserMapper shoppingUserMapper;
    /*将用户注册手机号+验证码保存到redis中。键为手机号值为验证码
     * @param phone:
     * @param checkCode:
     * @return void
     * @author Admin
     * @description TODO
     * @date 2022/9/22 16:27
     */
    @Override
    public void saveRegisterCheckCode(String phone, String checkCode) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // redis键为手机号，值为验证码，过期时间5分钟
        valueOperations.set("registerCode:" + phone, checkCode, 300, TimeUnit.SECONDS);
    }

    @Override
    public void registerCheckCode(String phone, String checkCode) {
        // 验证验证码
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object checkCodeRedis = valueOperations.get("registerCode:" + phone);
        if (!checkCode.equals(checkCodeRedis)) {
            throw new BusException(CodeEnum.REGISTER_CODE_ERROR);
        }
    }

    @Override
    public void register(ShoppingUser shoppingUser) {
// 1.验证手机号是否存在
        String phone = shoppingUser.getPhone();
        QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper();
        queryWrapper.eq("phone", phone);
        List<ShoppingUser> shoppingUsers = shoppingUserMapper.selectList(queryWrapper);
        if (shoppingUsers != null && shoppingUsers.size() > 0) {
            throw new BusException(CodeEnum.REGISTER_REPEAT_PHONE_ERROR);
        }

        // 2.验证用户名是否存在
        String username = shoppingUser.getUsername();
        QueryWrapper<ShoppingUser> queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("username", username);
        List<ShoppingUser> shoppingUsers1 = shoppingUserMapper.selectList(queryWrapper1);
        if (shoppingUsers1 != null && shoppingUsers1.size() > 0) {
            throw new BusException(CodeEnum.REGISTER_REPEAT_NAME_ERROR);
        }

        // 3.新增用户
        shoppingUser.setStatus("Y");
        shoppingUser.setPassword(Md5Util.encode(shoppingUser.getPassword()));
        shoppingUserMapper.insert(shoppingUser);
    }

    @Override
    public String loginPassword(String username, String password) {
        QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        ShoppingUser shoppingUser = shoppingUserMapper.selectOne(queryWrapper);
        // 验证用户名
        if (shoppingUser == null) {
            throw new BusException(CodeEnum.LOGIN_NAME_PASSWORD_ERROR);
        }
        // 验证密码
        boolean verify = Md5Util.verify(password, shoppingUser.getPassword());
        if (!verify) {
            throw new BusException(CodeEnum.LOGIN_NAME_PASSWORD_ERROR);
        }
        // 3.生成JWT令牌，返回令牌
        String sign = JWTUtil.sign(shoppingUser);
        return sign;
    }
    // 保存登录验证码到redis
    @Override
    public void saveLoginCheckCode(String phone, String checkCode) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // redis键为手机号，值为验证码，过期时间5分钟
        valueOperations.set("loginCode:" + phone, checkCode, 300, TimeUnit.SECONDS);
    }
    // 验证登录验证码
    @Override
    public String loginCheckCode(String phone, String checkCode) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object checkCodeRedis = valueOperations.get("loginCode:" + phone);
        if (!checkCode.equals(checkCodeRedis)) {
            throw new BusException(CodeEnum.LOGIN_CODE_ERROR);
        }
        // 登录成功，查询用户
        QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper();
        queryWrapper.eq("phone", phone);
        ShoppingUser shoppingUser = shoppingUserMapper.selectOne(queryWrapper);
        // 生成JWT令牌，返回令牌
        String sign = JWTUtil.sign(shoppingUser);
        return sign;
    }

    @Override
    public String getName(String token) {
        String name = JWTUtil.verify(token);
        return name;
    }

    @Override
    public ShoppingUser getLoginUser(String token) {
        String username = JWTUtil.verify(token);
        QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        ShoppingUser shoppingUser = shoppingUserMapper.selectOne(queryWrapper);
        return shoppingUser;
    }

}
```

编写控制器

```
/**
 * 商城用户
 */
@RestController
@RequestMapping("/user/shoppingUser")
public class ShoppingUserController {
    @DubboReference
    private ShoppingUserService shoppingUserService;
    @DubboReference
    private MessageService messageService;
    /**
     * 发送注册短信
     * @param phone 注册手机号
     * @return 操作结果
     */
    @GetMapping("/sendMessage")
    public BaseResult sendMessage(String phone){
        // 1.生成随机四位数
        String checkCode = RandomUtil.buildCheckCode(4);
        // 2.发送短信
        BaseResult result = messageService.sendMessage(phone, checkCode);
        // 3.发送成功，将验证码保存到redis中,发送失败，返回发送结果
        if (200 == result.getCode()) {
            shoppingUserService.saveRegisterCheckCode(phone, checkCode);
            return BaseResult.ok();
        } else {
            return result;
        }
    }
    /**
     * 验证用户注册验证码
     * @param phone 手机号
     * @param checkCode 验证码
     * @return 200验证成功，605验证码不正确
     */
    @GetMapping("/registerCheckCode")
    public BaseResult register(String phone,String checkCode){
        shoppingUserService.registerCheckCode(phone,checkCode);
        return BaseResult.ok();
    }
    /**
     * 用户注册
     * @param shoppingUser 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public BaseResult register(@RequestBody ShoppingUser shoppingUser){
        shoppingUserService.register(shoppingUser);
        return BaseResult.ok();
    }
    /**
     * 用户名密码登录
     * @param shoppingUser 用户对象
     * @return 登录结果
     */
    @PostMapping("/loginPassword")
    public BaseResult loginPassword(@RequestBody ShoppingUser shoppingUser){
        String sign = shoppingUserService.loginPassword(shoppingUser.getUsername(), shoppingUser.getPassword());
        return BaseResult.ok(sign);
    }
    /**
     * 发送登录短信验证码
     *
     * @param phone 手机号
     * @return 操作结果
     */
    @GetMapping("/sendLoginCheckCode")
    public BaseResult sendLoginCheckCode(String phone) {
        // 1.生成随机四位数
        String checkCode = RandomUtil.buildCheckCode(4);
        // 2.发送短信
        BaseResult result = messageService.sendMessage(phone, checkCode);
        // 3.发送成功，将验证码保存到redis中,发送失败，返回发送结果
        if (200 == result.getCode()) {
            shoppingUserService.saveLoginCheckCode(phone, checkCode);
            return BaseResult.ok();
        } else {
            return result;
        }
    }
    /**
     * 手机号验证码登录
     * @param phone 手机号
     * @param checkCode 验证码
     * @return 登录结果
     */
    @PostMapping("/loginCheckCode")
    public BaseResult loginCheckCode(String phone, String checkCode){
        String sign = shoppingUserService.loginCheckCode(phone, checkCode);
        return BaseResult.ok(sign);
    }

    /**
     * 获取登录的用户名
     * @param token 令牌
     * @return 用户名
     */
    @GetMapping("/getName")
    public BaseResult<String> getName(@RequestHeader("token") String token){
        String name = shoppingUserService.getName(token);
        return BaseResult.ok(name);
    }

}
```

#### 单点登录

生成令牌，给前端后，就可以让前端去访问其他模块的时候带上这个令牌就可以访问，在需要令牌才能访问的模块加上配置类就行。

### 购物车模块



再添加一个rabbitmq的效果

用户下单之后将数据添加到两个表，一个是订单表，一个是商品表，商品表在redis里就是在购物车，在数据库中就是已支付的商品，付款之后再在数据库中添加支付的数据例如订单时什么支付方式支付的。














