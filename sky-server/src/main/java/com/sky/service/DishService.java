package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    //新增菜品，同时插入菜品对应的口味数据
    void saveWithFlavor(DishDTO dishDTO);
    // 菜品查询
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);
    // 批量删除
    void delete(List<Long> ids);
}
