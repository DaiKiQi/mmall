package com.mmall.controller.backend;


import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IProductService productService;
    @Autowired
    private IFileService fileService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product) {
        ServerResponse x = checkValid(session);
        if (x != null) return x;
        return productService.saveOrUpdateProduct(product);
    }

    @Nullable
    private ServerResponse checkValid(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        ServerResponse adminRoleResult = userService.checkAdminRole(user);
        if (!adminRoleResult.isSuccess())
            return ServerResponse.createBySuccessMessage("需要管理员权限");
        return null;
    }

    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {
        ServerResponse x = checkValid(session);
        if (x != null) return x;
        return productService.setSaleStatus(productId, status);
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetail(HttpSession session, Integer productId) {
        ServerResponse x = checkValid(session);
        if (x != null) return x;
        return productService.manageProductDetail(productId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getList(HttpSession session,
                                            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        ServerResponse x = checkValid(session);
        if (x != null) return x;
        return productService.getProductList(pageNum, pageSize);
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> productSearch(HttpSession session, String productName, Integer productId,
                                                  @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        ServerResponse x = checkValid(session);
        if (x != null) return x;
        return productService.searchProduct(productName, productId, pageNum, pageSize);
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session,
                                 @RequestParam(value = "upload_file", required = false) MultipartFile file,
                                 HttpServletRequest request) {
        ServerResponse x = checkValid(session);
        if (x != null) return x;

        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = fileService.upload(file, path);
        if (StringUtils.isBlank(targetFileName))
            return ServerResponse.createByErrorMessage("上传失败");
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
        Map fileMap = Maps.newHashMap();
        fileMap.put("uri", targetFileName);
        fileMap.put("url", url);
        return ServerResponse.createBySuccessData(fileMap);
    }

    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpSession session,
                                 @RequestParam(value = "upload_file", required = false) MultipartFile file,
                                 HttpServletRequest request, HttpServletResponse response) {
        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "请登录管理员");
            return resultMap;
        }
        ServerResponse adminRoleResult = userService.checkAdminRole(user);
        if (!adminRoleResult.isSuccess()){
            resultMap.put("success", false);
            resultMap.put("msg", "需要管理员权限");
            return resultMap;
        }

        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = fileService.upload(file, path);
        if(StringUtils.isBlank(targetFileName)){
            resultMap.put("success", false);
            resultMap.put("msg", "上传失败");
            return resultMap;
        }
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
        resultMap.put("uri", targetFileName);
        resultMap.put("url", url);
        response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
        return resultMap;
    }
}
