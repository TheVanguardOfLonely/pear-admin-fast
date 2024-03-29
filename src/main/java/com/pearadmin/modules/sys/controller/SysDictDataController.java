package com.pearadmin.modules.sys.controller;

import com.github.pagehelper.PageInfo;
import com.pearadmin.common.plugins.system.domain.SysDictDataModel;
import com.pearadmin.common.plugins.system.service.ISysBaseAPI;
import com.pearadmin.common.tools.sequence.SequenceUtil;
import com.pearadmin.common.tools.sql.SqlInjectionUtil;
import com.pearadmin.common.web.base.BaseController;
import com.pearadmin.common.web.domain.request.PageDomain;
import com.pearadmin.common.web.domain.response.Result;
import com.pearadmin.common.web.domain.response.ResultTable;
import com.pearadmin.modules.sys.domain.SysDictData;
import com.pearadmin.modules.sys.service.ISysDictDataService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Describe: 主页控制器
 * Author: 就 眠 仪 式
 * CreateTime: 2019/10/23
 * */
@RestController
@RequestMapping("system/dictData")
public class SysDictDataController extends BaseController {

    /**
     * 基础路径
     * */
    private String MODULE_PATH = "system/dict/data/";

    @Resource
    private ISysDictDataService sysDictDataService;

    @Resource
    private ISysBaseAPI iSysBaseAPI;

    /**
     * Describe: 数据字典列表视图
     * Param: ModelAndView
     * Return: ModelAndView
     * */
    @GetMapping("main")
    @PreAuthorize("hasPermission('/system/dictData/main','sys:dictData:main')")
    public ModelAndView main(Model model, String typeCode){
        model.addAttribute("typeCode",typeCode);
        return JumpPage(MODULE_PATH + "main");
    }

    /**
     * Describe: 数据字典列表数据
     * Param: sysDictType
     * Return: Result
     * */
    @GetMapping("data")
    @PreAuthorize("hasPermission('/system/dictData/data','sys:dictData:data')")
    public ResultTable data (SysDictData sysDictData, PageDomain pageDomain){
       PageInfo<SysDictData> pageInfo = sysDictDataService.page(sysDictData,pageDomain);
       return pageTable(pageInfo.getList(),pageInfo.getTotal());
    }

