package mo.controller.admin.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import mo.controller.AbstractController;
import mo.controller.admin.AdminNewsController;
import mo.core.Permission;
import mo.core.PermissionManager;
import mo.core.Result;
import mo.core.ResultCode;
import mo.entity.po.News;
import mo.entity.po.Privilege;
import mo.interceptor.annotation.AuthCheck;
import mo.interceptor.annotation.RequiredType;
import mo.service.NewsService;
import mo.service.PrivilegeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class AdminNewsControllerImpl extends AbstractController implements AdminNewsController {

    private static final Logger logger = LoggerFactory.getLogger(AdminNewsControllerImpl.class);

    @Resource
    private NewsService newsService;

    @Resource
    private PrivilegeService privilegeService;

    @Override
    @AuthCheck({RequiredType.JWT, RequiredType.ADMIN})
    @ResponseBody
    @RequestMapping(value = "/admin/news", method = RequestMethod.POST)
    public Result createNews(@RequestParam("news") String news) {

        News topic = JSON.parseObject(news, new TypeReference<News>() {
        });
        logger.info("前端Json转JavaBean成功,news[{}]", topic);
        if (newsService.createNews(topic, getJWTUserId())) {
            return new Result().setCode(ResultCode.OK);
        } else {
            return new Result().setCode(ResultCode.INTERNAL_SERVER_ERROR).setMessage("添加失败!");
        }
    }

    @Override
    @ResponseBody
    @RequestMapping(value = "/admin/news", method = RequestMethod.GET)
    @AuthCheck({RequiredType.JWT, RequiredType.ADMIN})
    public Result getNews(@RequestParam(value = "page", defaultValue = "1") String page,
                          @RequestParam(value = "per_page", defaultValue = "10") String per_page) {
        //TODO 如果是公告管理者,可以查找所有人的公告
        JSONObject news = new JSONObject();
        news.put("newsLink", newsService.findNews(getJWTUserId(), Integer.valueOf(page), Integer.valueOf(per_page)));
        return new Result().setCode(ResultCode.OK).setData(news);
    }

    @Override
    @ResponseBody
    @AuthCheck({RequiredType.JWT, RequiredType.ADMIN})
    @RequestMapping(value = "/admin/news", method = RequestMethod.PUT)
    public Result updateNews(@RequestBody News news) {
        logger.info("前端Json转JavaBean成功,news[{}]", news);
        if (news.getNews_id() == null) {
            return new Result().setCode(ResultCode.BAD_REQUEST).setMessage("此公告不存在!");
        } else {
            Integer user_id = getJWTUserId();
            Privilege privilege = privilegeService.findPrivilegeByUserId(user_id);
            if (!user_id.equals(news.getUser_id()) && !PermissionManager.isLegalAdmin(Permission.Announcement_manager, privilege.getRightstr())) {
                return new Result().setCode(ResultCode.FORBIDDEN).setMessage("权限不足!");
            } else {
                if (newsService.updateNews(news)) {
                    return new Result().setCode(ResultCode.OK);
                } else {
                    return new Result().setCode(ResultCode.INTERNAL_SERVER_ERROR).setMessage("修改失败!");
                }
            }
        }
    }

    @Override
    @ResponseBody
    @AuthCheck({RequiredType.JWT, RequiredType.ADMIN})
    @RequestMapping(value = "/admin/news/{news_id}", method = RequestMethod.DELETE)
    public Result deleteNews(@PathVariable Integer news_id) {
        Integer user_id = getJWTUserId();
        Privilege privilege = privilegeService.findPrivilegeByUserId(user_id);
        News news = null;
        if ((news = newsService.findNews(news_id)) == null) {
            return new Result().setCode(ResultCode.BAD_REQUEST).setMessage("该公告不存在!");
        } else {
            if (!news.getUser_id().equals(user_id) && !PermissionManager.isLegalAdmin(Permission.Announcement_manager, privilege.getRightstr())) {
                return new Result().setCode(ResultCode.BAD_REQUEST).setMessage("权限不足!");
            } else {
                if (newsService.deleteNews(news_id)) {
                    return new Result().setCode(ResultCode.OK);
                } else {
                    return new Result().setCode(ResultCode.BAD_REQUEST).setMessage("内部错误删除失败!");
                }
            }
        }
    }
}
