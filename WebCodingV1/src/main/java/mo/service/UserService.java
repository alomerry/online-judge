package mo.service;

import mo.core.Result;
import mo.entity.po.User;
import mo.entity.vo.UserLink;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface UserService {

//    String SALT = "#&***#$#@";

    /**
     * 验证用户登录
     *
     * @param username 用户名
     * @param passwd   密码
     * @param session  session
     * @return 登录成功返回用户对象，否则返回错误代码和错误描述
     */
    Result checkLogin(String username, String passwd, HttpSession session);

    /**
     * 验证注册信息
     *
     * @param user    用户实体，包含各项注册所需信息
     * @param rpt_pwd 用户密码
     * @return 返回注册结果
     */
    String checkRegister(User user, String rpt_pwd);


    /**
     * 根据指定Id查找用户
     *
     * @param userId 用户Id
     * @return 返回用户实体
     */
    User findUserByUserId(Integer userId);

    /**
     * 查询用户
     * @param page 页码
     * @param per_page 每页数量
     * @return 用户集
     */
    List<UserLink> findUsersByPageAndPerPage(Integer page, Integer per_page);
}
