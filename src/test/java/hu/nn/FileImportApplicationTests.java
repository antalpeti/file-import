package hu.nn;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FileImportApplicationTests {

    @Test
    void contextLoads() {
        String base = "base";
        Assertions.assertEquals("base", base);
    }

}
