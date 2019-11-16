package com.example.demo.xxno.utils;

import java.util.HashMap;
import java.util.Map;

public class Meow<T> {
    static Map<Integer,User> map = new HashMap<>();
    static {
        User user = new User();
        user.setName("meow");
        map.put(1,user);
    }

    public static <T> T getValue(Integer key,Class<T> clazz){
        User user = map.get(key);
        T cast = clazz.cast(user);
        return cast;
    }

    public static void main(String[] args) {
        Meow<User> meow = new Meow();
        User user = meow.getValue(1, User.class);
        System.out.println(user.toString());
        Animal animal = meow.getValue(2, Animal.class);
        System.out.println(animal.toString());
    }


  //todo:这种泛型方法一般用来从某个地方get后 reture 根据传入的 Class<T> 转换后返回
    
    public <T> T test(Class<T> clazz) throws Exception {
        T t = clazz.newInstance();
        //todo 要想返回 T 泛型，方法入参必须有 T 才能使用
        return t;
    }

    public <T> List<T> test2(Class<T> clazz) throws Exception {
        List<T> list = new ArrayList<>();
        T t = clazz.newInstance();
        list.add(t);
        return list;
    }

    public <T> List<T> test(Collection<T> collection) throws Exception {
        List<T> list = new ArrayList<>();
        return list;
    }

    public void test4(Object obj) {
        if (obj instanceof List) {
            List list = (List) obj;
            for (Object o : list) {
                //xxx
            }
        }
        if (obj instanceof Map) {
            //xxx
        }
        if (obj instanceof String) {
            //xxx
        }
    }

}
