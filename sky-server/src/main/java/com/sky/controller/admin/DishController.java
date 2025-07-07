package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("Add new dish")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);

        // Redis
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);

        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    /****
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("Dish page query")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * Delete dish
     **/
    @DeleteMapping
    @ApiOperation("Delete dish")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("Delete dish: {}", ids);
        dishService.deleteBatch(ids);

        // delete redisTemplate
        cleanCache("dish_*");

        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("Get dish by Id")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("Get dish by Id: {}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation("Update dish")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("Update dish: {}", dishDTO);

        // redis
        cleanCache("dish_*");

        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("query dish by id")
    public Result<List<Dish>> list(Long categoryId) {
        log.info("query dish by id: {}", categoryId);
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("start or stop dish")
    public Result startOrStop(@PathVariable("status") Integer status, Long id) {
        log.info("start or stop dish: {}", id);
        dishService.startOrStop(status, id);
        return Result.success();
    }

    private void cleanCache(String pattern) {
        log.info("clean cache: {}", pattern);
        Set<String> keys = redisTemplate.keys(pattern );
        redisTemplate.delete(keys);
    }


}
