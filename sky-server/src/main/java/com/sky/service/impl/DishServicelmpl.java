package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
public class DishServicelmpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
     private SetmealDishMapper SetmealDishMapper;
    @Transactional
    @Override
    public void saveWithFlavor(DishDTO  dishDTO) {
        //保存菜品信息到菜品表
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);
        log.info("保存的菜品信息：{}",dish);
        //保存 菜品口味数据到菜品口味表
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        for (DishFlavor dishFlavor : flavors) {
             dishFlavor.setDishId(dishId);
         }
         dishMapper.insertBatch(flavors);

    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> pageResult = dishMapper.pageQuery(dishPageQueryDTO);
        return  new PageResult(pageResult.getTotal(),pageResult.getResult());
    }

    @Transactional
    @Override
    public void delete(List<Long> ids) {
        //起售 的菜品不能删除
         for (Long id : ids) {
              Dish dish = dishMapper.getById(id);
               if (dish.getStatus() == StatusConstant.ENABLE) {
                    throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
               }
         }
        //被套餐关联 的菜品不能删除
        for (Long id : ids) {
             Integer Count = SetmealDishMapper.setmealdishgetById(id);
              if (Count > 0) {
                  throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
              }
        }
        //删除菜品表中的数据
        dishMapper.deleteBatch(ids);
        //删除菜品口味表中的数据
         dishMapper.deleteFlavorBatch(ids);
    }
}
