package com.house.item.repository;

import static com.house.item.entity.QItemQuantityLog.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.house.item.domain.QQuantityLogSumDto;
import com.house.item.domain.QuantityLogSearch;
import com.house.item.domain.QuantityLogSumDto;
import com.house.item.domain.QuantityLogSumSearch;
import com.house.item.entity.ItemQuantityLog;
import com.house.item.entity.QuantityType;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemQuantityLogRepositoryImpl implements ItemQuantityLogRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<ItemQuantityLog> findByItemNoAndTypeAndYearAndMonth(QuantityLogSearch quantityLogSearch) {
		Pageable pageable = PageRequest.of(quantityLogSearch.getPage() - 1, quantityLogSearch.getSize());

		List<ItemQuantityLog> logs = queryFactory.selectFrom(itemQuantityLog)
			.where(
				itemQuantityLog.item.itemNo.eq(quantityLogSearch.getItem().getItemNo()),
				eqType(quantityLogSearch.getType()),
				eqDateByYear(quantityLogSearch.getYear()),
				eqDateByMonth(quantityLogSearch.getMonth())
			)
			.orderBy(logOrderBy(quantityLogSearch.getOrderBy(), quantityLogSearch.getSort()))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory.select(itemQuantityLog.itemQuantityLogNo.count())
			.from(itemQuantityLog)
			.where(
				itemQuantityLog.item.itemNo.eq(quantityLogSearch.getItem().getItemNo()),
				eqType(quantityLogSearch.getType()),
				eqDateByYear(quantityLogSearch.getYear()),
				eqDateByMonth(quantityLogSearch.getMonth())
			);

		return PageableExecutionUtils.getPage(logs, pageable, countQuery::fetchOne);
	}

	@Override
	public List<QuantityLogSumDto> sumByDate(QuantityLogSumSearch quantityLogSumSearch) {
		NumberExpression<Integer> date = itemQuantityLog.date.month();
		if (quantityLogSumSearch.getYear() == null) {
			date = itemQuantityLog.date.year();
		}

		return queryFactory.select(
				new QQuantityLogSumDto(
					date,
					itemQuantityLog.type,
					itemQuantityLog.count.sum()
				)
			)
			.from(itemQuantityLog)
			.where(
				itemQuantityLog.item.itemNo.eq(quantityLogSumSearch.getItem().getItemNo()),
				eqType(quantityLogSumSearch.getType()),
				eqDateByYear(quantityLogSumSearch.getYear())
			)
			.groupBy(date, itemQuantityLog.type)
			.fetch();
	}

	private BooleanExpression eqType(QuantityType type) {
		return type != null ? itemQuantityLog.type.eq(type) : null;
	}

	private BooleanExpression eqDateByYear(Integer year) {
		return year != null ? itemQuantityLog.date.year().eq(year) : null;
	}

	private BooleanExpression eqDateByMonth(Integer month) {
		return month != null ? itemQuantityLog.date.month().eq(month) : null;
	}

	private OrderSpecifier[] logOrderBy(String order, String sort) {
		Order sortBy = sort.equals("DESC") ? Order.DESC : Order.ASC;

		List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
		PathBuilder pathBuilder = new PathBuilder(ItemQuantityLog.class, "itemQuantityLog");

		orderSpecifiers.add(new OrderSpecifier(sortBy, pathBuilder.get(order), OrderSpecifier.NullHandling.NullsLast));
		if (!order.equals("date")) {
			orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, pathBuilder.get("date")));
		}

		return orderSpecifiers.toArray(OrderSpecifier[]::new);
	}
}
