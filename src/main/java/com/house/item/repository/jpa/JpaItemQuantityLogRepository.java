package com.house.item.repository.jpa;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.house.item.domain.QuantityLogSearch;
import com.house.item.domain.QuantityLogSumDto;
import com.house.item.domain.QuantityLogSumSearch;
import com.house.item.entity.ItemQuantityLog;
import com.house.item.repository.ItemQuantityLogRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JpaItemQuantityLogRepository implements ItemQuantityLogRepository {
	private final EntityManager em;

	@Override
	public void save(ItemQuantityLog itemQuantityLog) {
		em.persist(itemQuantityLog);
	}

	@Override
	public Optional<ItemQuantityLog> findByItemQuantityLogNoAndUserNo(Long itemQuantityLogNo, Long userNo) {
		String jpql = "select l from ItemQuantityLog l" +
			" join l.item i" +
			" join i.user u" +
			" where l.itemQuantityLogNo = :logNo and u.userNo = :userNo";

		List<ItemQuantityLog> logs = em.createQuery(jpql, ItemQuantityLog.class)
			.setParameter("logNo", itemQuantityLogNo)
			.setParameter("userNo", userNo)
			.getResultList();
		return logs.stream().findAny();
	}

	@Override
	public List<ItemQuantityLog> findByItemNoAndTypeAndYearAndMonth(QuantityLogSearch quantityLogSearch) {
		String jpql = "select l from ItemQuantityLog l" +
			" where l.item = :item";

		if (quantityLogSearch.getType() != null) {
			jpql += " and l.type = :type";
		}
		if (quantityLogSearch.getYear() != null) {
			jpql += " and YEAR(l.date) = :year";
		}
		if (quantityLogSearch.getMonth() != null) {
			jpql += " and MONTH(l.date) = :month";
		}

		jpql += " order by " + quantityLogSearch.getOrderBy() + " " + quantityLogSearch.getSort();
		if (quantityLogSearch.getOrderBy().equals("price")) {
			jpql += " NULLS LAST";
		}
		if (!quantityLogSearch.getOrderBy().equals("date")) {
			jpql += ", l.date desc";
		}

		TypedQuery<ItemQuantityLog> query = em.createQuery(jpql, ItemQuantityLog.class)
			.setParameter("item", quantityLogSearch.getItem());

		if (quantityLogSearch.getType() != null) {
			query.setParameter("type", quantityLogSearch.getType());
		}
		if (quantityLogSearch.getYear() != null) {
			query.setParameter("year", quantityLogSearch.getYear());
		}
		if (quantityLogSearch.getMonth() != null) {
			query.setParameter("month", quantityLogSearch.getMonth());
		}

		return query.setFirstResult((quantityLogSearch.getPage() - 1) * quantityLogSearch.getSize()) //조회 시작 위치(0부터 시작)
			.setMaxResults(quantityLogSearch.getSize())
			.getResultList();
	}

	@Override
	public Long getLogsByItemNoRowCount(QuantityLogSearch quantityLogSearch) {
		String jpql = "select count(l) from ItemQuantityLog l" +
			" where l.item = :item";

		if (quantityLogSearch.getType() != null) {
			jpql += " and l.type = :type";
		}
		if (quantityLogSearch.getYear() != null) {
			jpql += " and YEAR(l.date) = :year";
		}
		if (quantityLogSearch.getMonth() != null) {
			jpql += " and MONTH(l.date) = :month";
		}

		TypedQuery<Long> query = em.createQuery(jpql, Long.class)
			.setParameter("item", quantityLogSearch.getItem());

		if (quantityLogSearch.getType() != null) {
			query.setParameter("type", quantityLogSearch.getType());
		}
		if (quantityLogSearch.getYear() != null) {
			query.setParameter("year", quantityLogSearch.getYear());
		}
		if (quantityLogSearch.getMonth() != null) {
			query.setParameter("month", quantityLogSearch.getMonth());
		}

		return query.getSingleResult();
	}

	@Override
	public List<QuantityLogSumDto> sumByDate(QuantityLogSumSearch quantityLogSumSearch) {
		String jpql = "select new com.house.item.domain.QuantityLogSumDto(YEAR(l.date), l.type, sum(l.count))\n";

		if (quantityLogSumSearch.getYear() != null) {
			jpql = "select new com.house.item.domain.QuantityLogSumDto(MONTH(l.date), l.type, sum(l.count))\n";
		}

		jpql += """
			from ItemQuantityLog l
			where l.item = :item
			""";

		if (quantityLogSumSearch.getType() != null) {
			jpql += " and l.type = :type";
		}

		if (quantityLogSumSearch.getYear() != null) {
			jpql += """
				 and YEAR(l.date) = :year
				group by MONTH(l.date), l.type
				""";
		} else {
			jpql += " group by YEAR(l.date), l.type";
		}

		TypedQuery<QuantityLogSumDto> query = em.createQuery(jpql, QuantityLogSumDto.class)
			.setParameter("item", quantityLogSumSearch.getItem());

		if (quantityLogSumSearch.getYear() != null) {
			query.setParameter("year", quantityLogSumSearch.getYear());
		}

		if (quantityLogSumSearch.getType() != null) {
			query.setParameter("type", quantityLogSumSearch.getType());
		}

		return query.getResultList();
	}

	@Override
	public void delete(ItemQuantityLog itemQuantityLog) {
		em.remove(itemQuantityLog);
	}
}
