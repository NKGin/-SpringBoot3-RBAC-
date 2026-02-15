import com.ginwind.springrbac.SpringRBACManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = SpringRBACManager.class)
class test2 {

    @Test
    void contextLoads() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "123";
        String passwordInDb = passwordEncoder.encode(rawPassword);

        System.out.println("--- 注册阶段 ---");
        System.out.println("用户输入的明文: " + rawPassword);
        System.out.println("存入数据库的密文: " + passwordInDb);

        boolean isMatch = passwordEncoder.matches("123", passwordInDb);

        System.out.println("验证结果: " + (isMatch ? "成功 ✅" : "失败 ❌"));
    }
}
