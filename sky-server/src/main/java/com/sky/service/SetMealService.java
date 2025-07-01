package com.sky.service;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;

public interface SetMealService {


    void saveWithDish(SetmealDTO setMealDTO);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
}
