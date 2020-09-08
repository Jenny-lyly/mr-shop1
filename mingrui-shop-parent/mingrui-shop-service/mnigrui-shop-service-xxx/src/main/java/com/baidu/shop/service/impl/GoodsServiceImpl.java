package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDto;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.mapper.SpuMapper;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName GoodsServiceImpl
 * @Description: GoodsServiceImpl
 * @Author jinluying
 * @create: 2020-09-07 14:18
 * @Version V1.0
 **/
@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private SpuMapper spuMapper;

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public Result<PageInfo<SpuEntity>> getSpuInfo(SpuDTO spuDTO) {

        List<SpuEntity> list = this.getByExample(spuDTO);

        List<SpuDTO> spuDtoList = list.stream().map(spuEntity -> {

            SpuDTO spuDTO1 = BaiduBeanUtil.copyProperties(spuEntity, SpuDTO.class);

            //通过品牌id查询品牌名称
            this.getBrandNameByBid(spuEntity,spuDTO1);

            //设置分类
            this.getCateNameByCid(spuDTO1);

            return spuDTO1;
        }).collect(Collectors.toList());

        PageInfo<SpuEntity> pageInfo = new PageInfo<>(list);

        return this.setResult(HTTPStatus.OK,pageInfo.getTotal()+"",spuDtoList);
    }

    public List<SpuEntity> getByExample(SpuDTO spuDTO){
        //分页
        if(ObjectUtil.isNotNull(spuDTO.getPage()) && ObjectUtil.isNotNull(spuDTO.getRows()))
            PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());

        //构建条件查询
        Example example = new Example(SpuEntity.class);
        Example.Criteria criteria = example.createCriteria();

        //条件查询
        if(StringUtil.isNotEmpty(spuDTO.getTitle())) criteria.andLike("title","%"+spuDTO.getTitle()+"%");

        if(ObjectUtil.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable() != 2)
            criteria.andEqualTo("saleable",spuDTO.getSaleable());

        //排序
        if(StringUtil.isNotEmpty(spuDTO.getSort())) example.setOrderByClause(spuDTO.getOrderByClause());

        List<SpuEntity> list = spuMapper.selectByExample(example);

        return  list;
    }
    public void getBrandNameByBid(SpuEntity spuEntity,SpuDTO spuDTO){

        //通过品牌id查询品牌名称
        BrandEntity brandEntity = brandMapper.selectByPrimaryKey(spuEntity.getBrandId());

        if( ObjectUtil.isNotNull(brandEntity))  spuDTO.setBrandName(brandEntity.getName());
    }

    public void getCateNameByCid(SpuDTO spuDTO){
        //设置分类
        String categoryName1 = categoryMapper.getCategoryName(spuDTO.getCid1(), spuDTO.getCid2(), spuDTO.getCid3());

        spuDTO.setCategoryName(categoryName1);
    }


}
//通过cid1 cid2 cid3
//            String  categoryName  = categoryMapper.selectByIdList(
//                    Arrays.asList(spuDTO1.getCid31(), spuDTO1.getCid2(), spuDTO1.getCid3()))
//                    .stream().map(category -> category.getName())
//                    .collect(Collectors.joining("/"));