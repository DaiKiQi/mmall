package com.mmall.service.impI;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("cartService")
public class CartServiceImpI implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;



    @Override
    public ServerResponse<CartVo> list(Integer userId) {
        CartVo cartVo = getCartVoLimit(userId);
        return ServerResponse.createBySuccessData(cartVo);
    }

    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer quantity, Integer productId) {
        if (productId == null || quantity == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数错误");
        Product product= productMapper.selectByPrimaryKey(productId);
        if (product==null)
            return ServerResponse.createByErrorMessage("产品不存在");
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart == null) {
            //不在购物车中
            Cart cartItemNew = new Cart();
            cartItemNew.setQuantity(quantity);
            cartItemNew.setChecked(Const.Cart.CHECKED);
            cartItemNew.setProductId(productId);
            cartItemNew.setUserId(userId);
            cartMapper.insert(cartItemNew);
        } else {
            quantity += cart.getQuantity();
            cart.setQuantity(quantity);
            cartMapper.updateByPrimaryKey(cart);
        }
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> update(Integer userId, Integer quantity, Integer productId) {
        if (productId == null || quantity == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数错误");
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(quantity);
        }
        cartMapper.updateByPrimaryKey(cart);
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> deleteProducts(Integer userId, String productIds) {
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productList))
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数错误");
        cartMapper.deleteProductByIds(userId, productList);
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> selectOrUnselectAll(Integer userId, Integer checked) {
        cartMapper.checkedOrUncheckedProduct(userId, checked, null);
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> selectOrUnselectOne(Integer userId, Integer checked, Integer productId) {
        cartMapper.checkedOrUncheckedProduct(userId, checked, productId);
        return list(userId);
    }

    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId){
        if(userId==null){
            return ServerResponse.createBySuccessData(0);
        }
        return ServerResponse.createBySuccessData(cartMapper.selectCartProductCount(userId));
    }


    private CartVo getCartVoLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");
        if (CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cart : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cart.getId());
                cartProductVo.setUserId(cart.getUserId());
                cartProductVo.setProductId(cart.getProductId());

                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if (product != null) {
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());

                    int buyLimitCount = 0;
                    if (product.getStock() >= cart.getQuantity()) {
                        buyLimitCount = cart.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    } else {
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        Cart cartForQuality = new Cart();
                        cartForQuality.setId(cart.getId());
                        cartForQuality.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKey(cart);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(cartProductVo.getQuantity().doubleValue(), product.getPrice().doubleValue()));
                    cartProductVo.setProductChecked(cart.getChecked());
                }
                if (cart.getChecked() == Const.Cart.CHECKED) {
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    @Contract("null -> false")
    private boolean getAllCheckedStatus(Integer userId) {
        if (userId == null) {
            return false;
        }
        return cartMapper.selectCartProductUncheckedStatusByUserId(userId) == 0;
    }
}
