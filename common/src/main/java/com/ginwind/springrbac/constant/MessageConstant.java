package com.ginwind.springrbac.constant;

/**
 * 信息提示常量类
 */
public class MessageConstant {
    public static final String LOGIN_ERROR = "密码或用户名错误";
    public static final String PASSWORD_ERROR = "密码错误";
    public static final String PASSWORD_EXPIRED = "密码已过期";
    public static final String ACCOUNT_NOT_FOUND = "账号不存在";
    public static final String ACCOUNT_BANED = "账号被禁用";
    public static final String ACCOUNT_LOCKED = "账号被锁定";
    public static final String ACCOUNT_EXPIRED = "账号已过期";
    public static final String JWT_EXPIRED = "JWT已过期";
    // 对应 SignatureException (密钥错误或Token被篡改)
    public static final String JWT_SIGNATURE_INVALID = "JWT签名无效或被篡改";
    // 对应 MalformedJwtException (格式不对，比如少了头部或尾部)
    public static final String JWT_MALFORMED = "JWT令牌格式错误";
    // 对应 UnsupportedJwtException (算法不匹配)
    public static final String JWT_UNSUPPORTED = "不支持的JWT令牌";
    // 对应 IllegalArgumentException (Token为空或null)
    public static final String JWT_EMPTY = "JWT令牌为空";
    // --- 2. 业务逻辑异常 (配合 Redis 校验) ---
    // Token 格式对，但在 Redis 里找不到 (可能是注销了或被踢下线)
    public static final String JWT_INVALID = "JWT令牌无效或已失效";
    // 请求头里根本没带 Authorization 字段
    public static final String JWT_MISSING = "请求头中缺少JWT令牌";
    // 解析出来的用户不存在
    public static final String JWT_USER_NOT_FOUND = "JWT令牌关联的用户不存在";
    public static final String UNKNOWN_ERROR = "未知错误";
    public static final String USER_NOT_LOGIN = "用户未登录";
    public static final String INSUFFICIENT_PERMISSIONS = "权限不足";
    public static final String CATEGORY_BE_RELATED_BY_SETMEAL = "当前分类关联了套餐,不能删除";
    public static final String CATEGORY_BE_RELATED_BY_DISH = "当前分类关联了菜品,不能删除";
    public static final String SHOPPING_CART_IS_NULL = "购物车数据为空，不能下单";
    public static final String ADDRESS_BOOK_IS_NULL = "用户地址为空，不能下单";
    public static final String LOGIN_FAILED = "登录失败";
    public static final String UPLOAD_FAILED = "文件上传失败";
    public static final String SETMEAL_ENABLE_FAILED = "套餐内包含未启售菜品，无法启售";
    public static final String PASSWORD_EDIT_FAILED = "密码修改失败";
    public static final String DISH_ON_SALE = "起售中的菜品不能删除";
    public static final String SETMEAL_ON_SALE = "起售中的套餐不能删除";
    public static final String DISH_BE_RELATED_BY_SETMEAL = "当前菜品关联了套餐,不能删除";
    public static final String ORDER_STATUS_ERROR = "订单状态错误";
    public static final String ORDER_NOT_FOUND = "订单不存在";
    public static final String ALREADY_EXIST = "已存在！！";
    public static final String OUT_OF_RANGE = "超出配送范围！！";
    public static final String ADDRESS_PARSE_FAILED = "地址解析失败";

}
