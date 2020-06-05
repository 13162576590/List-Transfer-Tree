# List-Transfer-Tree
List转树形结构


由于项目需要，需要把list转为树形结构，一般list转为树形结构时，需要关心prentCode,及子节点对应的父节点，并且父节点对应的子节点应该放在哪一个属性。及节点的结构层次。

在实现前，先了解下lambda 方法引用获取字段，该方法优势是解决硬编码问题，同时符合java编码规范，不会直接写死，并且当字段名有改变，不能编译通过，及时发现错误。

博客涉及断言使用，主要是用于基本参数的校验，不做详细介绍。

一、lambda 方法引用获取字段

jdk8提供关键字::，用于类、方法及属性的应用，通过下面代码及可拿到相应的属性名。

	   //直接通过类应用方法，解决硬编码问题
	   //list 列表；
	   //menu对象节点，转化树形结构在其对应子节点 ；
	   //Menu::getCode表示跟节点值，可为空；
	   //Menu::getParentCode表示其父节点
	   //Menu::getChildren表示子节点
	    new ListTransferTree(list, menu, Menu::getCode, Menu::getParentCode, Menu::getChildren)
	    
	    //构造方面
	    public <T> ListTransferTree(List<T> list, T t, TypeFunction<T> parent, TypeFunction<T> children, TypeFunction<T> node) {
	        //断言用于参数校验
	        Assert.notEmpty(list, "error: prams list must not belist  empty");
	        Assert.notNull(t, "error: prams t must not be null");
	
	        this.list = list;
	        this.t = t;
	        
			//获取字段名
	        this.columnParentName = TypeFunction.getLambdaColumnName(parent);
	        this.columnChildrenName = TypeFunction.getLambdaColumnName(children);
	        this.columnNodeName = TypeFunction.getLambdaColumnName(node);
	    }
    
    //定义接口，参考mybaits-plus实现
    @FunctionalInterface
	public interface TypeFunction<T> extends Serializable {

	    Object get(T source);
	
	    /**
	     * 获取列名称
	     * @param lambda
	     * @return String
	     */
	    static String getLambdaColumnName(Serializable lambda) {
		        try {
		        	//核心代码，函数式接口继承Serializable时，编译器在编译Lambda表达式时，生成了一个writeReplace方法，这个方法会返回SerializedLambda，可以反射调用这个方法；
		            Method method = lambda.getClass().getDeclaredMethod("writeReplace");
		            method.setAccessible(Boolean.TRUE);
		            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(lambda);
		            String getter = serializedLambda.getImplMethodName();
		            String fieldName = Introspector.decapitalize(getter.replace("get", ""));
		            return fieldName;
		        } catch (ReflectiveOperationException e) {
		            throw new RuntimeException(e);
		        }
		    }
		}


二、 list转树形结构
 list转树形结构在项目中经常使用，比如项目菜单列表，通常涉及多级菜单。下面是具体实现代码：

    /**
     * 将普通的entity的集合转成存在树状结构的集合
     * @return
     */
    public void tree() throws Exception {
        if (list == null || list.isEmpty()) {
            return;
        }
       
       //查找子节点
        findChildren(list, t);
    }
    
    /**
     * 获取所有子级
     * @param t
     * @param list
     * @return
     */
    private <T> void findChildren(List<T> list, T t) throws Exception {
        if (list == null || list.isEmpty()) {
            return;
        }
        String columnParentValue = BeanUtils.getProperty(t, columnParentName);

        List<T> items = new ArrayList();
        list.stream().filter(e -> {
            String columnValue = null;
            try {
                columnValue = BeanUtils.getProperty(e, columnChildrenName);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (columnParentValue == null && columnValue == null) {
                return true;
            }
            return columnParentValue != null && columnParentValue.equals(columnValue);
        }).forEach((s) -> {
            items.add(s);
        });

        if (items == null || items.isEmpty()) {
            return;
        }

        BeanUtils.setProperty(t, columnNodeName, items);

        items.stream().forEach(e -> {
            try {
            	//递归查找
                this.findChildren(list, e);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
        

  三、测试
  测试代码如下:
	  
	  //测试代码
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
	
  测试结果:

    //json
	    {
	    "children": [
	        {
	            "children": [
	                {
	                    "children": [
	                        {
	                            "code": "M4",
	                            "id": 4,
	                            "level": 3,
	                            "name": "三级菜单-a-a",
	                            "parentCode": "M2",
	                            "parentId": 2
	                        },
	                        {
	                            "code": "M5",
	                            "id": 5,
	                            "level": 3,
	                            "name": "三级菜单-a-b",
	                            "parentCode": "M2",
	                            "parentId": 2
	                        },
	                        {
	                            "code": "M6",
	                            "id": 6,
	                            "level": 3,
	                            "name": "三级菜单-a-c",
	                            "parentCode": "M2",
	                            "parentId": 2
	                        }
	                    ],
	                    "code": "M2",
	                    "id": 2,
	                    "level": 2,
	                    "name": "二级菜单-a",
	                    "parentCode": "M1",
	                    "parentId": 1
	                },
	                {
	                    "children": [
	                        {
	                            "code": "M7",
	                            "id": 7,
	                            "level": 3,
	                            "name": "三级菜单-b-a",
	                            "parentCode": "M3",
	                            "parentId": 3
	                        }
	                    ],
	                    "code": "M3",
	                    "id": 3,
	                    "level": 2,
	                    "name": "二级菜单-b",
	                    "parentCode": "M1",
	                    "parentId": 1
	                }
	            ],
	            "code": "M1",
	            "id": 1,
	            "level": 1,
	            "name": "顶级菜单-1",
	            "parentCode": ""
	        },
	        {
	            "children": [
	                {
	                    "children": [
	                        {
	                            "code": "M11",
	                            "id": 11,
	                            "level": 3,
	                            "name": "三级菜单-a-a-2",
	                            "parentCode": "M9",
	                            "parentId": 9
	                        },
	                        {
	                            "code": "M12",
	                            "id": 12,
	                            "level": 3,
	                            "name": "三级菜单-a-b-2",
	                            "parentCode": "M9",
	                            "parentId": 9
	                        },
	                        {
	                            "code": "M13",
	                            "id": 13,
	                            "level": 3,
	                            "name": "三级菜单-a-c-2",
	                            "parentCode": "M9",
	                            "parentId": 9
	                        }
	                    ],
	                    "code": "M9",
	                    "id": 9,
	                    "level": 2,
	                    "name": "二级菜单-a-2",
	                    "parentCode": "M8",
	                    "parentId": 8
	                },
	                {
	                    "children": [
	                        {
	                            "code": "M14",
	                            "id": 14,
	                            "level": 3,
	                            "name": "三级菜单-b-a-2",
	                            "parentCode": "M10",
	                            "parentId": 10
	                        }
	                    ],
	                    "code": "M10",
	                    "id": 10,
	                    "level": 2,
	                    "name": "二级菜单-b-2",
	                    "parentCode": "M8",
	                    "parentId": 8
	                }
	            ],
	            "code": "M8",
	            "id": 8,
	            "level": 1,
	            "name": "顶级菜单-2",
	            "parentCode": ""
	        }
	    ],
	    "code": ""
	}


