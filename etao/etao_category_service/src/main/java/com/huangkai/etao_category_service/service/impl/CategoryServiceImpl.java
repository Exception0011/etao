package com.huangkai.etao_category_service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_category_service.mapper.CategoryMapper;
import com.huangkai.etao_common.domain.Category;
import com.huangkai.etao_common.service.CategoryService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * @author Huangkai on 2023/5/22
 */
@DubboService
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public void add(Category category) {
        categoryMapper.insert(category);
        refreshRedisCategory();
    }

    @Override
    public void update(Category category) {
        categoryMapper.updateById(category);
        refreshRedisCategory();
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Category category = categoryMapper.selectById(id);
        category.setStatus(status);
        categoryMapper.updateById(category);
        refreshRedisCategory();
    }

    @Override
    public void delete(Long[] ids) {
        categoryMapper.deleteBatchIds(Arrays.asList(ids));
        refreshRedisCategory();
    }

    @Override
    public Category findById(Long id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public Page<Category> search(int page, int size) {
        return categoryMapper.selectPage(new Page<>(page,size),null);
    }

    @Override
    public List<Category> findAll() {
        ListOperations<String,Category> listOperations = redisTemplate.opsForList();
        List<Category> category = listOperations.range("category", 0, -1);//0到-1表示查询所有
        if (category != null && category.size() >0){
            System.out.println("redis");
            return category;
        }else {
            System.out.println("数据库");
            QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status",1);
            List<Category> categories = categoryMapper.selectList(queryWrapper);
            listOperations.leftPushAll("category",categories);
            return categories;
        }
    }

    public void refreshRedisCategory(){
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",1);
        List<Category> categories = categoryMapper.selectList(queryWrapper);
        //先删除原有的广告
        redisTemplate.delete("category");
        //将新的广告同步进去
        ListOperations<String,Category> listOperations = redisTemplate.opsForList();
        listOperations.leftPushAll("category",categories);
    }
}

