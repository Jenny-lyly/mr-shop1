package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDto;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.mapper.BrandCategoryMapper;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.SpuMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.PinyinUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName BrandServiceImpl
 * @Description: BrandServiceImpl
 * @Author jinluying
 * @create: 2020-08-31 14:51
 * @Version V1.0
 **/
@RestController
@Slf4j
public class BrandServiceImpl extends BaseApiService implements BrandService {

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private SpuMapper spuMapper;

    @Resource
    private BrandCategoryMapper categoryBrandMapper;

    @Override
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDto brandDTO) {
        //分页
        if(ObjectUtil.isNotNull(brandDTO.getPage()) && ObjectUtil.isNotNull(brandDTO.getRows()))
            PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());
        //排序/条件查询
        Example example = new Example(BrandEntity.class);
        if(StringUtil.isNotEmpty(brandDTO.getSort())) example.setOrderByClause(brandDTO.getOrderByClause());

        if (StringUtil.isNotEmpty(brandDTO.getName())) example.createCriteria()
                .andLike("name","%" + brandDTO.getName() + "%");
        //查询
        List<BrandEntity> list = brandMapper.selectByExample(example);
        //数据封装
        PageInfo<BrandEntity> pageInfo = new PageInfo<>(list);
        //返回
        return this.setResultSuccess(pageInfo);
    }

    @Override
    @Transactional
    public Result<JsonObject> saveBrandInfo(BrandDto brandDTO) {

         BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);

        //获取到品牌名称
        //获取到品牌名称第一个字符
        //将第一个字符转换为pinyin
        //获取拼音的首字母
        //统一转为大写
        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().charAt(0))
                , PinyinUtil.TO_FIRST_CHAR_PINYIN).charAt(0));

        brandMapper.insertSelective(brandEntity);

        this.insertCategoryAndBrand(brandDTO,brandEntity);

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JsonObject> updateBrandInfo(BrandDto brandDTO) {
        BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO, BrandEntity.class);

        brandEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandEntity.getName().charAt(0))
                , PinyinUtil.TO_FIRST_CHAR_PINYIN).charAt(0));

        brandMapper.updateByPrimaryKeySelective(brandEntity);

        this.deleteCategoryAndBrand(brandEntity.getId());

        this.insertCategoryAndBrand(brandDTO,brandEntity);

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JsonObject> deleteBrandInfo(Integer id) {
        Example example = new Example(SpuEntity.class);
        example.createCriteria().andEqualTo("brandId",id);
        List<SpuEntity> spuEntityList = spuMapper.selectByExample(example);
        if(spuEntityList.size() >0){
            return this.setResultError(HTTPStatus.OPERATION_ERROR,"当前品牌被商品绑定,不能删除");
        }
        brandMapper.deleteByPrimaryKey(id);

        this.deleteCategoryAndBrand(id);

        return this.setResultSuccess();
    }

    @Override
    public Result<List<BrandEntity>> getBrandByCate(Integer cid) {
        List<BrandEntity> list = brandMapper.getBrandByCate(cid);
        return this.setResultSuccess(list);
    }

    private void deleteCategoryAndBrand(Integer id){

        //通过brandId删除中间表中的数据
        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId",id);
        categoryBrandMapper.deleteByExample(example);
    }

    private void insertCategoryAndBrand(BrandDto brandDTO,BrandEntity brandEntity){

        if(brandDTO.getCategory().contains(",")){

            //通过split方法分割字符串的Array
            //Arrays.asList将Array转换为List
            //使用JDK1,8的stream
            //使用map函数返回一个新的数据
            //collect 转换集合类型Stream<T>
            //Collectors.toList())将集合转换为List类型
            List<CategoryBrandEntity> categoryBrandEntities = Arrays.asList(brandDTO.getCategory().split(","))
                    .stream().map(cid -> {

                        CategoryBrandEntity entity = new CategoryBrandEntity();
                        entity.setCategoryId(StringUtil.toInteger(cid));
                        entity.setBrandId(brandEntity.getId());

                        return entity;
                    }).collect(Collectors.toList());

            //批量新增
            categoryBrandMapper.insertList(categoryBrandEntities);

        }else{
            //新增
            CategoryBrandEntity entity = new CategoryBrandEntity();

            entity.setCategoryId(StringUtil.toInteger(brandDTO.getCategory()));
            entity.setBrandId(brandEntity.getId());

            categoryBrandMapper.insertSelective(entity);
        }
    }

}
