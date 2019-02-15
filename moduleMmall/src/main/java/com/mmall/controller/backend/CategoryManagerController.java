package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/manage/category")
public class CategoryManagerController {
    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IUserService userService;

    @RequestMapping(value = "get_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Category>> getCategory(HttpSession session, @RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        ServerResponse adminRoleResult = userService.checkAdminRole(user);
        if (!adminRoleResult.isSuccess())
            return ServerResponse.createBySuccessMessage("需要管理员权限");
        ServerResponse<List<Category>> categoryResult =
                categoryService.getChildCategoryById(parentId);
        return categoryResult;
    }

    @RequestMapping(value = "add_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> addCategory(HttpSession session, @RequestParam(value = "parentId", defaultValue = "0") Integer parentId, String categoryName) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        ServerResponse adminRoleResult = userService.checkAdminRole(user);
        if (!adminRoleResult.isSuccess())
            return ServerResponse.createBySuccessMessage("需要管理员权限");
        ServerResponse<String> responseResult =
                categoryService.addCategoryByParentId(parentId, categoryName);
        return responseResult;
    }


    @RequestMapping(value = "set_category_name.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        ServerResponse adminRoleResult = userService.checkAdminRole(user);
        if (!adminRoleResult.isSuccess())
            return ServerResponse.createBySuccessMessage("需要管理员权限");
        ServerResponse<String> responseResult =
                categoryService.updateCategoryName(categoryId, categoryName);
        return responseResult;
    }

    @RequestMapping(value = "get_deep_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Integer>> getDeepCategory(HttpSession session, Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        ServerResponse adminRoleResult = userService.checkAdminRole(user);
        if (!adminRoleResult.isSuccess())
            return ServerResponse.createBySuccessMessage("需要管理员权限");
        ServerResponse<List<Integer>> categoryResult =
                categoryService.getDeepCategoryById(categoryId);
        return categoryResult;
    }
}
