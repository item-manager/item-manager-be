package com.house.item.repository;

import static com.house.item.entity.QItem.*;
import static com.house.item.entity.QItemLabel.*;
import static com.house.item.entity.QLocation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.house.item.domain.ConsumableItemDTO;
import com.house.item.domain.ConsumableSearch;
import com.house.item.domain.EquipmentSearch;
import com.house.item.domain.QConsumableItemDTO;
import com.house.item.entity.Item;
import com.house.item.entity.ItemType;
import com.house.item.entity.Location;
import com.house.item.entity.QItemQuantityLog;
import com.house.item.entity.QLocation;
import com.house.item.entity.QuantityType;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ItemRepositoryImpl implements ItemRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QLocation place = new QLocation("place");

	@Override
	public List<Item> findByRoom(Location room) {
		return queryFactory.selectFrom(item)
			.leftJoin(item.location, place)
			.where(place.room.eq(room))
			.fetch();
	}

	@Override
	public Page<ConsumableItemDTO> findConsumableByNameAndLabel(ConsumableSearch consumableSearch) {
		QItemQuantityLog purchase = new QItemQuantityLog("purchase");
		QItemQuantityLog consume = new QItemQuantityLog("consume");

		// content
		JPAQuery<ConsumableItemDTO> itemJPAQuery = queryFactory.select(new QConsumableItemDTO(
				item,
				purchase.date.max().as("latestPurchase"),
				consume.date.max().as("latestConsume")
			))
			.from(item)
			.leftJoin(purchase).on(
				item.itemNo.eq(purchase.item.itemNo)
					.and(purchase.type.eq(QuantityType.PURCHASE))) // item 구매 기록 join
			.leftJoin(consume).on(
				item.eq(consume.item)
					.and(consume.type.eq(QuantityType.CONSUME))) // item 사용 기록 join
			.leftJoin(item.location, place).fetchJoin()
			.leftJoin(place.room, location).fetchJoin()
			.groupBy(item);

		// search label
		if (consumableSearch.getLabelNos() != null && !consumableSearch.getLabelNos().isEmpty()) {
			// item label join 1:n
			itemJPAQuery
				.join(item.itemLabels, itemLabel)
				.where(itemLabel.label.labelNo.in(consumableSearch.getLabelNos()))
				.having(itemLabel.itemLabelNo.countDistinct().eq((long)consumableSearch.getLabelNos().size()));
		}
		itemJPAQuery
			.where(
				place.user.userNo.eq(consumableSearch.getUserNo()),
				item.type.eq(ItemType.CONSUMABLE),
				likeName(consumableSearch.getName()),
				loeThreshold(consumableSearch.isCheckThreshold())
			);

		itemJPAQuery.orderBy(consumableOrderBySort(consumableSearch.getPageable().getSort()))
			.offset(consumableSearch.getPageable().getOffset())
			.limit(consumableSearch.getPageable().getPageSize());
		List<ConsumableItemDTO> items = itemJPAQuery.fetch();

		// count
		JPAQuery<Long> countQuery = queryFactory.select(item.itemNo.count()).from(item);
		if (consumableSearch.getLabelNos() != null && !consumableSearch.getLabelNos().isEmpty()) {
			countQuery
				.join(item.itemLabels, itemLabel)
				.where(itemLabel.label.labelNo.in(consumableSearch.getLabelNos()))
				.groupBy(item)
				.having(itemLabel.itemLabelNo.countDistinct().eq((long)consumableSearch.getLabelNos().size()));
		}
		countQuery
			.join(item.location, place)
			.where(
				place.user.userNo.eq(consumableSearch.getUserNo()),
				item.type.eq(ItemType.CONSUMABLE),
				likeName(consumableSearch.getName()),
				loeThreshold(consumableSearch.isCheckThreshold())
			);

		return PageableExecutionUtils.getPage(items, consumableSearch.getPageable(), countQuery::fetchOne);
	}

	@Override
	public Page<Item> findEquipmentByNameAndLabelAndPlace(EquipmentSearch equipmentSearch) {
		QLocation room = new QLocation("room");

		// content
		JPAQuery<Item> itemJPAQuery = queryFactory.selectFrom(item);
		// search label
		if (equipmentSearch.getLabelNos() != null && !equipmentSearch.getLabelNos().isEmpty()) {
			// item label join 1:n
			itemJPAQuery
				.join(item.itemLabels, itemLabel)
				.where(itemLabel.label.labelNo.in(equipmentSearch.getLabelNos()))
				.groupBy(item)
				.having(itemLabel.itemLabelNo.countDistinct().eq((long)equipmentSearch.getLabelNos().size()));
		}
		itemJPAQuery
			.leftJoin(item.location, place).fetchJoin() // place fetch join
			.leftJoin(place.room, room).fetchJoin() // room fetch join
			.where(
				place.user.userNo.eq(equipmentSearch.getUserNo()),
				item.type.eq(ItemType.EQUIPMENT),
				likeName(equipmentSearch.getName()),
				inPlaceNos(equipmentSearch.getPlaceNos())
			);
		itemJPAQuery
			.orderBy(item.priority.desc())
			.offset(equipmentSearch.getPageable().getOffset())
			.limit(equipmentSearch.getPageable().getPageSize());
		List<Item> items = itemJPAQuery.fetch();

		// count
		JPAQuery<Long> countQuery = queryFactory.select(item.itemNo.count()).from(item);
		if (equipmentSearch.getLabelNos() != null && !equipmentSearch.getLabelNos().isEmpty()) {
			countQuery
				.join(item.itemLabels, itemLabel)
				.where(itemLabel.label.labelNo.in(equipmentSearch.getLabelNos()))
				.groupBy(item)
				.having(itemLabel.itemLabelNo.countDistinct().eq((long)equipmentSearch.getLabelNos().size()));
		}
		countQuery
			.join(item.location, place)
			.where(
				place.user.userNo.eq(equipmentSearch.getUserNo()),
				item.type.eq(ItemType.EQUIPMENT),
				likeName(equipmentSearch.getName()),
				inPlaceNos(equipmentSearch.getPlaceNos())
			);

		return PageableExecutionUtils.getPage(items, equipmentSearch.getPageable(), countQuery::fetchOne);
	}

	private BooleanExpression likeName(String name) {
		return StringUtils.hasText(name) ? item.name.like("%" + name + "%") : null;
	}

	private BooleanExpression inPlaceNos(List<Long> placeNos) {
		return placeNos != null && !placeNos.isEmpty() ? item.location.locationNo.in(placeNos) : null;
	}

	private BooleanExpression loeThreshold(boolean checkThreshold) {
		return checkThreshold ? item.quantity.loe(item.threshold) : null;
	}

	private OrderSpecifier[] consumableOrderBySort(Sort sort) {
		List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

		PathBuilder itemPathBuilder = new PathBuilder<>(item.getType(), item.getMetadata());
		PathBuilder pathBuilder;
		for (Sort.Order order : sort) {
			Order sortBy = order.isAscending() ? Order.ASC : Order.DESC;

			if (order.getProperty().startsWith("latest")) {
				pathBuilder = new PathBuilder<>(LocalDateTime.class, order.getProperty());
			} else {
				pathBuilder = itemPathBuilder.get(order.getProperty());
			}
			orderSpecifiers.add(new OrderSpecifier(sortBy, pathBuilder));
		}

		if (sort.stream().noneMatch(order -> order.getProperty().equals("priority"))) {
			orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, itemPathBuilder.get("priority")));
		}

		return orderSpecifiers.toArray(OrderSpecifier[]::new);
	}
}
