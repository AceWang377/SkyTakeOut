package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
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

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        int pageNum = setmealPageQueryDTO.getPage();
        int pageSize = setmealPageQueryDTO.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        Page<SetmealVO> page = setMealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }
}
