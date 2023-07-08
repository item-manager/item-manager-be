package com.house.item.repository;

import static com.house.item.entity.QItemQuantityLog.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import com.house.item.domain.QQuantityLogDTO;
import com.house.item.domain.QQuantityLogSumDto;
import com.house.item.domain.QuantityLogDTO;
import com.house.item.domain.QuantityLogSearch;
import com.house.item.domain.QuantityLogSumDto;
import com.house.item.domain.QuantityLogSumSearch;
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
	public Page<QuantityLogDTO> findByItemNoAndTypeAndYearAndMonth(QuantityLogSearch quantityLogSearch) {
		List<QuantityLogDTO> logs = queryFactory.select(
				new QQuantityLogDTO(
					itemQuantityLog,
					itemQuantityLog.price.divide(itemQuantityLog.count).as("unitPrice")
				)
			)
			.from(itemQuantityLog)
			.where(
				itemQuantityLog.item.itemNo.eq(quantityLogSearch.getItem().getItemNo()),
				eqType(quantityLogSearch.getType()),
				eqDateByYear(quantityLogSearch.getYear()),
				eqDateByMonth(quantityLogSearch.getMonth())
			)
			.orderBy(logOrderBySort(quantityLogSearch.getPageable().getSort()))
			.offset(quantityLogSearch.getPageable().getOffset())
			.limit(quantityLogSearch.getPageable().getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory.select(itemQuantityLog.itemQuantityLogNo.count())
			.from(itemQuantityLog)
			.where(
				itemQuantityLog.item.itemNo.eq(quantityLogSearch.getItem().getItemNo()),
				eqType(quantityLogSearch.getType()),
				eqDateByYear(quantityLogSearch.getYear()),
				eqDateByMonth(quantityLogSearch.getMonth())
			);

		return PageableExecutionUtils.getPage(logs, quantityLogSearch.getPageable(), countQuery::fetchOne);
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

	private OrderSpecifier[] logOrderBySort(Sort sort) {
		List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

		PathBuilder logPathBuilder = new PathBuilder(itemQuantityLog.getType(), itemQuantityLog.getMetadata());
		for (Sort.Order order : sort) {
			Order sortBy = order.isAscending() ? Order.ASC : Order.DESC;

			if (order.getProperty().equals("price")) {
				PathBuilder<Integer> unitPrice = new PathBuilder<>(Integer.class, "unitPrice");
				orderSpecifiers.add(new OrderSpecifier(sortBy, unitPrice, OrderSpecifier.NullHandling.NullsLast));
			} else {
				orderSpecifiers.add(new OrderSpecifier(sortBy, logPathBuilder.get(order.getProperty()),
					OrderSpecifier.NullHandling.NullsLast));
			}
		}

		if (sort.stream().noneMatch(order -> order.getProperty().equals("date"))) {
			orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, logPathBuilder.get("date")));
		}

		return orderSpecifiers.toArray(OrderSpecifier[]::new);
	}
}
