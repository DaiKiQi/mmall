package com.mmall.service.impI;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.channels.SeekableByteChannel;
import java.util.List;
import java.util.Set;

@Service("categoryService")
public class CategoryServiceimpI implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    private Logger logger= LoggerFactory.getLogger(CategoryServiceimpI.class);


    @Override
    public ServerResponse<List<Category>> getChildCategoryById(Integer categoryId) {
        List<Category> categoryList= categoryMapper.selectCategoryByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList))
            logger.info("未找到"+categoryId+"的分类");
        return ServerResponse.createBySuccessData(categoryList);
    }

    @Override
    public ServerResponse<List<Integer>> getDeepCategoryById(Integer categoryId) {
        Set<Category> categorySet= Sets.newHashSet();
        findChildCategory(categorySet, categoryId);
        List<Integer> categoryIdList= Lists.newArrayList();
        if(categoryId !=null){
            for (Category categoryItem: categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return  ServerResponse.createBySuccessData(categoryIdList);
    }
    private Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId){
        Category category= categoryMapper.selectByPrimaryKey(categoryId);
        if(category!=null)
            categorySet.add(category);
        List<Category> categoryList= categoryMapper.selectCategoryByParentId(categoryId);
        for(Category categoryItem: categoryList){
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }
    @Override
    public ServerResponse<String> addCategoryByParentId(Integer parentId, String categoryName) {
        if(parentId==null || StringUtils.isBlank(categoryName))
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"参数错误");
        Category parentResult = categoryMapper.selectByPrimaryKey(parentId);
        if (parentResult == null)
            return ServerResponse.createByErrorMessage("父节点不存在");
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);
        int addResult = categoryMapper.insert(category);
        if (addResult == 0)
            return ServerResponse.createByErrorMessage("添加品类失败");
        return ServerResponse.createBySuccessMessage("添加品类成功");
    }

    @Override
    public ServerResponse<String> updateCategoryName(Integer categoryId, String categoryName) {
        if(categoryId==null || StringUtils.isBlank(categoryName))
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"参数错误");
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int updateResult= categoryMapper.updateByPrimaryKeySelective(category);
        if(updateResult==0)
            return ServerResponse.createByErrorMessage("更新失败");
        return ServerResponse.createBySuccessMessage("更新成功");
    }
}
