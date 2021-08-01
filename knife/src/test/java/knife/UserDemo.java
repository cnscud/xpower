package knife;

import com.cnscud.xpower.knife.IService;
import com.cnscud.xpower.knife.Iknife;
import com.cnscud.xpower.knife.annotation.Table;
import com.cnscud.xpower.knife.annotation.TableField;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2019-09-23
 */
public class UserDemo {
    
    @Table("users")
    public static class User {
        @TableField(id = true)
        private Integer uid;
        private String mobile;
        @TableField("realname")
        private String realName;
        private Gender gender;

        @Override
        public String toString() {
            return uid + " mobile=" + mobile+" realName="+realName+" gender="+gender;
        }
    }

    public interface UserService extends IService<User> {

    }

    public class UserServiceImpl implements UserService{
        @Override
        public Class<User> getEntity() {
            return User.class;
        }

        @Override
        public String getBizName() {
            return null;
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {


        /*Iknife kn = new KnifeImpl();
        String bizName = "compass";
        User user = kn.queryById(bizName, User.class, 10);
        System.out.println(user);
        //
        user.realName = "饭饭";
        int num = kn.updateById(bizName, user);
        System.out.println("update count="+num);
        //
        user = kn.queryById(bizName, User.class, 10);
        System.out.println(user);
        //
        user.mobile = null;
        user.realName = null;
        kn.updateById(bizName, user);
        //
        System.out.println(kn.queryByIds(bizName, User.class, Arrays.asList(10, 1, 2)));
        //
        User listUser = new User();
        System.out.println(kn.list(bizName, listUser, "id", false, 2, 20));*/
    }

}
