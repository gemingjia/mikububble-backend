package com.miku.bubble.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.miku.bubble.model.entity.User;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static com.miku.bubble.common.StringUtils.getRandomString;

/**
 * 用户服务测试
 *
 * @author miku
 */
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    private ExecutorService executorService = new ThreadPoolExecutor(40, 1000,
            10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    private String[] urlimg = {
            "https://thirdwx.qlogo.cn/mmopen/vi_32/J72FziaFO9rmTGsmxMv6xficW4qIZXQvZNfjIbiaVRE5kicNw8icfc0ELkUA57dlv6kUwZbP7YQfJE0iaBTL13qtQsJw/132",
            "https://img0.baidu.com/it/u=1978670007,3318649095&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg1.doubanio.com%2Fview%2Frichtext%2Flarge%2Fpublic%2Fp92818829.jpg&refer=http%3A%2F%2Fimg1.doubanio.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1684415482&t=7185ee96efd57c6d1dfc329f310f4505",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fci.xiaohongshu.com%2F2447d5ea-8b13-5bd3-78fd-ced24403c3cc%3FimageView2%2F2%2Fw%2F1080%2Fformat%2Fjpg&refer=http%3A%2F%2Fci.xiaohongshu.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1684415498&t=031d4ca61cecbb127ece1a46a4b09f86",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fsafe-img.xhscdn.com%2Fbw1%2F92fef8df-e943-4757-a22a-42dcf7539aec%3FimageView2%2F2%2Fw%2F1080%2Fformat%2Fjpg&refer=http%3A%2F%2Fsafe-img.xhscdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1684415530&t=ecaa3e54e2a9ab4a38b838786e9e0cf1"
    };

    private String[] tags = {
            "JavaScript",
            "Java",
            "Python",
            "C++",
            "C",
            "C#",
            "Rust",
            "Go",
            "PHP",
            "Ruby",
            "swift"
    };

    @Test
    void testAddUser() {
        User user = new User();
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        boolean result = userService.updateById(user);
        Assertions.assertTrue(result);
    }

    @Test
    void testDeleteUser() {
        boolean result = userService.removeById(1L);
        Assertions.assertTrue(result);
    }

    @Test
    void testGetUser() {
        User user = userService.getById(1L);
        Assertions.assertNotNull(user);
    }

    @Test
    void userRegister() {
        String userAccount = "miku";
        String userPassword = "";
        String checkPassword = "123456";
        try {
            long result = userService.userRegister(userAccount, userPassword, checkPassword);
            Assertions.assertEquals(-1, result);
            userAccount = "yu";
            result = userService.userRegister(userAccount, userPassword, checkPassword);
            Assertions.assertEquals(-1, result);
            userAccount = "miku";
            userPassword = "123456";
            result = userService.userRegister(userAccount, userPassword, checkPassword);
            Assertions.assertEquals(-1, result);
            userAccount = "yu pi";
            userPassword = "12345678";
            result = userService.userRegister(userAccount, userPassword, checkPassword);
            Assertions.assertEquals(-1, result);
            checkPassword = "123456789";
            result = userService.userRegister(userAccount, userPassword, checkPassword);
            Assertions.assertEquals(-1, result);
            userAccount = "miku";
            result = userService.userRegister(userAccount, userPassword, checkPassword);
            Assertions.assertEquals(-1, result);
        } catch (Exception e) {

        }
    }

    @Test
    public void testSearchUsersByTags() {
//        List<String> tagNameList = Arrays.asList("java", "python");
//        List<User> userList = userService.searchUsersByTags(tagNameList);
//        userList.forEach(System.out::println);
//        Assert.assertNotNull(userList);
        System.out.println("[" + "\"" +
                tags[(int) (Math.random() * 5)] + "\"" + "," + "\"" +
                tags[(int) (Math.random() * 5)] + "\"" + "," + "\"" +
                tags[(int) (Math.random() * 5)] + "\"" + "," + "\"" +
                tags[(int) (Math.random() * 5)] + "\"" +
                "]");
        for (int i = 0; i < 100; i++) {
            System.out.print((int) (Math.random() * 2));
        }


    }

    /**
     * 并发批量插入用户
     */
    @Test
    public void doConcurrencyInsertUsers() {}
//    {
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        // 分十组
//        int batchSize = 5000;
//        int j = 0;
//        List<CompletableFuture<Void>> futureList = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            List<User> userList = new ArrayList<>();
//            while (true) {
//                j++;
//                User user = new User();
//                user.setUsername(getRandomString(6));
//                user.setUserAccount(getRandomString(7));
//                user.setAvatarUrl(urlimg[(int) (Math.random() * 5)]);
//                user.setGender((int) (Math.random() * 2));
//                user.setUserPassword("12345678");
//                user.setPhone("123456798");
//                user.setEmail("123@gmail.com");
//                user.setTags("[" + "\"" +
//                        tags[(int) (Math.random() * 5)] + "\"" + "," + "\"" +
//                        tags[(int) (Math.random() * 5)] + "\"" + "," + "\"" +
//                        tags[(int) (Math.random() * 5)] + "\"" + "," + "\"" +
//                        tags[(int) (Math.random() * 5)] + "\"" +
//                        "]");
//                user.setUserStatus(0);
//                user.setUserRole(0);
//                user.setPlanetCode("1231321");
//                userList.add(user);
//                if (j % batchSize == 0) {
//                    break;
//                }
//            }
//            // 异步执行
//            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//                System.out.println("threadName: " + Thread.currentThread().getName());
//                userService.saveBatch(userList, batchSize);
//            }, executorService);
//            futureList.add(future);
//        }
//        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
//        // 20 秒 10 万条
//        stopWatch.stop();
//        System.out.println(stopWatch.getTotalTimeMillis());
//    }

    @Test
    void testRedisson(){
//        RList<String> list = redissonClient.getList("test-list");
//        list.add("miku");
//        list.add("gemingjia");
//        System.out.println(list.get(0));
//        System.out.println(list.get(1));

    }

}