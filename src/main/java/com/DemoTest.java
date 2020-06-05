package com;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

public class DemoTest {

    public static void main(String[] args) throws Exception {

        Menu menu = new Menu();

        List<Menu> list = new ArrayList<>();
        list.add(new Menu(1 ,"M1" ,"顶级菜单-1",    "",null,1));
        list.add(new Menu(2 ,"M2" ,"二级菜单-a",    "M1",1,2));
        list.add(new Menu(3 ,"M3" ,"二级菜单-b",    "M1",1,2));
        list.add(new Menu(4 ,"M4" ,"三级菜单-a-a",  "M2",2,3));
        list.add(new Menu(5 ,"M5" ,"三级菜单-a-b",  "M2",2,3));
        list.add(new Menu(6 ,"M6" ,"三级菜单-a-c",  "M2",2,3));
        list.add(new Menu(7 ,"M7" ,"三级菜单-b-a",  "M3",3,3));
        list.add(new Menu(8 ,"M8" ,"顶级菜单-2",    "",null,1));
        list.add(new Menu(9 ,"M9" ,"二级菜单-a-2",  "M8",8,2));
        list.add(new Menu(10,"M10","二级菜单-b-2",  "M8",8,2));
        list.add(new Menu(11,"M11","三级菜单-a-a-2","M9",9,3));
        list.add(new Menu(12,"M12","三级菜单-a-b-2","M9",9,3));
        list.add(new Menu(13,"M13","三级菜单-a-c-2","M9",9,3));
        list.add(new Menu(14,"M14","三级菜单-b-a-2","M10",10,3));

        //id
        menu.setId(null);
//        new ListTransferTree(list, menu, Menu::getId, Menu::getParentId, Menu::getChildren)
//                .tree();

        //code
//        new ListTransferTree(list, menu, Menu::getCode, Menu::getParentCode, Menu::getChildren)
//                .tree();

        menu.setCode("");
        new ListTransferTree(list, menu, Menu::getCode, Menu::getParentCode, Menu::getChildren)
                .tree();

        System.out.println(JSON.toJSONString(menu));
    }
}
