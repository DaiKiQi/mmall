package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

public interface ICategoryService {

    ServerResponse<List<Category>> getChildCategoryById(Integer categoryId);

    ServerResponse<String> addCategoryByParentId(Integer parentId, String categoryName);

    ServerResponse<String> updateCategoryName(Integer categoryId, String categoryName);

    ServerResponse<List<Integer>> getDeepCategoryById(Integer categoryId);
}
