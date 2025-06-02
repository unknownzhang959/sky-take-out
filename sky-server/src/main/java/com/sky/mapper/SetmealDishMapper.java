package com.sky.mapper;

import org.apache.ibatis.annotations.Select;

public interface SetmealDishMapper {
     @Select("select COUNT(*) from setmeal_dish where dish_id=#{id}")
    Integer setmealdishgetById(Long id);

}
