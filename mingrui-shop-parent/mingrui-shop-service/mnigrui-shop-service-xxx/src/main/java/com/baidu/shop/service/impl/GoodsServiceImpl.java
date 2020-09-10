package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDto;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.dto.SpuDetailDTO;
import com.baidu.shop.entity.*;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


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

    @Resource
    private SpuDetailMapper spuDetailMapper;

    @Resource
    private SkuMapper skuMapper;

    @Resource
    private StockMapper stockMapper;

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

    @Override
    @Transactional
    public Result<JSONObject> saveGoodsInfo(SpuDTO spuDTO) {
        Date date = new Date();
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setSaleable(1);
        spuEntity.setValid(1);
        spuEntity.setCreateTime(date);
        spuEntity.setLastUpdateTime(date);
        //新增spu
        spuMapper.insertSelective(spuEntity);
        Integer spuId = spuEntity.getId();

        //新增spudetail
        SpuDetailDTO spuDetail = spuDTO.getSpuDetail();
        SpuDetailEntity spuDetailEntity = BaiduBeanUtil.copyProperties(spuDetail, SpuDetailEntity.class);
        spuDetailEntity.setSpuId(spuId);
        spuDetailMapper.insertSelective(spuDetailEntity);

        this.saveSkuAndStock(spuDTO.getSkus(),spuId,date);

        return this.setResultSuccess();
    }

    @Override
    public Result<SpuDetailEntity> getSpuDetailBySpuId(Integer spuId) {

        SpuDetailEntity spuDetailEntity = spuDetailMapper.selectByPrimaryKey(spuId);
        return this.setResultSuccess(spuDetailEntity);
    }

    @Override
    public Result<List<SkuDTO>> selectBySkuAndStock(Integer spuId) {
        List<SkuDTO> list = skuMapper.selectBySkuAndStock(spuId);
        return this.setResultSuccess(list);
    }

    @Override
    @Transactional
    public Result<JSONObject> delGoodsInfo(Integer spuId) {
        //删除spu
        spuMapper.deleteByPrimaryKey(spuId);
        //删除spuDetail
        spuDetailMapper.deleteByPrimaryKey(spuId);
        //删除sku stock
        this.delSkusAndStocks(spuId);

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JSONObject> editGoodsInfo(SpuDTO spuDTO) {
        Date date = new Date();
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setLastUpdateTime(date);
        //修改spu
        spuMapper.updateByPrimaryKeySelective(spuEntity);
        //修改spu-detail
        spuDetailMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(spuDTO.getSpuDetail(), SpuDetailEntity.class));

        //删除sku stock
        this.delSkusAndStocks(spuDTO.getId());

        //新增  sku stock
        this.saveSkuAndStock(spuDTO.getSkus(),spuDTO.getId(),date);

        return this.setResultSuccess();
    }


    @Override
    public Result<JSONObject> isSaleable(SpuDTO spuDTO) {
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
      // SpuEntity spuEntity = spuMapper.selectByPrimaryKey(spuDTO.getId());
        spuEntity.setId(spuDTO.getId());
        if(spuEntity.getSaleable() == 1){
            spuEntity.setSaleable(0);
        }else{
            spuEntity.setSaleable(1);
        }
        spuMapper.updateByPrimaryKeySelective(spuEntity);
        return this.setResultSuccess();
    }

    public void saveSkuAndStock(List<SkuDTO> skus, Integer spuId, Date date){
        skus.stream().forEach(skuDTO -> {
            //新增sku
            SkuEntity skuEntity = BaiduBeanUtil.copyProperties(skuDTO, SkuEntity.class);
            skuEntity.setSpuId(spuId);
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuMapper.insertSelective(skuEntity);

            //新增stock
            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDTO.getStock());
            stockMapper.insertSelective(stockEntity);
        });


    }

    public void delSkusAndStocks(Integer spuId){

        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuId);
        List<SkuEntity> skuEntities = skuMapper.selectByExample(example);
        List<Long> skuIdList = skuEntities.stream().map(sku -> sku.getId()).collect(Collectors.toList());
        if(skuIdList.size() > 0){
            skuMapper.deleteByIdList(skuIdList);
            stockMapper.deleteByIdList(skuIdList);
        }


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
        //通过cid1 cid2 cid3
//            String  categoryName  = categoryMapper.selectByIdList(
//                    Arrays.asList(spuDTO1.getCid31(), spuDTO1.getCid2(), spuDTO1.getCid3()))
//                    .stream().map(category -> category.getName())
//                    .collect(Collectors.joining("/"));
        //设置分类
        String categoryName1 = categoryMapper.getCategoryName
                (spuDTO.getCid1(), spuDTO.getCid2(), spuDTO.getCid3());

        spuDTO.setCategoryName(categoryName1);
    }
}
