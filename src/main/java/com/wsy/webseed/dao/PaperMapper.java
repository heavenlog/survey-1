package com.wsy.webseed.dao;

import com.wsy.webseed.dao.base.ISqlMapper;
import com.wsy.webseed.domain.SurveyPaperVo;
import com.wsy.webseed.domain.SurveyQuestionVo;
import com.wsy.webseed.domain.entity.SurveyQuestion;

import java.util.List;
import java.util.Map;


public interface PaperMapper extends ISqlMapper {

    public int save(SurveyPaperVo vo);

    public int update(SurveyPaperVo vo);

    public List<SurveyPaperVo> query(Map<String, Object> param);

    public SurveyPaperVo queryById(Long id);

    public List<SurveyQuestionVo> queryByPaperId(Long paperId);

    public int savePaperQuestionRelation(Map<String, Object> param);

    public int delPaperQuestionRelation(Long paperId);
}
