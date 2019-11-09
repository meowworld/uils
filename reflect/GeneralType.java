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

}
