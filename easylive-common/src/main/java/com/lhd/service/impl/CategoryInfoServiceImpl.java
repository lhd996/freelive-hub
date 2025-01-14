package com.lhd.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.lhd.component.RedisComponent;
import com.lhd.entity.constants.Constants;
import com.lhd.entity.enums.ResponseCodeEnum;
import com.lhd.entity.query.VideoInfoQuery;
import com.lhd.exception.BusinessException;
import com.lhd.service.VideoInfoService;
import org.springframework.stereotype.Service;

import com.lhd.entity.enums.PageSize;
import com.lhd.entity.query.CategoryInfoQuery;
import com.lhd.entity.po.CategoryInfo;
import com.lhd.entity.vo.PaginationResultVO;
import com.lhd.entity.query.SimplePage;
import com.lhd.mappers.CategoryInfoMapper;
import com.lhd.service.CategoryInfoService;
import com.lhd.utils.StringTools;


/**
 * 分类信息 业务接口实现
 */
@Service("categoryInfoService")
public class CategoryInfoServiceImpl implements CategoryInfoService {

    @Resource
    private CategoryInfoMapper<CategoryInfo, CategoryInfoQuery> categoryInfoMapper;
    @Resource
    private RedisComponent redisComponent;
    @Resource
    private VideoInfoService videoInfoService;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<CategoryInfo> findListByParam(CategoryInfoQuery param) {
        List<CategoryInfo> categoryInfoList = this.categoryInfoMapper.selectList(param);
        // 需要树形展示
        if (param.getConvertToTree() != null && param.getConvertToTree()) {
           categoryInfoList = convertLine2Tree(categoryInfoList, Constants.ZERO);
        }
        return categoryInfoList;
    }
    /**
     * 将分类变为树形结构(设置children数组)
     * @param dataList 分类列表 cid 当前分类id
     * @return
     * @author liuhd
     * 2024/12/7 17:16
     */
    private List<CategoryInfo> convertLine2Tree(List<CategoryInfo> dataList,Integer cid){
        List<CategoryInfo> res = new ArrayList<>();
        //  遍历列表 让子分类也变成树形结构
        for (CategoryInfo data : dataList) {
            // 是子分类
            if (data.getpCategoryId().equals(cid)){
                // 让子分类也设置children数组
                data.setChildren(convertLine2Tree(dataList,data.getCategoryId()));
                // 设置好了 则添加到children数组中
                res.add(data);
            }
        }
        return res;
    }
    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(CategoryInfoQuery param) {
        return this.categoryInfoMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<CategoryInfo> findListByPage(CategoryInfoQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<CategoryInfo> list = this.findListByParam(param);
        PaginationResultVO<CategoryInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(CategoryInfo bean) {
        return this.categoryInfoMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<CategoryInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.categoryInfoMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<CategoryInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.categoryInfoMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(CategoryInfo bean, CategoryInfoQuery param) {
        StringTools.checkParam(param);
        return this.categoryInfoMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(CategoryInfoQuery param) {
        StringTools.checkParam(param);
        return this.categoryInfoMapper.deleteByParam(param);
    }

    /**
     * 根据CategoryId获取对象
     */
    @Override
    public CategoryInfo getCategoryInfoByCategoryId(Integer categoryId) {
        return this.categoryInfoMapper.selectByCategoryId(categoryId);
    }

    /**
     * 根据CategoryId修改
     */
    @Override
    public Integer updateCategoryInfoByCategoryId(CategoryInfo bean, Integer categoryId) {
        return this.categoryInfoMapper.updateByCategoryId(bean, categoryId);
    }

    /**
     * 根据CategoryId删除
     */
    @Override
    public Integer deleteCategoryInfoByCategoryId(Integer categoryId) {
        return this.categoryInfoMapper.deleteByCategoryId(categoryId);
    }

    /**
     * 根据CategoryCode获取对象
     */
    @Override
    public CategoryInfo getCategoryInfoByCategoryCode(String categoryCode) {
        return this.categoryInfoMapper.selectByCategoryCode(categoryCode);
    }

    /**
     * 根据CategoryCode修改
     */
    @Override
    public Integer updateCategoryInfoByCategoryCode(CategoryInfo bean, String categoryCode) {
        return this.categoryInfoMapper.updateByCategoryCode(bean, categoryCode);
    }

    /**
     * 根据CategoryCode删除
     */
    @Override
    public Integer deleteCategoryInfoByCategoryCode(String categoryCode) {
        return this.categoryInfoMapper.deleteByCategoryCode(categoryCode);
    }


    /**
     * 新增Category
     *
     * @param
     * @return
     * @author liuhd
     * 2024/12/7 15:11
     */

    @Override
    public void saveCategory(CategoryInfo bean) {
        // 根据code查 看看有没有数据
        CategoryInfo dbBean = categoryInfoMapper.selectByCategoryCode(bean.getCategoryCode());
        // 如果是bean新增操作 并且这个编号在数据库中已经有了
        // 或者bean是修改操作 并且这个编号在数据库中有了 并且还不是自己的编号
        if (bean.getCategoryId() == null && dbBean != null ||
                bean.getCategoryId() != null && dbBean != null && !dbBean.getCategoryId().equals(bean.getCategoryId())) {
            throw new BusinessException("分类编号已经存在");
        }
        // 如果是新增操作
        if (bean.getCategoryId() == null) {
            // 保证当前分类的sort是同一个父分类下最大的
            Integer sort = categoryInfoMapper.selectMaxSort(bean.getpCategoryId());
            bean.setSort(sort + Constants.ONE);
            // 插入
            categoryInfoMapper.insert(bean);
        }
        // 如果是修改操作
        categoryInfoMapper.updateByCategoryId(bean, bean.getCategoryId());

        //重新存入redis
        save2Redis();
    }

    /**
     * 删除category by id
     *
     * @param
     * @return
     * @author liuhd
     * 2024/12/7 16:00
     */

    @Override
    public void delCategoryById(Integer categoryId) {
        // 分类下有无视频
        VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
        videoInfoQuery.setCategoryIdOrPCategoryId(categoryId);
        Integer count = videoInfoService.findCountByParam(videoInfoQuery);
        if (count > 0) {
            throw new BusinessException("分类下有视频,无法删除");
        }

        // 删除该分类以及子分类下所有视频
        CategoryInfoQuery query = new CategoryInfoQuery();
        query.setCategoryIdOrPCategoryId(categoryId);
        // 这一条sql删除该分类以及子分类下所有视频
        categoryInfoMapper.deleteByParam(query);
        //重新存入redis
        save2Redis();
    }
    /**
     * 改变sort值
     * @param categoryIds 拍好后的id
     * @return
     * @author liuhd
     * 2024/12/7 21:02
     */

    @Override
    public void changeSort(String categoryIds) {
        String[] ids  = categoryIds.split(",");
        // 构造更新参数
        List<CategoryInfo> params = new ArrayList<>();
        int sort = 0;
        for (String id : ids) {
            CategoryInfo param = new CategoryInfo();
            param.setCategoryId(Integer.parseInt(id));
            param.setSort(++ sort);
            params.add(param);
        }
        // 用这些参数做批量更新
        categoryInfoMapper.updateSortBatch(params);
        //重新存入redis
        save2Redis();
    }
    /**
     * 从redis中获取缓存
     * @param 
     * @return 
     * @author liuhd
     * 2024/12/8 10:22
     */
    
    @Override
    public List<CategoryInfo> getAllCategoryList() {
        List<CategoryInfo> categoryList = redisComponent.getCategoryList();
        if (categoryList == null){
            save2Redis();
        }
        return redisComponent.getCategoryList();
    }

    /**
     * 刷新缓存
     * @param
     * @return
     * @author liuhd
     * 2024/12/8 10:26
     */

    private void save2Redis(){
        CategoryInfoQuery query = new CategoryInfoQuery();
        query.setConvertToTree(true);
        query.setOrderBy("sort asc");
        List<CategoryInfo> categoryInfoList = findListByParam(query);
        redisComponent.saveCategoryList(categoryInfoList);
    }

}