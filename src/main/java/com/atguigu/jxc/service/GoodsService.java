package com.atguigu.jxc.service;

import com.atguigu.jxc.domain.ServiceVO;
import com.atguigu.jxc.entity.Goods;
import com.atguigu.jxc.entity.OverflowList;
import com.atguigu.jxc.entity.OverflowListGoods;
import com.atguigu.jxc.entity.Unit;
import com.atguigu.jxc.entity.vo.GoodsTypeVo;

import java.util.List;
import java.util.Map;

public interface GoodsService {


    ServiceVO getCode();


    Map<String, Object> listInventory(Integer page, Integer rows, String codeOrName, Integer goodsTypeId);


    ServiceVO deleteGoodsById(Integer goodsId);

    Map<String, Object> getNoInventoryQuantity(Integer page, Integer rows, String nameOrCode);

    Map<String, Object> getHasInventoryQuantity(Integer page, Integer rows, String nameOrCode);


    Map<String, Object> queryDamageListGoods(Integer damageListId);

    Map<String, Object> queryOverflowList(String sTime, String eTime);

    Map<String, Object> queryOverflowListGoods(Integer overflowListId);


    Map<String, Object> list(Integer page, Integer rows, String goodsName, Integer goodsTypeId);

    List<GoodsTypeVo> queryAllGoodsType();

    List<Unit> queryUnitList();


    void saveOrUpdateGoods(Goods goods, Integer goodsId);

    Integer saveStock(Integer goodsId, Integer inventoryQuantity, Double purchasingPrice);

    Integer deleteStock(Integer goodsId);

    Map<String, Object> queryDamageListGoodsByTime(String sTime, String eTime);

    Map<String, Object> listAlarm();

    void saveOverflowListGoods(String overflowNumber, OverflowList overflowList, List<OverflowListGoods> overflowListGoods);
}
