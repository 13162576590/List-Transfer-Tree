package com;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * list数据转成树状结构
 * @author chenyouhong
 */
public class ListTransferTree {

    private List list;

    private Object t;

    private String columnParentName;
    private String columnChildrenName;
    private String columnNodeName;

    private ListTransferTree() {

    }

    public <T> ListTransferTree(List<T> list, T t, TypeFunction<T> parent, TypeFunction<T> children, TypeFunction<T> node) {
        Assert.notEmpty(list, "error: prams list must not be empty");
        Assert.isTrue(t != null, "error: prams t must not be null");

        this.list = list;
        this.t = t;
        this.columnParentName = TypeFunction.getLambdaColumnName(parent);
        this.columnChildrenName = TypeFunction.getLambdaColumnName(children);
        this.columnNodeName = TypeFunction.getLambdaColumnName(node);
    }

    /**
     * 将普通的entity的集合转成存在树状结构的集合
     * @return
     */
    public void tree() throws Exception {
        if (list == null || list.isEmpty()) {
            return;
        }

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
                this.findChildren(list, e);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

}
