package com.sky.service.impl;

import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.SetMealService;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    private SetmealMapper setMealMapper;

    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    @Override
    @Transactional
    public void saveWithDish(SetmealDTO setMealDTO) {
        Setmeal setMeal = new Setmeal();
        BeanUtils.copyProperties(setMealDTO, setMeal);

        setMealMapper.insert(setMeal);

        //Get the generate ID
        Long setMealId = setMeal.getId();

        List<SetmealDish> setMealDishes = setMealDTO.getSetmealDishes();
        setMealDishes.forEach(setMealDish -> {setMealDish.setSetmealId(setMealId);});

        setMealDishMapper.insertBatch(setMealDishes);

    }
}
