package com.atguigu.jxc.service.impl;

import com.atguigu.jxc.dao.GoodsDao;
import com.atguigu.jxc.domain.ServiceVO;
import com.atguigu.jxc.domain.SuccessCode;
import com.atguigu.jxc.entity.*;


import com.atguigu.jxc.entity.Unit;

import com.atguigu.jxc.entity.vo.GoodsTypeVo;

import com.atguigu.jxc.service.GoodsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsDao goodsDao;

    @Override
    public ServiceVO getCode() {

        // 获取当前商品最大编码
        String code = goodsDao.getMaxCode();

        // 在现有编码上加1
        Integer intCode = Integer.parseInt(code) + 1;

        // 将编码重新格式化为4位数字符串形式
        String unitCode = intCode.toString();

        for (int i = 4; i > intCode.toString().length(); i--) {

            unitCode = "0" + unitCode;

        }
        return new ServiceVO<>(SuccessCode.SUCCESS_CODE, SuccessCode.SUCCESS_MESS, unitCode);
    }

    @Override
    public Map<String, Object> listInventory(Integer page, Integer rows, String codeOrName, Integer goodsTypeId) {
        PageHelper.startPage(page, rows);
        List<Goods> list = goodsDao.queryStock(codeOrName, goodsTypeId);
        PageInfo<Goods> pageInfo = new PageInfo<Goods>(list);
        long total = pageInfo.getTotal();
        List<Goods> goods = pageInfo.getList();
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("rows", goods);
        return result;
    }

    @Override

    public ServiceVO deleteGoodsById(Integer goodsId) {

        Integer rows = goodsDao.deleteGoodsById(goodsId);
        if (rows==1){
            return new ServiceVO(SuccessCode.SUCCESS_CODE,SuccessCode.SUCCESS_MESS);
        }else{
            return new ServiceVO(-1,"商品有入库、有进货和销售单据");
        }
    }

    @Override
    public Map<String, Object> getNoInventoryQuantity(Integer page, Integer rows, String nameOrCode) {
        Map<String, Object> pageMap=new HashMap<>();

        PageHelper.startPage(page,rows);
        List<Goods>goods=goodsDao.getNoInventoryQuantityByPage((page-1)*rows,rows,nameOrCode);
        PageInfo pageInfo=new PageInfo(goods);
        pageMap.put("total",pageInfo.getTotal());
        pageMap.put("rows",pageInfo.getList());
        return pageMap;
    }

    @Override
    public Map<String, Object> getHasInventoryQuantity(Integer page, Integer rows, String nameOrCode) {
        Map<String, Object> pageMap=new HashMap<>();

        PageHelper.startPage(page,rows);
        List<Goods>goods=goodsDao.getHasInventoryQuantity((page-1)*rows,rows,nameOrCode);
        PageInfo pageInfo=new PageInfo(goods);
        pageMap.put("total",pageInfo.getTotal());
        pageMap.put("rows",pageInfo.getList());
        return pageMap;
    }

    public Map<String, Object> queryDamageListGoods(Integer damageListId) {
        HashMap<String, Object> map = new HashMap<>();
        List<DamageListGoods> damageListGoodsList = this.goodsDao.queryDamageListGoods(damageListId);
        map.put("rows", damageListGoodsList);
        return map;
    }


    @Override
    public Map<String, Object> queryOverflowList(String sTime, String eTime) {
        HashMap<String, Object> map = new HashMap<>();
        List<OverflowList> overflowLists = this.goodsDao.queryOverflowList(sTime, eTime);
        map.put("rows", overflowLists);
        return map;
    }

    @Override
    public Map<String, Object> queryOverflowListGoods(Integer overflowListId) {
        HashMap<String, Object> map = new HashMap<>();
        List<OverflowListGoods> overflowListGoods = this.goodsDao.queryOverflowListGoods(overflowListId);
        map.put("rows", overflowListGoods);
        return map;
    }





    @Override
    public Map<String, Object> list(Integer page, Integer rows, String goodsName, Integer goodsTypeId) {
        PageHelper.startPage(page,rows);
        List<Goods> list = goodsDao.queryGoods(goodsName,goodsTypeId);
        PageInfo<Goods> pageInfo = new PageInfo<Goods>(list);
        long total = pageInfo.getTotal();
        List<Goods> goods = pageInfo.getList();
        Map<String, Object> result = new HashMap<>();
        result.put("total",total);
        result.put("rows",goods);

        return result;

    }

    @Override
    public Integer saveStock(Integer goodsId, Integer inventoryQuantity, Double purchasingPrice) {
        Goods goods = this.goodsDao.queryGoodsById(goodsId);
        if(goods == null){
            return 0;
        }
        goods.setInventoryQuantity(inventoryQuantity);
        goods.setPurchasingPrice(purchasingPrice);
        return this.goodsDao.updateGoods(goods);
    }

    @Override
    public Integer deleteStock(Integer goodsId) {
        Goods goods = this.goodsDao.queryGoodsById(goodsId);
        if(goods == null){
            return -1;
        }
        if(goods.getState() != 0){
            return goods.getState();
        }
        goods.setInventoryQuantity(-1);
        this.goodsDao.updateGoods(goods);
        return 0;
    }

    public List<GoodsTypeVo> queryAllGoodsType() {

        //方法3.1
        List<GoodsType> goodsTypes = goodsDao.queryAllGoodsTypeByPid(-1);
        List<GoodsTypeVo> collect = getGoodsTypeVos(goodsTypes);
        return collect;
    }

    @Override
    public List<Unit> queryUnitList() {
        List<Unit> unitList = goodsDao.queryUnitList();
        return unitList;
    }


    @Transactional
    @Override
    public void saveOrUpdateGoods(Goods goods, Integer goodsId) {

        if (goods.getState() == null) {
            goods.setState(0);
        }
        if (goods.getInventoryQuantity() == null) {
            goods.setInventoryQuantity(0);
        }

        if (goodsId != null) {
            Integer i = goodsDao.updateGoods(goods);

            if (i == 0) {
                throw new RuntimeException("添加失败");
            }


        } else {
            Integer i = goodsDao.saveGoods(goods);
            if (i == 0) {
                throw new RuntimeException("修改失败");
            }
        }

    }

    @Override
    public Map<String, Object> queryDamageListGoodsByTime(String sTime, String eTime) {
        List<DamageList> damageLists = goodsDao.queryDamageListGoodsByTime(sTime,eTime);
        Map<String, Object> result = new HashMap<>();
        result.put("rows",damageLists);
        return result;
    }

    @Override
    public Map<String, Object> listAlarm() {
        List<Goods> list = goodsDao.queryListAlarm();
        Map<String,Object> result = new HashMap<>();
        result.put("rows",list);
        return result;
    }

    @Override
    public void saveOverflowListGoods(String overflowNumber, OverflowList overflowList, List<OverflowListGoods> overflowListGoods) {
        if (overflowList != null && !StringUtils.isEmpty(overflowList)){
            overflowList.setOverflowNumber(overflowNumber);
            goodsDao.saveOverflowList(overflowList);
            if (!CollectionUtils.isEmpty(overflowListGoods)) {
                overflowListGoods.forEach(goods -> {
                    goods.setOverflowListId(overflowList.getOverflowListId());
                    goodsDao.saveOverflowListGoods(goods);
                });
            }
        }

    }

    private List<GoodsTypeVo> getGoodsTypeVos(List<GoodsType> goodsTypes) {
        return goodsTypes.stream().map(goodsType -> {
            GoodsTypeVo vo = new GoodsTypeVo();
            vo.setId(goodsType.getGoodsTypeId());
            vo.setText(goodsType.getGoodsTypeName());
            switch (goodsType.getGoodsTypeState()) {
                case 1:
                    vo.setState("closed");
                default:
                    vo.setState("open");
            }

            vo.setIconCls("goods-type");
            HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
            stringIntegerHashMap.put("state", goodsType.getGoodsTypeState());
            vo.setAttributes(stringIntegerHashMap);

            if (goodsType.getGoodsTypeId() != null) {
                List<GoodsType> goodsTypes1 = goodsDao.queryAllGoodsTypeByPid(goodsType.getGoodsTypeId());

                List<GoodsTypeVo> goodsTypeVos = this.getGoodsTypeVos(goodsTypes1);
                vo.setChildren(goodsTypeVos);
            }
            return vo;
        }).collect(Collectors.toList());
    }



}
