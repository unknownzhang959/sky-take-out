package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
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
    // 根据id查询菜品和对应的口味数据
    DishVO getByIdWithFlavor(Long id);
    // 修改菜品
    void update(DishDTO dishDTO);
    // 起售停售
    void startOrStop(Integer status, Long id);
    // 管理端：根据分类id查询菜品，包括起售和停售的
    List<DishVO> list(Long categoryId);
    //  C端：根据条件查询菜品数据，只有启售的
    List<DishVO> listWithFlavor(Dish dish);
}
