package com.wsy.webseed.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wsy.webseed.common.exception.BussinessException;
import com.wsy.webseed.domain.StatisticsVo;
import com.wsy.webseed.domain.SurveyPaperVo;
import com.wsy.webseed.domain.SurveyStatisticsVo;
import com.wsy.webseed.service.PaperService;
import com.wsy.webseed.service.SurveyStatisticsService;
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
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/statistics")
public class StatisticsController {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SurveyStatisticsService surveyStatisticsService;
    @Autowired
    PaperService paperService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String toList(Model model) {
        List<SurveyPaperVo> list;
        try {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("isPublish", SurveyPaperVo.IS_PUBLISH);
            list = paperService.query(param);
        } catch (Exception e) {
            LOGGER.error("跳转统计列表页错误",e);
            list = new ArrayList<SurveyPaperVo>();
        }

        model.addAttribute("list", list);
        return "statistics/list";
    }

    @RequestMapping(value = "/page/{paperId}", method = RequestMethod.GET)
    public String toPaperStatistics(@PathVariable Long paperId, Model model) {
        List<SurveyStatisticsVo> list = null;
        try {
            list = surveyStatisticsService.queryStatisticsVoByPaperId(paperId);
        } catch (Exception e) {
            LOGGER.error("跳转统计列表页错误",e);
            list = new ArrayList<SurveyStatisticsVo>();
        }
        model.addAttribute("userStatistics", list);
        model.addAttribute("paperId", paperId);
        return "statistics/paperStatistics";
    }

    @RequestMapping(value = "/{paperId}", method = RequestMethod.GET)
    @ResponseBody
    public String getStatisticsDataByPaperId(@PathVariable Long paperId) {
        StatisticsVo vo = null;
        try {
            vo = surveyStatisticsService.queryStatisticsByPaperId(paperId);
            LOGGER.info(JSON.toJSONString(vo, SerializerFeature.WriteMapNullValue));
        } catch (Exception e) {
            LOGGER.error("获取统计异常,paperId:{}",paperId,e);
            vo = new StatisticsVo();
        }

        return JSON.toJSONString(vo, SerializerFeature.WriteMapNullValue);
    }

    @RequestMapping(value = "/save/{paperId}", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String save(HttpServletRequest request, @PathVariable Long paperId) {
        String result = "";
        String ip = request.getRemoteAddr();
        String statistics = request.getParameter("surveyStatistics");
        String questionStatistics = request.getParameter("surveyStatisticsQues");
        try {
            if(StringUtils.isBlank(statistics) || StringUtils.isBlank(questionStatistics)) {
                throw new BussinessException("保存问卷入参错误");
            }
            surveyStatisticsService.save(statistics, questionStatistics, ip);
            result = Operation.result(Operation.successCode, "问卷调查保存成功");
        } catch (BussinessException e) {
            LOGGER.error(e.getServiceMsg());
            result = Operation.result(Operation.failCode, e.getServiceMsg());
        } catch (Exception e) {
            LOGGER.error("统计问卷失败, paperId="+paperId);
            result = Operation.result(Operation.failCode, "统计问卷失败");
        }

        return result;
    }

    @RequestMapping(value = "/export/{paperId}")
    public void exportStatistics(HttpServletRequest request, HttpServletResponse response, @PathVariable Long paperId) {

        try {

            response.setContentType("application/octet-stream;charset=GB2312");
            OutputStream fOut = null;
            try {

                String content = surveyStatisticsService.createCsvFile(paperId);

                final String userAgent = request.getHeader("USER-AGENT");
                String finalFileName = null;
                String filename = "统计报表"+paperId;
                if(StringUtils.contains(userAgent, "MSIE")){//IE浏览器
                    finalFileName = URLEncoder.encode(filename, "UTF8");
                }else if(StringUtils.contains(userAgent, "Mozilla")){//google,火狐浏览器
                    finalFileName = new String(filename.getBytes(), "ISO8859-1");
                }else{
                    finalFileName = URLEncoder.encode(filename, "UTF8");//其他浏览器
                }
                response.setHeader("Content-Disposition","attachment; filename=" + finalFileName + ".csv");
                //将数据插入的execl表格中
                fOut = response.getOutputStream();
                if(fOut!=null)
                    fOut.write(content.getBytes("GBK"));
            } catch (Exception e) {
                LOGGER.error("导出统计文件错误,paperId:{}", paperId, e);
            } finally{
                //关闭输出文件流
                try{
                    if(fOut!=null){
                        fOut.flush();
                        fOut.close();
                    }
                }catch (IOException e){
                    LOGGER.error("colse io is error",e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("导出统计文件错误,paperId:{}",paperId,e);
        }
    }
}
