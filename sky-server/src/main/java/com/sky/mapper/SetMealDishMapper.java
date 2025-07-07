package com.sky.mapper;


import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SetMealDishMapper {

    /**
     * 根据菜品id查询套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetMealIdsByDishIds(List<Long> dishIds);

    void insertBatch(List<SetmealDish> setMealDishes);

    @Delete("DELETE from setmeal_dish where id = #{id}")
    void deleteBySetmealId(Long id);

    @Select("SELECT * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getSetMealIds(Long setmealId);

    @Update("UPDATE setmeal SET name = #{name}, category_id = #{categoryId}, price = #{price}, status = #{status}, " +
            "sale = #{sale}, description = #{description}, image = #{image}, create_time = #{createTime}, " +
            "update_time = #{updateTime}, create_user = #{createUser}, update_user = #{updateUser} " +
            "WHERE id = #{id}")
    void update(Setmeal setmeal);
}
