package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

public interface ICartService {
    ServerResponse<CartVo> add(Integer userId, Integer quantity, Integer productId);

    ServerResponse<CartVo> update(Integer userId, Integer quantity, Integer productId);

    ServerResponse<CartVo> deleteProducts(Integer userId, String productIds);

    ServerResponse<CartVo> list(Integer userId);

    ServerResponse<CartVo> selectOrUnselectAll(Integer userId,Integer checked);

    ServerResponse<CartVo> selectOrUnselectOne(Integer userId,Integer checked, Integer productId);

    ServerResponse<Integer> getCartProductCount(Integer userId);
}
