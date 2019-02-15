package com.mmall.controller.portal;


import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    private ICartService cartService;

    @RequestMapping(value = "list.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse list(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录");
        return cartService.list(user.getId());
    }

    @RequestMapping(value = "add.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse add(HttpSession session, @RequestParam(value = "count") Integer quantity, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录");
        return cartService.add(user.getId(), quantity, productId);
    }

    @RequestMapping(value = "update.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse update(HttpSession session, @RequestParam(value = "count") Integer quantity, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录");
        return cartService.update(user.getId(), quantity, productId);
    }

    @RequestMapping(value = "delete_product.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse delete(HttpSession session, String productIds) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录");
        return cartService.deleteProducts(user.getId(), productIds);
    }

    //选择
    @RequestMapping(value = "select_all.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录");
        return cartService.selectOrUnselectAll(user.getId(), Const.Cart.CHECKED);
    }

    @RequestMapping(value = "unselect_all.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> unselectAll(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录");
        return cartService.selectOrUnselectAll(user.getId(), Const.Cart.UNCHECKED);
    }

    @RequestMapping(value = "select.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> select(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录");
        return cartService.selectOrUnselectOne(user.getId(), Const.Cart.CHECKED, productId);
    }

    @RequestMapping(value = "unselect.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<CartVo> unselect(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录");
        return cartService.selectOrUnselectOne(user.getId(), Const.Cart.UNCHECKED, productId);
    }

    @RequestMapping(value = "get_cart_product_count.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "需要登录");
        return cartService.getCartProductCount(user.getId());
    }
}
