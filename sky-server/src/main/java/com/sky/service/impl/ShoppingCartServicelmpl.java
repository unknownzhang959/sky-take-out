package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServicelmpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper SetmealMapper;


    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> carts = shoppingCartMapper.list(shoppingCart);
        //判断购物车中是否存在该商品
        if (carts == null){ //如果购物车中不存在该商品，则添加到购物车中
            //判断购物车中该商品是否为菜品
            if (shoppingCartDTO.getDishId() != null){
                Dish d = dishMapper.getById(shoppingCartDTO.getDishId());
                shoppingCart.setName(d.getName());
                shoppingCart.setImage(d.getImage());
                shoppingCart.setAmount(d.getPrice());
            }else {
                Setmeal s = SetmealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(s.getName());
                shoppingCart.setImage(s.getImage());
                shoppingCart.setAmount(s.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);

        }else{
            //如果购物车中存在该商品，则数量加一
            shoppingCart = carts.get(0);
            shoppingCart.setNumber(shoppingCart.getNumber()+1);
            shoppingCartMapper.update(shoppingCart);
        }
        //最终目标：添加到购物车中，如果购物车中已经存在该商品，则数量加一，否则添加到购物车中

    }

    @Override
    public List<ShoppingCart> list() {
        List<ShoppingCart> shoppingCarts =shoppingCartMapper.list(ShoppingCart.builder().userId(BaseContext.getCurrentId()).build());
//        List<ShoppingCart> shoppingCarts =shoppingCartMapper.listcart(BaseContext.getCurrentId());
        return shoppingCarts;
    }

    @Override
    public void clean() {
        //清空自己的购物车
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.clean(userId);
    }

    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> lists = shoppingCartMapper.list(shoppingCart);
        ShoppingCart list = lists.get(0);
        if (list.getNumber() == 1){
            shoppingCartMapper.delete(list);
        }else{
            list.setNumber(list.getNumber()-1);
            shoppingCartMapper.update(list);
        }

    }
}
