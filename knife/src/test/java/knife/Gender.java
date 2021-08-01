package knife;

import com.cnscud.xpower.knife.IEnum;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2019-09-23
 */
public enum Gender implements IEnum<Integer>{
    Male,Femal,Unknow;

    @Override
    public Integer getValue() {
        return this.ordinal();
    }
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Gender.class;
        for(Object obj: clazz.getEnumConstants()) {
            IEnum ie = (IEnum)obj;
            System.out.println(ie.getValue()+" "+ie.getValue().getClass());
        }
        System.out.println("getComponentType="+clazz.getInterfaces());
    }
}