    /**
     * Describe: 根据字典code获取数据字典列表数据
     * Param: typeCode
     * Return: Result
     * */
    @GetMapping("selectByCode")
    @PreAuthorize("hasPermission('/system/dictData/selectByCode','sys:dictData:selectByCode')")
    public Result selectByCode (String typeCode){
        List<SysDictData> list = sysDictDataService.selectByCode(typeCode);
        return success(list);
    }
    /**
     * 获取字典数据
     * @param dictCode 字典code
     * @param dictCode 表名,文本字段,code字段  | 举例：sys_dept,dept_name,dept_id
     * @return
     */
    @GetMapping(value = "/getDictItems/{dictCode}")
    public Result<List<SysDictDataModel>> getDictItems(@PathVariable String dictCode, @RequestParam(value = "sign",required = false) String sign,HttpServletRequest request) {

        Result<List<SysDictDataModel>> result = new Result<List<SysDictDataModel>>();
        List<SysDictDataModel> ls = null;
        try {
            if(dictCode.indexOf(",")!=-1) {
                //关联表字典（举例：sys_user,realname,id）
                String[] params = dictCode.split(",");

                if(params.length<3) {
                    return Result.failure("字典Code格式不正确！");
                }
                //SQL注入校验（只限制非法串改数据库）
                final String[] sqlInjCheck = {params[0],params[1],params[2]};
                SqlInjectionUtil.filterContent(sqlInjCheck);

                if(params.length==4) {
                    //SQL注入校验（查询条件SQL 特殊check，此方法仅供此处使用）
                    SqlInjectionUtil.specialFilterContent(params[3]);
                    ls = iSysBaseAPI.queryTableDictItemsByCodeAndFilter(params[0],params[1],params[2],params[3]);
                }else if (params.length==3) {
                    ls = iSysBaseAPI.queryTableDictItemsByCode(params[0],params[1],params[2]);
                }else{
                    return Result.failure("字典Code格式不正确！");
                }
            }else {
                //字典表
                ls = iSysBaseAPI.selectDictByCode(dictCode);
            }

            result.setSuccess(true);
            result.setData(ls);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure("操作失败！");
        }

        return result;
    }
    /**
     * 根据字典code加载字典text 返回
     */
    @RequestMapping(value = "/loadDictItem/{dictCode}", method = RequestMethod.GET)
    public Result<List<SysDictDataModel>> loadDictItem(@PathVariable String dictCode,@RequestParam(name="key") String keys, @RequestParam(value = "sign",required = false) String sign,HttpServletRequest request) {
        Result<List<SysDictDataModel>> result = new Result<>();
        try {
            if(dictCode.indexOf(",")!=-1) {
                String[] params = dictCode.split(",");
                if(params.length!=3) {
                    return Result.failure("字典Code格式不正确！");
                }
                String[] keyArray = keys.split(",");
                List<SysDictDataModel> texts = iSysBaseAPI.queryTableDictByKeys(params[0], params[1], params[2], keyArray);
                return Result.success(texts);
            }else {
                return Result.failure("字典Code格式不正确！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure("操作失败！");
        }
    }

    /**
     * Describe: 数据字典类型新增视图
     * Param: sysDictType
     * Return: ModelAndView
     * */
    @GetMapping("add")
    @PreAuthorize("hasPermission('/system/dictData/add','sys:dictData:add')")
    public ModelAndView add(Model model,String typeCode){
        model.addAttribute("typeCode",typeCode);
        return JumpPage(MODULE_PATH+"add");
    }

    /**
     * Describe: 新增字典类型接口
     * Param: sysDictType
     * Return: Result
     * */
    @PostMapping("save")
    @PreAuthorize("hasPermission('/system/dictData/add','sys:dictData:add')")
    public Result save(@RequestBody SysDictData sysDictData){
        sysDictData.setDataId(SequenceUtil.makeStringId());
        Boolean result = sysDictDataService.save(sysDictData);
        return decide(result);
    }

    /**
     * Describe: 数据字典类型修改视图
     * Param: sysDictType
     * Return: ModelAndView
     * */
    @GetMapping("edit")
    @PreAuthorize("hasPermission('/system/dictData/edit','sys:dictData:edit')")
    public ModelAndView edit(Model model,String dataId){
        model.addAttribute("sysDictData",sysDictDataService.getById(dataId));
        return JumpPage(MODULE_PATH+"edit");
    }

    /**
     * Describe: 数据字典类型修改视图
     * Param: sysDictData
     * Return: ModelAndView
     * */
    @PutMapping("update")
    @PreAuthorize("hasPermission('/system/dictData/edit','sys:dictData:edit')")
    public Result update(@RequestBody SysDictData sysDictData){
        boolean result =  sysDictDataService.updateById(sysDictData);
        return decide(result);
    }

    /**
     * Describe: 数据字典删除
     * Param: id
     * Return: Result
     * */
    @DeleteMapping("remove/{id}")
    @PreAuthorize("hasPermission('/system/dictData/remove','sys:dictData:remove')")
    public Result remove(@PathVariable("id")String id){
        Boolean result = sysDictDataService.remove(id);
        return decide(result);
    }

    /**
     * Describe: 根据 Id 启用字典
     * Param dictId
     * Return ResuTree
     * */
    @PutMapping("enable")
    public Result enable(@RequestBody SysDictData sysDictData){
        sysDictData.setEnable("0");
        boolean result = sysDictDataService.updateById(sysDictData);
        return decide(result);
    }

    /**
     * Describe: 根据 Id 禁用字典
     * Param dictId
     * Return ResuTree
     * */
    @PutMapping("disable")
    public Result disable(@RequestBody SysDictData sysDictData){
        sysDictData.setEnable("1");
        boolean result = sysDictDataService.updateById(sysDictData);
        return decide(result);
    }
}
