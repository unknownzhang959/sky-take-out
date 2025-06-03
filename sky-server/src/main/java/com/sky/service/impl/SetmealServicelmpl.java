package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class SetmealServicelmpl implements SetmealService {
    @Autowired
     private SetmealMapper setmealMapper;

    @Transactional
    @Override
    public void saveWithDish(SetmealDTO setmealDTO) {
        // 保存套餐基本信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.insert(setmeal);
        //  批量保存套餐与菜品关系的信息
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmeal.getId());
            setmealMapper.insertsetmealDish(setmealDish);
        }


    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> pageResult = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(pageResult.getTotal(),pageResult.getResult());
    }

    @Transactional
    @Override
    public void delete(List<Long> ids) {
        //  起售 的套餐不能删除
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);
            if (setmeal.getStatus().equals(StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        // 删除套餐
        setmealMapper.deleteBatch(ids);
        // 删除套餐菜品关系表
        setmealMapper.deleteSetmealDishBatch(ids);

    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }

    @Override
    public SetmealVO getByIdWithDish(Long id) {
        //  查询套餐基本信息
        SetmealVO setmealVO = setmealMapper.getByIdWithDish(id);
        //  查询套餐菜品关系
        setmealVO.setSetmealDishes(setmealMapper.getsetmealByDishId(id));
        //  封装到SetmealDTO中
        setmealVO.setCategoryId(setmealVO.getCategoryId());
        return setmealVO;
    }

    @Transactional
    @Override
    public void update(SetmealDTO setmealDTO) {
        //  更新套餐基本信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);
        //  删除套餐菜品关系
        setmealMapper.deleteSetmealDishBatch(Collections.singletonList(setmealDTO.getId()));
        //  添加套餐菜品关系
        for (SetmealDish setmealDish : setmealDTO.getSetmealDishes()) {
            setmealDish.setSetmealId(setmealDTO.getId());
            setmealMapper.insertsetmealDish(setmealDish);
        }


    }
}
