package com.wsy.webseed.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wsy.webseed.common.exception.BussinessException;
import com.wsy.webseed.domain.SurveyPaperVo;
import com.wsy.webseed.domain.SurveyQuestionVo;
import com.wsy.webseed.service.PaperService;
import com.wsy.webseed.service.QuestionService;
import com.wsy.webseed.util.Operation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/paper")
public class PaperController {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PaperService paperService;

    @Autowired
    QuestionService questionService;

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String save(HttpServletRequest request, SurveyPaperVo vo) {
        String result = "";
        try {
            paperService.savePaper(vo);
            result = Operation.result(Operation.successCode, "新增问卷成功");
        } catch (BussinessException e) {
            LOGGER.error("新增问题失败: {}", e);
            result = Operation.result(Operation.failCode, "新增问卷失败");
        } catch (Exception e) {
            LOGGER.error("新增问题服务不可用: {}", e);
            result = Operation.result(Operation.failCode, "新增问卷服务不可用");
        }
        return result;
    }

    @RequestMapping(value = "/config/{paperId}", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String config(@PathVariable Long paperId, HttpServletRequest request, String questionIds) {
        String result = "";
        try {

            paperService.config(paperId, questionIds.split(","));
            result = Operation.result(Operation.successCode, "配置问卷成功");
        } catch (BussinessException e) {
            LOGGER.error("配置问卷失败: {}", e);
            result = Operation.result(Operation.failCode, "配置问卷失败");
        } catch (Exception e) {
            LOGGER.error("配置问卷服务不可用: {}", e);
            result = Operation.result(Operation.failCode, "配置问卷服务不可用");
        }
        return result;
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String toSavePage(String title, Model model) {

        return "paper/add";
    }

    @RequestMapping(value = "/upd/{id}", method = RequestMethod.GET)
    public String toUpdPage(@PathVariable Long id, Model model) {
        SurveyPaperVo vo = new SurveyPaperVo();
        try {
            vo = paperService.queryById(id);
        } catch (BussinessException e) {
            LOGGER.error("查询问卷失败: {}", e);
        } catch (Exception e) {
            LOGGER.error("查询问卷服务不可用: {}", e);
        }
        model.addAttribute("paperId", id);
        model.addAttribute("vo", vo);
        return "paper/upd";
    }

    @RequestMapping(value = "/config/{id}", method = RequestMethod.GET)
    public String toConfigPage(@PathVariable Long id, Model model) {
        SurveyPaperVo vo = new SurveyPaperVo();
        List<SurveyQuestionVo> questions = new ArrayList<SurveyQuestionVo>();
        List<SurveyQuestionVo> choosed = new ArrayList<SurveyQuestionVo>();
        try {
            vo = paperService.queryById(id);
            questions = questionService.query(new HashMap<String, Object>());
            choosed = paperService.queryByPaperId(id);
        } catch (BussinessException e) {
            LOGGER.error("查询问卷失败: {}", e);
        } catch (Exception e) {
            LOGGER.error("查询问卷服务不可用: {}", e);
        }
        model.addAttribute("paperId", id);
        model.addAttribute("questions", questions);
        model.addAttribute("choosed", choosed);
        model.addAttribute("vo", vo);
        return "paper/config";
    }


    @RequestMapping(value = "/upd", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String update(HttpServletRequest request, SurveyPaperVo vo) {

        String result = "";
        try {
            paperService.updPaper(vo);
            result = Operation.result(Operation.successCode, "修改问卷成功");
        } catch (BussinessException e) {
            LOGGER.error("新增问题失败: {}", e);
            result = Operation.result(Operation.failCode, "修改问卷失败");
        } catch (Exception e) {
            LOGGER.error("新增问题服务不可用: {}", e);
            result = Operation.result(Operation.failCode, "修改问卷服务不可用");
        }
        return result;
    }

    @RequestMapping(value = "/del/{id}", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String del(HttpServletRequest request, @PathVariable Long id) {

        String result = "";
        try {
            paperService.del(id);
            result = Operation.result(Operation.successCode, "删除问卷成功");
        } catch (BussinessException e) {
            LOGGER.error("新增问题失败: {}", e);
            result = Operation.result(Operation.failCode, "删除问卷失败");
        } catch (Exception e) {
            LOGGER.error("新增问题服务不可用: {}", e);
            result = Operation.result(Operation.failCode, "删除问卷服务不可用");
        }
        return result;
    }

    @RequestMapping(value = "/publish/{id}", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String publish(HttpServletRequest request, @PathVariable Long id) {

        String result = "";
        try {
            paperService.publish(id);
            result = Operation.result(Operation.successCode, "发布问卷成功");
        } catch (BussinessException e) {
            LOGGER.error("新增问题失败: {}", e);
            result = Operation.result(Operation.failCode, "发布问卷失败");
        } catch (Exception e) {
            LOGGER.error("新增问题服务不可用: {}", e);
            result = Operation.result(Operation.failCode, "发布问卷服务不可用");
        }
        return result;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String query(String title, Model model) {
        Map<String, Object> param = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(title)) {
            param.put("title", title);
        }
        List<SurveyPaperVo> vos = new ArrayList<SurveyPaperVo>();

        try {
            vos = paperService.query(param);
        } catch (BussinessException e) {
            LOGGER.error("查询问卷列表失败: {}", e);
        } catch (Exception e) {
            LOGGER.error("查询问卷列表服务不可用: {}", e);
        }

        model.addAttribute("list", vos);
        model.addAttribute("title", title);
        model.addAttribute("configMap", SurveyPaperVo.configMap);
        model.addAttribute("publishMap", SurveyPaperVo.publishMap);

        return "paper/list";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String queryById(@PathVariable(value = "id") Long id) {
        String result = "";
        try {
            LOGGER.info("查询问卷详细,id = {}", id);
            SurveyPaperVo vo = paperService.queryById(id);
            if (vo != null) {
                result = Operation.result(Operation.successCode, JSON.toJSONString(vo, SerializerFeature.WriteNullStringAsEmpty));
            } else {
                result = Operation.result(Operation.successCode, JSON.toJSONString(new SurveyQuestionVo(), SerializerFeature.WriteNullStringAsEmpty));
            }
        } catch (BussinessException e) {
            LOGGER.error("查询问题失败:", e);
            result = Operation.result(Operation.failCode, "查询问题失败");
        } catch (Exception e) {
            LOGGER.error("查询问题服务不可用:", e);
            result = Operation.result(Operation.failCode, "查询问题服务不可用");
        }
        return result;
    }

    @RequestMapping(value = "/showData/{id}", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String getPaperData(@PathVariable(value = "id") Long id, Model model) {
        String result = Operation.result(Operation.failCode, "");
        try {
            LOGGER.info("预览试卷,id = {}", id);
            List<SurveyQuestionVo> questionVos = paperService.queryByPaperId(id);
            result = Operation.result(Operation.successCode, JSON.toJSONString(questionVos, SerializerFeature.WriteNullStringAsEmpty));
        } catch (BussinessException e) {
            LOGGER.error("查询问题失败:", e);
        } catch (Exception e) {
            LOGGER.error("查询问题服务不可用:", e);
        }

        return result;
    }

    @RequestMapping(value = "/show/{id}", method = RequestMethod.GET)
    public String toShowPage(@PathVariable(value = "id") Long id, Model model) {
        SurveyPaperVo vo = new SurveyPaperVo();
        try {
            vo = paperService.queryById(id);
        } catch (Exception e) {
            LOGGER.error("查询问题服务不可用:", e);
        }

        model.addAttribute("paperVo", vo);

        return "paper/show";
    }
}
