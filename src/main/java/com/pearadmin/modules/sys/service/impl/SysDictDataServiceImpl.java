package com.pearadmin.modules.sys.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pearadmin.common.tools.spring.SpringUtil;
import com.pearadmin.common.web.domain.request.PageDomain;
import com.pearadmin.modules.sys.domain.SysDictData;
import com.pearadmin.modules.sys.mapper.SysDictDataMapper;
import com.pearadmin.modules.sys.service.ISysDictDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Describe: 字典值服务实现类
 * Author: 就 眠 仪 式
 * CreateTime: 2019/10/23
 * */
@Service
public class SysDictDataServiceImpl implements ISysDictDataService {

    @Resource
    private SysDictDataMapper sysDictDataMapper;
    //字典缓存 10分钟失效
    public static LoadingCache<String, List<SysDictData>> loadingCacheSysDictData = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(600, TimeUnit.SECONDS).build(new CacheLoader<String,List<SysDictData>>() {
        @Override
        public List<SysDictData> load(String typeCode) {
            SysDictDataMapper tempSysDictDataMapper =SpringUtil.getBean("sysDictDataMapper",SysDictDataMapper.class);
            return tempSysDictDataMapper.selectByCode(typeCode);
        }
    });
    @Override
    public List<SysDictData> list(SysDictData sysDictData) {
        return sysDictDataMapper.selectList(sysDictData);
    }

    @Override
    public List<SysDictData> selectByCode(String typeCode) {
        try {
            List<SysDictData>  list=  loadingCacheSysDictData.get(typeCode);
            return list;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();

    }

    @Override
    public void refreshChcheTypeCode(String typeCode) {
        try {
            loadingCacheSysDictData.refresh(typeCode);
        }catch (Exception e){

        }
    }

    @Override
    public PageInfo<SysDictData> page(SysDictData sysDictData, PageDomain pageDomain) {
        PageHelper.startPage(pageDomain.getPage(),pageDomain.getLimit());
        List<SysDictData> list = sysDictDataMapper.selectList(sysDictData);
        return new PageInfo<>(list);
    }

    @Override
    public Boolean save(SysDictData sysDictData) {
        Integer result = sysDictDataMapper.insert(sysDictData);
        if(result>0){
            refreshChcheTypeCode(sysDictData.getTypeCode());
            return true;
        }else{
            return false;
        }
    }

    @Override
    public SysDictData getById(String id) {
       return  sysDictDataMapper.selectById(id);
    }

    @Override
    public Boolean updateById(SysDictData sysDictData) {
        int result = sysDictDataMapper.updateById(sysDictData);
        if(result > 0){
            refreshChcheTypeCode(sysDictData.getTypeCode());
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean remove(String id) {
        SysDictData sysDictData=  sysDictDataMapper.selectById(id);
        int result=0;
        if(sysDictData!=null) {
             result = sysDictDataMapper.deleteById(id);
        }
        if(result>0){
            refreshChcheTypeCode(sysDictData.getTypeCode());
            return true;
        }else{
            return false;
        }
    }
}
