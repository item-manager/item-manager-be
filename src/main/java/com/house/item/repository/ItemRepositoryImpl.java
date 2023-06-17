package com.house.item.repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.house.item.domain.ConsumableItemDTO;
import com.house.item.domain.ConsumableSearch;
import com.house.item.domain.EquipmentSearch;
import com.house.item.entity.Item;
import com.house.item.entity.Location;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryCustom {

    public static final String SELECT_FROM_JPQL = "select i from Item i";
    private final EntityManager em;

    @Override
    public List<Item> findByRoom(Location room) {
        String jpql = SELECT_FROM_JPQL +
            " join i.location p" +
            " join p.room r" +
            " where r.locationNo = :roomNo";
        return em.createQuery(jpql, Item.class)
            .setParameter("roomNo", room.getLocationNo())
            .getResultList();
    }

    //소모품 관리
    @Override
    public List<ConsumableItemDTO> findConsumableByNameAndLabel(ConsumableSearch consumableSearch) {
        String sql = """
            SELECT DISTINCT I.*,
                LATEST_PURCHASE as latestPurchase,
                LATEST_CONSUME as latestConsume
            FROM item I
            LEFT JOIN (
                SELECT IQL.ITEM_NO,
                    MAX(IQL.DATE) AS LATEST_PURCHASE
                FROM item_quantity_log IQL
                WHERE IQL.TYPE = 'PURCHASE'
                GROUP BY IQL.ITEM_NO
            ) AS PURCHASE ON I.ITEM_NO = PURCHASE.ITEM_NO
            LEFT JOIN (
                SELECT IQL.ITEM_NO
                    , MAX(IQL.DATE) AS LATEST_CONSUME
                FROM item_quantity_log IQL
                WHERE IQL.TYPE = 'CONSUME'
                    GROUP BY IQL.ITEM_NO
                ) AS CONSUME ON I.ITEM_NO = CONSUME.ITEM_NO
                   """;

        if (consumableSearch.getLabelNos() != null && !consumableSearch.getLabelNos().isEmpty()) {
            sql += """
                JOIN (
                    SELECT IL.ITEM_NO
                    FROM item_label IL
                    WHERE IL.LABEL_NO IN :labelNos
                    GROUP BY IL.ITEM_NO
                    HAVING COUNT(IL.ITEM_NO) = :labelNosSize
                ) HAVE_LABEL ON I.ITEM_NO = HAVE_LABEL.ITEM_NO
                """;
        }

        sql += "WHERE I.USER_NO = :userNo AND I.TYPE = 'CONSUMABLE' \n";

        if (StringUtils.hasText(consumableSearch.getName())) {
            sql += "AND I.NAME LIKE '%" + consumableSearch.getName() + "%' \n";
        }

        if (consumableSearch.getOrderBy() != null) {
            sql += "ORDER BY " + consumableSearch.getOrderBy() + " " + consumableSearch.getSort() + ", I.ITEM_NO ASC";
        } else {
            sql += "ORDER BY I.ITEM_NO " + consumableSearch.getSort();
        }

        Query query = em.createNativeQuery(sql, "ConsumableItemMapping")
                .setParameter("userNo", consumableSearch.getUserNo());

        if (consumableSearch.getLabelNos() != null && !consumableSearch.getLabelNos().isEmpty()) {
            query.setParameter("labelNos", consumableSearch.getLabelNos());
            query.setParameter("labelNosSize", consumableSearch.getLabelNos().size());
        }

        List<Object[]> resultList = query.setFirstResult((consumableSearch.getPage() - 1) * consumableSearch.getSize()) //조회 시작 위치(0부터 시작)
                .setMaxResults(consumableSearch.getSize())
                .getResultList();

        List<ConsumableItemDTO> consumableItemDTOS = new ArrayList<>();
        for (int i = 0; i < resultList.size(); i++) {
            consumableItemDTOS.add((ConsumableItemDTO) resultList.get(i)[1]);
            consumableItemDTOS.get(i).setItem((Item) resultList.get(i)[0]);
        }

        return consumableItemDTOS;
    }

    @Override
    public int getConsumableRowCount(ConsumableSearch consumableSearch) {
        String sql = """
            SELECT COUNT(ITEM_NO)
            FROM (
                SELECT DISTINCT I.*,
                    LATEST_PURCHASE as latestPurchase,
                    LATEST_CONSUME as latestConsume
                FROM item I
                LEFT JOIN (
                    SELECT IQL.ITEM_NO,
                        MAX(IQL.DATE) AS LATEST_PURCHASE
                    FROM item_quantity_log IQL
                    WHERE IQL.TYPE = 'PURCHASE'
                    GROUP BY IQL.ITEM_NO
                ) AS PURCHASE ON I.ITEM_NO = PURCHASE.ITEM_NO
                LEFT JOIN (
                    SELECT IQL.ITEM_NO
                        , MAX(IQL.DATE) AS LATEST_CONSUME
                    FROM item_quantity_log IQL
                    WHERE IQL.TYPE = 'CONSUME'
                        GROUP BY IQL.ITEM_NO
                    ) AS CONSUME ON I.ITEM_NO = CONSUME.ITEM_NO
                """;

        if (consumableSearch.getLabelNos() != null && !consumableSearch.getLabelNos().isEmpty()) {
            sql += """
                    JOIN (
                        SELECT IL.ITEM_NO
                        FROM item_label IL
                        WHERE IL.LABEL_NO IN :labelNos
                        GROUP BY IL.ITEM_NO
                        HAVING COUNT(IL.ITEM_NO) = :labelNosSize
                    ) HAVE_LABEL ON I.ITEM_NO = HAVE_LABEL.ITEM_NO
                """;
        }

        sql += "    WHERE I.USER_NO = :userNo AND I.TYPE = 'CONSUMABLE' \n";

        if (StringUtils.hasText(consumableSearch.getName())) {
            sql += "    AND I.NAME LIKE '%" + consumableSearch.getName() + "%' \n";
        }

        sql += ") consumable_items";

        Query query = em.createNativeQuery(sql)
                .setParameter("userNo", consumableSearch.getUserNo());

        if (consumableSearch.getLabelNos() != null && !consumableSearch.getLabelNos().isEmpty()) {
            query.setParameter("labelNos", consumableSearch.getLabelNos());
            query.setParameter("labelNosSize", consumableSearch.getLabelNos().size());
        }

        return ((BigInteger) query.getSingleResult()).intValue();
    }

    //비품 관리
    @Override
    public List<Item> findEquipmentByNameAndLabelAndPlace(EquipmentSearch equipmentSearch) {
        String jpql = """
                select distinct i from Item i
                join i.user u
                join fetch i.location p
                join fetch p.room r \n
                """;

        if (equipmentSearch.getLabels() != null && !equipmentSearch.getLabels().isEmpty()) {
            jpql += "join i.itemLabels il \n";
        }

        jpql += " where u.userNo = :userNo AND i.type = 'EQUIPMENT'";

        if (StringUtils.hasText(equipmentSearch.getName())) {
            jpql += " and i.name like '%" + equipmentSearch.getName() + "%'";
        }

        if (equipmentSearch.getPlaceNos() != null && !equipmentSearch.getPlaceNos().isEmpty()) {
            jpql += " and p.locationNo in :placeNos";
        }

        if (equipmentSearch.getLabels() != null && !equipmentSearch.getLabels().isEmpty()) {
            jpql += " and il.label in :labels group by i having count(i) = :labelsSize";
        }

        jpql += " order by i.priority desc, i.itemNo";

        TypedQuery<Item> query = em.createQuery(jpql, Item.class)
                .setParameter("userNo", equipmentSearch.getUserNo());

        if (equipmentSearch.getLabels() != null && !equipmentSearch.getLabels().isEmpty()) {
            query.setParameter("labels", equipmentSearch.getLabels())
                    .setParameter("labelsSize", (long) equipmentSearch.getLabels().size());
        }
        if (equipmentSearch.getPlaceNos() != null && !equipmentSearch.getPlaceNos().isEmpty()) {
            query.setParameter("placeNos", equipmentSearch.getPlaceNos());
        }

        return query.setFirstResult((equipmentSearch.getPage() - 1) * equipmentSearch.getSize()) //조회 시작 위치(0부터 시작)
                .setMaxResults(equipmentSearch.getSize())
                .getResultList();
    }

    @Override
    public int getEquipmentRowCount(EquipmentSearch equipmentSearch) {
        String jpql = """
                select distinct i.itemNo from Item i
                join i.user u \n
                """;

        if (equipmentSearch.getPlaceNos() != null && !equipmentSearch.getPlaceNos().isEmpty()) {
            jpql += "join i.location p \n";
        }
        if (equipmentSearch.getLabels() != null && !equipmentSearch.getLabels().isEmpty()) {
            jpql += "join i.itemLabels il \n";
        }

        jpql += " where u.userNo = :userNo AND i.type = 'EQUIPMENT'";

        if (StringUtils.hasText(equipmentSearch.getName())) {
            jpql += " and i.name like '%" + equipmentSearch.getName() + "%'";
        }

        if (equipmentSearch.getPlaceNos() != null && !equipmentSearch.getPlaceNos().isEmpty()) {
            jpql += " and p.locationNo in :placeNos";
        }

        if (equipmentSearch.getLabels() != null && !equipmentSearch.getLabels().isEmpty()) {
            jpql += " and il.label in :labels group by i having count(i) = :labelsSize";
        }

        TypedQuery<Long> query = em.createQuery(jpql, Long.class)
                .setParameter("userNo", equipmentSearch.getUserNo());

        if (equipmentSearch.getLabels() != null && !equipmentSearch.getLabels().isEmpty()) {
            query.setParameter("labels", equipmentSearch.getLabels())
                    .setParameter("labelsSize", (long) equipmentSearch.getLabels().size());
        }
        if (equipmentSearch.getPlaceNos() != null && !equipmentSearch.getPlaceNos().isEmpty()) {
            query.setParameter("placeNos", equipmentSearch.getPlaceNos());
        }

        return query.getResultList().size();
    }
}
